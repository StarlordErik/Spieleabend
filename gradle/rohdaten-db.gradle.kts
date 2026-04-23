buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.xerial:sqlite-jdbc:3.50.3.0")
    }
}

import org.gradle.api.GradleException
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import kotlin.io.path.absolutePathString
import kotlin.io.path.bufferedReader
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.readText

data class SeedData(
    val games: List<GameSeed>,
) {
    fun normalized(): SeedData =
        copy(games = games.map(GameSeed::normalized).sortedBy(GameSeed::sortKey))

    fun counts(): String =
        buildString {
            append("Spiele=${games.size}")
            append(", Kategorien=${games.sumOf { game -> game.categories.size }}")
            append(", Kartentexte=${games.sumOf { game -> game.categories.sumOf { category -> category.cardTexts.size } }}")
            append(", Lokalisierungen=")
            append(
                games.size +
                    games.sumOf { game -> game.categories.size } +
                    games.sumOf { game -> game.categories.sumOf { category -> category.cardTexts.size } },
            )
            append(", Translationen=")
            append(
                games.sumOf { game -> game.localization.translations.size } +
                    games.sumOf { game -> game.categories.sumOf { category -> category.localization.translations.size } } +
                    games.sumOf { game ->
                        game.categories.sumOf { category ->
                            category.cardTexts.sumOf { cardText -> cardText.localization.translations.size }
                        }
                    },
            )
        }
}

data class GameSeed(
    val localization: LocalizationSeed,
    val texteProKarte: Int,
    val bildDateiname: String?,
    val categories: List<CategorySeed>,
) {
    fun normalized(): GameSeed =
        copy(
            localization = localization.normalized(),
            categories = categories.map(CategorySeed::normalized).sortedBy(CategorySeed::sortKey),
        )

    fun sortKey(): String =
        "${localization.sortKey()}|$texteProKarte|${bildDateiname.orEmpty()}"
}

data class CategorySeed(
    val localization: LocalizationSeed,
    val cardTexts: List<CardTextSeed>,
) {
    fun normalized(): CategorySeed =
        copy(
            localization = localization.normalized(),
            cardTexts = cardTexts.map(CardTextSeed::normalized).sortedBy(CardTextSeed::sortKey),
        )

    fun sortKey(): String = localization.sortKey()
}

data class CardTextSeed(
    val localization: LocalizationSeed,
) {
    fun normalized(): CardTextSeed =
        copy(localization = localization.normalized())

    fun sortKey(): String = localization.sortKey()
}

data class LocalizationSeed(
    val ogSprache: String,
    val translations: Map<String, String>,
) {
    fun normalized(): LocalizationSeed =
        copy(translations = translations.toSortedMap())

    fun sortKey(): String =
        "$ogSprache|${translations.toSortedMap().entries.joinToString("||") { (language, text) -> "$language=$text" }}"
}

data class ParsedMetadata(
    val translations: Map<String, String>,
    val ogSprache: String?,
    val texteProKarte: Int?,
    val bildDateiname: String?,
)

val rawDataDirectory = layout.projectDirectory.dir("rohdaten").asFile.toPath()
val outputDatabase = layout.projectDirectory.file("app/src/main/assets/spieleabend.db").asFile.toPath()
val legacyOutputDatabase = layout.projectDirectory.file("app/src/main/assets/data/seed/spieleabend.db").asFile.toPath()
val roomImplFile =
    layout.projectDirectory.file(
        "app/build/generated/ksp/debug/kotlin/de/impulse/spieleabend/data/SpieleabendDatabase_Impl.kt",
    ).asFile.toPath()

