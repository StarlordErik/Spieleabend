package de.kaserik.impulse.frontend.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.DataInputStream

@Stable
internal class PrivacyDraft(
    private val onChanged: () -> Unit,
    private val maximumPrediction: () -> Int,
    private val canEdit: () -> Boolean,
) {
    var name by mutableStateOf("")
        private set
    var vote by mutableStateOf<Boolean?>(null)
        private set
    var prediction by mutableIntStateOf(1)
        private set

    fun updateName(value: String) {
        if (!canEdit()) return
        name = value.take(PRIVACY_MAX_NAME_LENGTH).replace('\n', ' ').replace('\r', ' ')
        onChanged()
    }

    fun chooseVote(yes: Boolean) {
        if (!canEdit()) return
        vote = yes
        onChanged()
    }

    fun choosePrediction(value: Int) {
        if (!canEdit()) return
        prediction = value.coerceIn(1, maximumPrediction())
        onChanged()
    }

    fun constrainPrediction() {
        prediction = prediction.coerceIn(1, maximumPrediction())
    }

    fun reset(name: String = "") {
        this.name = name
        vote = null
        prediction = 1
    }

    fun restore(input: DataInputStream) {
        name = input.readUTF().take(PRIVACY_MAX_NAME_LENGTH)
        vote = when (input.readInt()) {
            0 -> false
            1 -> true
            else -> null
        }
        prediction = input.readInt().coerceIn(1, PRIVACY_MAX_PLAYERS)
    }
}

private const val PRIVACY_MAX_NAME_LENGTH = 40
