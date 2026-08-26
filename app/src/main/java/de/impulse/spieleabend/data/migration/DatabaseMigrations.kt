package de.impulse.spieleabend.data.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration2To3: Migration =
    object : Migration(DATABASE_VERSION_2, DATABASE_VERSION_3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `gezogene_karte` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `spiel_id` INTEGER NOT NULL,
                    FOREIGN KEY(`spiel_id`) REFERENCES `spiel`(`lokalisierung_id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_gezogene_karte_spiel_id` " +
                    "ON `gezogene_karte` (`spiel_id`)",
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `gezogener_kartentext` (
                    `karte_id` INTEGER NOT NULL,
                    `position` INTEGER NOT NULL,
                    `kartentext_id` INTEGER NOT NULL,
                    `kategorie_id` INTEGER NOT NULL,
                    PRIMARY KEY(`karte_id`, `position`),
                    FOREIGN KEY(`karte_id`) REFERENCES `gezogene_karte`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`kartentext_id`) REFERENCES `kartentext`(`lokalisierung_id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`kategorie_id`) REFERENCES `kategorie`(`lokalisierung_id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_gezogener_kartentext_kartentext_id` " +
                    "ON `gezogener_kartentext` (`kartentext_id`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_gezogener_kartentext_kategorie_id` " +
                    "ON `gezogener_kartentext` (`kategorie_id`)",
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `spiel_einstellung` (
                    `spiel_id` INTEGER NOT NULL,
                    `texte_pro_karte_override` INTEGER NOT NULL,
                    PRIMARY KEY(`spiel_id`),
                    FOREIGN KEY(`spiel_id`) REFERENCES `spiel`(`lokalisierung_id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
        }
    }

private const val DATABASE_VERSION_2 = 2
private const val DATABASE_VERSION_3 = 3