tasks.register("writeRawDataDatabase") {
    group = "data"
    description = "Schreibt die Rohdaten in eine SQLite-Datenbank fuer das App-Seed."
    dependsOn(":app:kspDebugKotlin")

    doLast {
        Class.forName("org.sqlite.JDBC")
        val expected = parseRawData(rawDataDirectory)
        val schemaStatements = extractSchemaStatements(roomImplFile)
        val roomVersion = extractRoomVersion(roomImplFile)

        outputDatabase.parent.createDirectories()
        legacyOutputDatabase.deleteIfExists()
        outputDatabase.deleteIfExists()

        DriverManager.getConnection("jdbc:sqlite:${outputDatabase.absolutePathString()}").use { connection ->
            connection.autoCommit = false
            connection.createStatement().use { statement ->
                statement.execute("PRAGMA foreign_keys = ON")
            }
            schemaStatements.forEach { sql ->
                connection.createStatement().use { statement ->
                    statement.execute(sql)
                }
            }
            writeSeedData(connection, expected)
            connection.createStatement().use { statement ->
                statement.execute("PRAGMA user_version = $roomVersion")
            }
            connection.commit()
        }

        logger.lifecycle("Rohdaten-Datenbank geschrieben: ${outputDatabase.absolutePathString()} (${expected.counts()})")
    }
}

tasks.register("verifyRawDataDatabase") {
    group = "verification"
    description = "Prueft, ob die erzeugte Rohdaten-Datenbank die Struktur aus rohdaten widerspiegelt."
    dependsOn("writeRawDataDatabase")

    doLast {
        Class.forName("org.sqlite.JDBC")
        val expected = parseRawData(rawDataDirectory).normalized()
        val roomVersion = extractRoomVersion(roomImplFile)
        val actual =
            DriverManager.getConnection("jdbc:sqlite:${outputDatabase.absolutePathString()}").use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("PRAGMA foreign_keys = ON")
                }
                verifyRoomVersion(connection, roomVersion)
                readSeedData(connection).normalized()
            }

        if (expected != actual) {
            throw GradleException(
                "Die erzeugte Datenbank spiegelt rohdaten nicht korrekt wider.\n" +
                    "Erwartet: ${expected.counts()}\n" +
                    "Gefunden: ${actual.counts()}",
            )
        }

        logger.lifecycle("Rohdaten-Datenbank verifiziert: ${actual.counts()}")
    }
}

fun parseRawData(root: Path): SeedData {
    require(root.exists() && root.isDirectory()) {
        "Rohdaten-Verzeichnis fehlt: ${root.absolutePathString()}"
    }

    return SeedData(games = listDirectories(root).map(::parseGame))
}

fun parseGame(gameDirectory: Path): GameSeed {
    val metadata = parseMetadata(gameDirectory.resolve("SpielMetadaten.yml"))
    val ogSprache = metadata.ogSprache
        ?: throw GradleException("Spiel ${gameDirectory.name} braucht ogSprache in SpielMetadaten.yml.")

    return GameSeed(
        localization =
            LocalizationSeed(
                ogSprache = ogSprache,
                translations = completeTranslations(metadata.translations, ogSprache, "Spiel ${gameDirectory.name}"),
            ),
        texteProKarte = metadata.texteProKarte ?: 1,
        bildDateiname = metadata.bildDateiname,
        categories = listDirectories(gameDirectory).map { categoryDirectory -> parseCategory(categoryDirectory, ogSprache) },
    )
}

