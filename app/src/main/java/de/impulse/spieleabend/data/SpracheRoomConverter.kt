package de.impulse.spieleabend.data

import androidx.room.TypeConverter
import de.impulse.spieleabend.common.Sprache

class SpracheRoomConverter {
    @TypeConverter
    fun fromSprache(sprache: Sprache): String = sprache.code

    @TypeConverter
    fun toSprache(code: String): Sprache =
        requireNotNull(Sprache.fromCode(code)) {
            "Unbekannte Sprache in der Datenbank: '$code'."
        }
}
