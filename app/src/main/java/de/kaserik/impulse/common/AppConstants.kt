package de.kaserik.impulse.common

// Non-translatable identifiers and formats shared by app infrastructure.
internal object DatabaseFiles {
    const val Name = "impulse.db"
}

internal object PreferenceKeys {
    const val PreferencesName = "app_settings"
    const val DeveloperModeKey = "developer_mode"
    const val FunFactsModeKey = "fun_facts_mode_enabled"
    const val FunFactsSessionKey = "fun_facts_session"
    const val LanguageKey = "language"
}

internal object AssetPaths {
    const val Images = "images/"
    const val SourceAssets = "app/src/main/assets/"
    const val Conversation = "${Images}game_box_side_erzaehlt_euch_mehr.png"
    const val Couples = "${Images}game_box_side_erzaehlt_euch_mehr_fuer_paare.png"
    const val FunFacts = "${Images}game_box_side_fun_facts.png"
    const val Privacy = "${Images}game_box_side_privacy.png"
    const val Strangers = "${Images}game_box_side_were_not_really_strangers.png"
}

internal object AnimationLabels {
    const val CategoryTabScale = "category-tab-scale"
    const val ColorTabScale = "color-tab-scale"
    const val RevealSign = "Schild aufdecken"
}

const val GAME_ID_ARG = "gameId"

internal object NavigationRoutes {
    const val Start = "start"
    const val GamePrefix = "game/"
    const val CardsPrefix = "cards/"
    const val Game = "$GamePrefix{$GAME_ID_ARG}"
    const val Cards = "$CardsPrefix{$GAME_ID_ARG}"
}

internal object SessionFormat {
    const val SERIALIZATION_VERSION = "7"
    const val PREVIOUS_SERIALIZATION_VERSION = "6"
    const val OLDER_SERIALIZATION_VERSION = "5"
    const val EARLIER_SERIALIZATION_VERSION = "4"
    const val LEGACY_SERIALIZATION_VERSION = "3"
    const val INITIAL_SERIALIZATION_VERSION = "2"
    const val FIELD_SEPARATOR = ";"
    const val STROKE_SEPARATOR = "|"
    const val POINT_SEPARATOR = ","
    const val COORDINATE_SEPARATOR = ":"
    const val STROKE_WIDTH_SEPARATOR = "~"
    const val EMPTY_DRAWING = "-"
    const val NULL_QUESTION_ORIGIN = "-"
}