fun parseCategory(
    categoryDirectory: Path,
    gameOgSprache: String,
): CategorySeed {
    val metadata = parseMetadata(categoryDirectory.resolve("KategorieMetadaten.yml"))
    val ogSprache = metadata.ogSprache ?: gameOgSprache
    val translationFiles =
        Files.list(categoryDirectory).use { paths ->
            paths
                .filter { path -> Files.isRegularFile(path) }
                .filter { path -> path.name.endsWith(".yml") }
                .filter { path -> path.name != "KategorieMetadaten.yml" }
                .sorted(compareBy<Path> { it.name })
                .toList()
        }

    if (translationFiles.isEmpty()) {
        throw GradleException("Kategorie ${categoryDirectory.name} enthaelt keine Kartentext-Dateien.")
    }

    val translationsByLanguage =
        linkedMapOf<String, Map<Int, String>>().apply {
            translationFiles.forEach { translationFile ->
                put(normalizeLanguage(translationFile.name.removeSuffix(".yml")), parseCardTranslationFile(translationFile))
            }
        }
    val basisIndices = translationsByLanguage["OG"]?.keys ?: translationsByLanguage[ogSprache]?.keys
        ?: throw GradleException(
            "Kategorie ${categoryDirectory.name} braucht eine OG-Datei oder eine Datei fuer $ogSprache.",
        )

    translationsByLanguage.forEach { (language, translations) ->
        val extraIndices = translations.keys - basisIndices
        if (extraIndices.isNotEmpty()) {
            throw GradleException(
                "Kategorie ${categoryDirectory.name} enthaelt in $language.yml Kartentexte ohne Basis: " +
                    extraIndices.sorted().joinToString(),
            )
        }
    }

    return CategorySeed(
        localization =
            LocalizationSeed(
                ogSprache = ogSprache,
                translations = completeTranslations(metadata.translations, ogSprache, "Kategorie ${categoryDirectory.name}"),
            ),
        cardTexts =
            basisIndices.sorted().map { index ->
                val translations =
                    linkedMapOf<String, String>().apply {
                        translationsByLanguage.forEach { (language, texts) ->
                            texts[index]?.takeIf(String::isNotBlank)?.let { put(language, it) }
                        }
                    }

                CardTextSeed(
                    localization =
                        LocalizationSeed(
                            ogSprache = ogSprache,
                            translations = completeTranslations(
                                translations,
                                ogSprache,
                                "Kartentext $index in ${categoryDirectory.name}",
                            ),
                        ),
                )
            },
    )
}

fun parseMetadata(metadataFile: Path): ParsedMetadata {
    if (!metadataFile.exists()) {
        throw GradleException("Metadaten-Datei fehlt: ${metadataFile.absolutePathString()}")
    }

    val translations = linkedMapOf<String, String>()
    var ogSprache: String? = null
    var texteProKarte: Int? = null
    var bildDateiname: String? = null
    var inLocalizationBlock = false

    metadataFile.bufferedReader(Charsets.UTF_8).useLines { lines ->
        lines.forEachIndexed { index, rawLine ->
            val line = rawLine.trimEnd()
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                return@forEachIndexed
            }

            if (!rawLine.startsWith(" ")) {
                inLocalizationBlock = trimmed == "Lokalisierung:"
                if (inLocalizationBlock) {
                    return@forEachIndexed
                }

                val separatorIndex = trimmed.indexOf(':')
                if (separatorIndex <= 0) {
                    throw GradleException("Ungueltige Metadaten-Zeile in ${metadataFile.name}:${index + 1}: $rawLine")
                }

                val key = trimmed.substring(0, separatorIndex).trim()
                val value = parseScalarValue(trimmed.substring(separatorIndex + 1), metadataFile, index + 1)
                when (key) {
                    "ogSprache" -> ogSprache = normalizeLanguage(value)
                    "texteProKarte" -> {
                        texteProKarte = value.toIntOrNull()
                            ?: throw GradleException("texteProKarte in ${metadataFile.name}:${index + 1} muss numerisch sein.")
                    }
                    "bildDateiname" -> bildDateiname = value.ifBlank { null }
                }
                return@forEachIndexed
            }

            if (!inLocalizationBlock) {
                throw GradleException("Unerwartete eingerueckte Zeile in ${metadataFile.name}:${index + 1}: $rawLine")
            }

            val localizedLine = trimmed
            val separatorIndex = localizedLine.indexOf(':')
            if (separatorIndex <= 0) {
                throw GradleException("Ungueltige Lokalisierungs-Zeile in ${metadataFile.name}:${index + 1}: $rawLine")
            }

            val language = normalizeLanguage(localizedLine.substring(0, separatorIndex).trim())
            val text = parseScalarValue(localizedLine.substring(separatorIndex + 1), metadataFile, index + 1)
            if (text.isNotBlank()) {
                translations[language] = text
            }
        }
    }

    if (translations.isEmpty()) {
        throw GradleException("${metadataFile.name} braucht mindestens eine Lokalisierung.")
    }

    return ParsedMetadata(
        translations = translations,
        ogSprache = ogSprache,
        texteProKarte = texteProKarte,
        bildDateiname = bildDateiname,
    )
}

