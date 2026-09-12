package de.kaserik.impulse.common

// Non-translatable identifiers and formats shared by app infrastructure.
internal object DatabaseFiles {
    const val NAME = "impulse.db"
}

internal object PreferenceKeys {
    const val PREFERENCES_NAME = "app_settings"
    const val DEVELOPER_MODE_KEY = "developer_mode"
    const val FUN_FACTS_MODE_KEY = "fun_facts_mode_enabled"
    const val FUN_FACTS_SESSION_KEY = "fun_facts_session"
    const val LANGUAGE_KEY = "language"
    const val LAST_DRAW_CATEGORY_PREFIX = "last_draw_category_"
}

internal object AssetPaths {
    const val IMAGES = "images/"
    const val SOURCE_ASSETS = "app/src/main/assets/"
    const val CONVERSATION = "${IMAGES}game_box_side_erzaehlt_euch_mehr.png"
    const val COUPLES = "${IMAGES}game_box_side_erzaehlt_euch_mehr_fuer_paare.png"
    const val FUN_FACTS = "${IMAGES}game_box_side_fun_facts.png"
    const val PRIVACY = "${IMAGES}game_box_side_privacy.png"
    const val STRANGERS = "${IMAGES}game_box_side_were_not_really_strangers.png"
}

internal object AnimationLabels {
    const val CATEGORY_TAB_SCALE = "category-tab-scale"
    const val COLOR_TAB_SCALE = "color-tab-scale"
    const val REVEAL_SIGN = "Schild aufdecken"
}

const val GAME_ID_ARG = "gameId"

internal object NavigationRoutes {
    const val START = "start"
    const val GAME_PREFIX = "game/"
    const val CARDS_PREFIX = "cards/"
    const val GAME = "$GAME_PREFIX{$GAME_ID_ARG}"
    const val CARDS = "$CARDS_PREFIX{$GAME_ID_ARG}"
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
