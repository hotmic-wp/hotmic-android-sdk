package io.hotmic.player.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import androidx.emoji.widget.EmojiTextView
import io.hotmic.media_player_sample.R
import io.hotmic.media_player_sample.ui.toPx
import io.hotmic.player.models.ChatReaction

internal class ReactionView : EmojiTextView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    var highlighted: Boolean = false

    private fun init() {
        background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.sample_reaction_custom_bkg,
            context.theme
        )

        setTextColor(Color.BLACK)
        setPadding(
            8.toPx(),
            4.toPx(),
            8.toPx(),
            6.toPx()
        )
    }

    var reactionCount: Int = 0

    private var reaction = ChatReaction.LIKE

    fun setReaction(reaction: ChatReaction) {
        this.reaction = reaction
        updateText()
    }

    fun setCount(count: Int) {
        this.reactionCount = count
        updateText()
    }

    private fun updateText() {
        text = "${reaction.emoji} $reactionCount"
    }

    fun setHighlight(isHighlighted: Boolean) {
        highlighted = isHighlighted
        val backgroundId = if (isHighlighted) {
            R.drawable.sample_reaction_custom_bkg_selected
        } else {
            R.drawable.sample_reaction_custom_bkg
        }

        background = ResourcesCompat.getDrawable(
            resources,
            backgroundId,
            context.theme
        )
    }

    fun toggleReaction() {
        this.setCount(this.reactionCount + if (this.highlighted) -1 else 1)
        this.setHighlight(!this.highlighted)
    }
}