fun parseScalarValue(
    rawValue: String,
    file: Path,
    lineNumber: Int,
): String {
    val trimmed = rawValue.trimStart()
    if (trimmed.isEmpty()) {
        return ""
    }

    return if (trimmed.startsWith('"')) {
        parseQuotedSegments(trimmed, file, lineNumber)
    } else {
        trimmed.trim().removeSurrounding("'")
    }
}

fun parseCardTranslationFile(translationFile: Path): Map<Int, String> {
    val translations = linkedMapOf<Int, String>()

    translationFile.bufferedReader(Charsets.UTF_8).useLines { lines ->
        lines.forEachIndexed { index, rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) {
                return@forEachIndexed
            }

            val separatorIndex = line.indexOf(':')
            if (separatorIndex <= 0) {
                throw GradleException("Ungueltige Kartentext-Zeile in ${translationFile.name}:${index + 1}: $rawLine")
            }

            val key =
                line.substring(0, separatorIndex).trim().toIntOrNull()
                    ?: throw GradleException(
                        "Ungueltiger Kartentext-Index in ${translationFile.name}:${index + 1}: $rawLine",
                    )
            val text = parseQuotedSegments(line.substring(separatorIndex + 1), translationFile, index + 1)
            if (translations.put(key, text) != null) {
                throw GradleException("Doppelter Kartentext-Index $key in ${translationFile.name}.")
            }
        }
    }

    return translations
}

fun parseQuotedSegments(
    rawValue: String,
    file: Path,
    lineNumber: Int,
): String {
    var remaining = rawValue.trimStart()
    val segments = mutableListOf<String>()

    while (remaining.isNotEmpty()) {
        if (!remaining.startsWith('"')) {
            throw GradleException("Kartentext in ${file.name}:$lineNumber muss in Anfuehrungszeichen stehen.")
        }

        val builder = StringBuilder()
        var position = 1
        var escaping = false

        while (position < remaining.length) {
            val current = remaining[position]
            if (escaping) {
                builder.append(unescape(current, remaining, position, file, lineNumber))
                escaping = false
                position += if (current == 'u') 4 else 0
            } else {
                when (current) {
                    '\\' -> escaping = true
                    '"' -> {
                        segments += builder.toString()
                        val trailing = remaining.substring(position + 1).trimStart()
                        if (trailing.isEmpty()) {
                            return segments.joinToString(" ")
                        }
                        if (!trailing.startsWith(',')) {
                            throw GradleException(
                                "Ungueltiger Rest hinter Kartentext in ${file.name}:$lineNumber: $trailing",
                            )
                        }
                        remaining = trailing.drop(1).trimStart()
                        if (remaining.isEmpty()) {
                            return segments.joinToString(" ")
                        }
                        break
                    }
                    else -> builder.append(current)
                }
            }
            position++
        }

        if (position >= remaining.length) {
            throw GradleException("Kartentext in ${file.name}:$lineNumber endet ohne schliessendes Anfuehrungszeichen.")
        }
    }

    return segments.joinToString(" ")
}

fun unescape(
    escapedCharacter: Char,
    value: String,
    position: Int,
    file: Path,
    lineNumber: Int,
): String =
    when (escapedCharacter) {
        '\\' -> "\\"
        '"' -> "\""
        'n' -> "\n"
        'r' -> "\r"
        't' -> "\t"
        'u' -> {
            val endIndex = position + 5
            if (endIndex > value.length) {
                throw GradleException("Ungueltiger Unicode-Escape in ${file.name}:$lineNumber.")
            }
            val hex = value.substring(position + 1, endIndex)
            if (hex.any { character -> !character.isDigit() && character.lowercaseChar() !in 'a'..'f' }) {
                throw GradleException("Ungueltiger Unicode-Escape in ${file.name}:$lineNumber.")
            }
            hex.toInt(16).toChar().toString()
        }
        else -> escapedCharacter.toString()
    }

fun completeTranslations(
    rawTranslations: Map<String, String>,
    ogSprache: String,
    source: String,
): Map<String, String> {
    val translations =
        linkedMapOf<String, String>().apply {
            rawTranslations.forEach { (language, text) ->
                val trimmedText = text.trim()
                if (trimmedText.isNotBlank()) {
                    put(normalizeLanguage(language), trimmedText)
                }
            }
        }

    if (translations.isEmpty()) {
        throw GradleException("$source enthaelt keine nutzbaren Translationen.")
    }

    if (!translations.containsKey("OG")) {
        val ogText = translations[ogSprache]
            ?: throw GradleException("$source enthaelt weder eine OG-Translation noch eine Translation fuer $ogSprache.")
        translations["OG"] = ogText
    }

    return translations.toSortedMap()
}

fun normalizeLanguage(language: String?): String {
    val normalized = language?.trim()?.uppercase()
    if (normalized.isNullOrEmpty()) {
        throw GradleException("Leeres Sprachkuerzel in den Rohdaten.")
    }

    return normalized
}

fun listDirectories(root: Path): List<Path> =
    Files.list(root).use { paths ->
        paths
            .filter { path -> Files.isDirectory(path) }
            .sorted(compareBy<Path> { it.name })
            .toList()
    }

fun extractSchemaStatements(roomImpl: Path): List<String> {
    if (!roomImpl.exists()) {
        throw GradleException("Room-Implementierung fehlt: ${roomImpl.absolutePathString()}")
    }

    val content = roomImpl.readText(Charsets.UTF_8)
    val createBlock =
        Regex("""createAllTables\(connection: [^)]+\) \{(.*?)\n\s*\}""", RegexOption.DOT_MATCHES_ALL)
            .find(content)
            ?.groupValues
            ?.get(1)
            ?: throw GradleException("createAllTables konnte aus ${roomImpl.name} nicht gelesen werden.")

    return Regex("""connection\.execSQL\("((?:\\.|[^"])*)"\)""")
        .findAll(createBlock)
        .map { match -> unescapeKotlinString(match.groupValues[1]) }
        .toList()
        .ifEmpty { throw GradleException("Keine SQL-Statements in createAllTables gefunden.") }
}

fun extractRoomVersion(roomImpl: Path): Int {
    if (!roomImpl.exists()) {
        throw GradleException("Room-Implementierung fehlt: ${roomImpl.absolutePathString()}")
    }

    return Regex("""RoomOpenDelegate\((\d+),\s*"[^"]+",\s*"[^"]+"\)""")
        .find(roomImpl.readText(Charsets.UTF_8))
        ?.groupValues
        ?.get(1)
        ?.toIntOrNull()
        ?: throw GradleException("Room-Version konnte aus ${roomImpl.name} nicht gelesen werden.")
}

fun unescapeKotlinString(value: String): String {
    val result = StringBuilder()
    var index = 0

    while (index < value.length) {
        val current = value[index]
        if (current != '\\') {
            result.append(current)
            index++
            continue
        }

        val next = value.getOrNull(index + 1)
            ?: throw GradleException("Ungueltiger Kotlin-String-Escape in der Room-Implementierung.")
        result.append(
            when (next) {
                '\\' -> '\\'
                '"' -> '"'
                'n' -> '\n'
                'r' -> '\r'
                't' -> '\t'
                '$' -> '$'
                else -> next
            },
        )
        index += 2
    }

    return result.toString()
}

fun writeSeedData(
    connection: Connection,
    seedData: SeedData,
) {
    connection.prepareStatement(
        "INSERT INTO lokalisierung (og_sprache) VALUES (?)",
        Statement.RETURN_GENERATED_KEYS,
    ).use { insertLocalization ->
        connection.prepareStatement(
            "INSERT INTO translation (lokalisierung_id, sprache, text, bearbeitet) VALUES (?, ?, ?, ?)",
        ).use { insertTranslation ->
            connection.prepareStatement(
                "INSERT INTO spiel (lokalisierung_id, inaktiv, selbst_erstellt, favorit, bild_dateiname, texte_pro_karte) VALUES (?, ?, ?, ?, ?, ?)",
            ).use { insertGame ->
                connection.prepareStatement(
                    "INSERT INTO kategorie (lokalisierung_id, inaktiv, selbst_erstellt, favorit) VALUES (?, ?, ?, ?)",
                ).use { insertCategory ->
                    connection.prepareStatement(
                        "INSERT INTO kartentext (lokalisierung_id, inaktiv, selbst_erstellt, favorit, gesehen, gespielt) VALUES (?, ?, ?, ?, ?, ?)",
                    ).use { insertCardText ->
                        connection.prepareStatement(
                            "INSERT INTO spiel_x_kategorie (spiel_id, kategorie_id, inaktiv, selbst_erstellt) VALUES (?, ?, ?, ?)",
                        ).use { insertGameCategory ->
                            connection.prepareStatement(
                                "INSERT INTO kategorie_x_kartentext (kategorie_id, kartentext_id, inaktiv, selbst_erstellt) VALUES (?, ?, ?, ?)",
                            ).use { insertCategoryCardText ->
                                seedData.games.forEach { game ->
                                    val gameId = insertLocalization(insertLocalization, insertTranslation, game.localization)
                                    insertGame(insertGame, gameId, game)
                                    game.categories.forEach { category ->
                                        val categoryId =
                                            insertLocalization(insertLocalization, insertTranslation, category.localization)
                                        insertCategory(insertCategory, categoryId)
                                        insertOriginalJoin(insertGameCategory, gameId, categoryId)
                                        category.cardTexts.forEach { cardText ->
                                            val cardTextId =
                                                insertLocalization(insertLocalization, insertTranslation, cardText.localization)
                                            insertCardText(insertCardText, cardTextId)
                                            insertOriginalJoin(insertCategoryCardText, categoryId, cardTextId)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun verifyRoomVersion(
    connection: Connection,
    expectedVersion: Int,
) {
    connection.createStatement().use { statement ->
        statement.executeQuery("PRAGMA user_version").use { result ->
            if (!result.next()) {
                throw GradleException("PRAGMA user_version konnte nicht gelesen werden.")
            }
            val actualVersion = result.getInt(1)
            if (actualVersion != expectedVersion) {
                throw GradleException(
                    "Die Seed-Datenbank hat SQLite-Version $actualVersion, Room erwartet aber $expectedVersion.",
                )
            }
        }
    }
}

fun insertLocalization(
    insertLocalization: java.sql.PreparedStatement,
    insertTranslation: java.sql.PreparedStatement,
    localization: LocalizationSeed,
): Int {
    insertLocalization.setString(1, localization.ogSprache)
    insertLocalization.executeUpdate()
    val localizationId =
        insertLocalization.generatedKeys.use { generatedKeys ->
            if (!generatedKeys.next()) {
                throw GradleException("Lokalisierungs-ID konnte nicht erzeugt werden.")
            }
            generatedKeys.getInt(1)
        }

    localization.translations.toSortedMap().forEach { (language, text) ->
        insertTranslation.setInt(1, localizationId)
        insertTranslation.setString(2, language)
        insertTranslation.setString(3, text)
        insertTranslation.setInt(4, 0)
        insertTranslation.executeUpdate()
    }

    return localizationId
}

fun insertGame(
    insertGame: java.sql.PreparedStatement,
    gameId: Int,
    game: GameSeed,
) {
    insertGame.setInt(1, gameId)
    insertGame.setInt(2, 0)
    insertGame.setInt(3, 0)
    insertGame.setInt(4, 0)
    if (game.bildDateiname == null) {
        insertGame.setNull(5, java.sql.Types.VARCHAR)
    } else {
        insertGame.setString(5, game.bildDateiname)
    }
    insertGame.setInt(6, game.texteProKarte)
    insertGame.executeUpdate()
}

fun insertCategory(
    insertCategory: java.sql.PreparedStatement,
    categoryId: Int,
) {
    insertCategory.setInt(1, categoryId)
    insertCategory.setInt(2, 0)
    insertCategory.setInt(3, 0)
    insertCategory.setInt(4, 0)
    insertCategory.executeUpdate()
}

fun insertCardText(
    insertCardText: java.sql.PreparedStatement,
    cardTextId: Int,
) {
    insertCardText.setInt(1, cardTextId)
    insertCardText.setInt(2, 0)
    insertCardText.setInt(3, 0)
    insertCardText.setInt(4, 0)
    insertCardText.setInt(5, 0)
    insertCardText.setInt(6, 0)
    insertCardText.executeUpdate()
}

fun insertOriginalJoin(
    insertJoin: java.sql.PreparedStatement,
    leftId: Int,
    rightId: Int,
) {
    insertJoin.setInt(1, leftId)
    insertJoin.setInt(2, rightId)
    insertJoin.setInt(3, 0)
    insertJoin.setInt(4, 0)
    insertJoin.executeUpdate()
}

fun readSeedData(connection: Connection): SeedData {
    val games = mutableListOf<GameSeed>()

    connection.prepareStatement(
        "SELECT lokalisierung_id, inaktiv, selbst_erstellt, favorit, bild_dateiname, texte_pro_karte FROM spiel ORDER BY lokalisierung_id",
    ).use { selectGames ->
        selectGames.executeQuery().use { result ->
            while (result.next()) {
                val gameId = result.getInt("lokalisierung_id")
                validateDefaultFlags(
                    inaktiv = result.getBoolean("inaktiv"),
                    selbstErstellt = result.getBoolean("selbst_erstellt"),
                    favorit = result.getBoolean("favorit"),
                    context = "Spiel $gameId",
                )

                games +=
                    GameSeed(
                        localization = readLocalization(connection, gameId),
                        texteProKarte = result.getInt("texte_pro_karte"),
                        bildDateiname = result.getString("bild_dateiname"),
                        categories = readCategoriesForGame(connection, gameId),
                    )
            }
        }
    }

    return SeedData(games)
}

fun readCategoriesForGame(
    connection: Connection,
    gameId: Int,
): List<CategorySeed> {
    val categoryIds = mutableListOf<Int>()

    connection.prepareStatement(
        "SELECT kategorie_id, inaktiv, selbst_erstellt FROM spiel_x_kategorie WHERE spiel_id = ? ORDER BY kategorie_id",
    ).use { selectJoins ->
        selectJoins.setInt(1, gameId)
        selectJoins.executeQuery().use { result ->
            while (result.next()) {
                if (result.getBoolean("inaktiv") || result.getBoolean("selbst_erstellt")) {
                    throw GradleException("Spiel $gameId enthaelt unerwartete Join-Flags.")
                }
                categoryIds += result.getInt("kategorie_id")
            }
        }
    }

    return categoryIds.map { categoryId ->
        connection.prepareStatement(
            "SELECT inaktiv, selbst_erstellt, favorit FROM kategorie WHERE lokalisierung_id = ?",
        ).use { selectCategory ->
            selectCategory.setInt(1, categoryId)
            selectCategory.executeQuery().use { result ->
                if (!result.next()) {
                    throw GradleException("Kategorie $categoryId fehlt in der Datenbank.")
                }
                validateDefaultFlags(
                    inaktiv = result.getBoolean("inaktiv"),
                    selbstErstellt = result.getBoolean("selbst_erstellt"),
                    favorit = result.getBoolean("favorit"),
                    context = "Kategorie $categoryId",
                )
            }
        }

        CategorySeed(
            localization = readLocalization(connection, categoryId),
            cardTexts = readCardTextsForCategory(connection, categoryId),
        )
    }
}

fun readCardTextsForCategory(
    connection: Connection,
    categoryId: Int,
): List<CardTextSeed> {
    val cardTextIds = mutableListOf<Int>()

    connection.prepareStatement(
        "SELECT kartentext_id, inaktiv, selbst_erstellt FROM kategorie_x_kartentext WHERE kategorie_id = ? ORDER BY kartentext_id",
    ).use { selectJoins ->
        selectJoins.setInt(1, categoryId)
        selectJoins.executeQuery().use { result ->
            while (result.next()) {
                if (result.getBoolean("inaktiv") || result.getBoolean("selbst_erstellt")) {
                    throw GradleException("Kategorie $categoryId enthaelt unerwartete Kartentext-Join-Flags.")
                }
                cardTextIds += result.getInt("kartentext_id")
            }
        }
    }

    return cardTextIds.map { cardTextId ->
        connection.prepareStatement(
            "SELECT inaktiv, selbst_erstellt, favorit, gesehen, gespielt FROM kartentext WHERE lokalisierung_id = ?",
        ).use { selectCardText ->
            selectCardText.setInt(1, cardTextId)
            selectCardText.executeQuery().use { result ->
                if (!result.next()) {
                    throw GradleException("Kartentext $cardTextId fehlt in der Datenbank.")
                }
                validateDefaultFlags(
                    inaktiv = result.getBoolean("inaktiv"),
                    selbstErstellt = result.getBoolean("selbst_erstellt"),
                    favorit = result.getBoolean("favorit"),
                    context = "Kartentext $cardTextId",
                )
                if (result.getBoolean("gesehen") || result.getBoolean("gespielt")) {
                    throw GradleException("Kartentext $cardTextId hat unerwartete Status-Flags.")
                }
            }
        }

        CardTextSeed(localization = readLocalization(connection, cardTextId))
    }
}

fun readLocalization(
    connection: Connection,
    localizationId: Int,
): LocalizationSeed {
    val ogSprache =
        connection.prepareStatement(
            "SELECT og_sprache FROM lokalisierung WHERE id = ?",
        ).use { selectLocalization ->
            selectLocalization.setInt(1, localizationId)
            selectLocalization.executeQuery().use { result ->
                if (!result.next()) {
                    throw GradleException("Lokalisierung $localizationId fehlt in der Datenbank.")
                }
                result.getString("og_sprache")
            }
        }

    val translations =
        linkedMapOf<String, String>().apply {
            connection.prepareStatement(
                "SELECT sprache, text, bearbeitet FROM translation WHERE lokalisierung_id = ? ORDER BY sprache",
            ).use { selectTranslations ->
                selectTranslations.setInt(1, localizationId)
                selectTranslations.executeQuery().use { result ->
                    while (result.next()) {
                        if (result.getBoolean("bearbeitet")) {
                            throw GradleException("Lokalisierung $localizationId enthaelt unerwartet bearbeitete Translationen.")
                        }
                        put(result.getString("sprache"), result.getString("text"))
                    }
                }
            }
        }

    return LocalizationSeed(
        ogSprache = ogSprache,
        translations = translations.toSortedMap(),
    )
}

fun validateDefaultFlags(
    inaktiv: Boolean,
    selbstErstellt: Boolean,
    favorit: Boolean,
    context: String,
) {
    if (inaktiv || selbstErstellt || favorit) {
        throw GradleException("$context enthaelt unerwartete Default-Abweichungen.")
    }
}
