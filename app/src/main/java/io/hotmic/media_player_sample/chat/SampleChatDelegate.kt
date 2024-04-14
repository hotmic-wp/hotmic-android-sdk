package io.hotmic.media_player_sample.chat

import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.hotmic.media_player_sample.R
import io.hotmic.media_player_sample.ui.toPx
import io.hotmic.player.models.Chat
import io.hotmic.player.models.ChatReaction
import io.hotmic.player.ui.ReactionView
import polyadapter.PolyAdapter
import polyadapter.equalityItemCallback

internal class SampleChatDelegate(
    private val actionHandler: ChatDelegatesListener
) : PolyAdapter.BindingDelegate<Chat, SampleChatViewHolder>,
    PolyAdapter.OnViewRecycledDelegate<SampleChatViewHolder> {

    override val layoutId = SampleChatViewHolder.LAYOUT
    override val dataType = Chat::class.java

    override val itemCallback = equalityItemCallback<Chat> { id }

    override fun createViewHolder(itemView: View) = SampleChatViewHolder(itemView)

    override fun bindView(holder: SampleChatViewHolder, item: Chat) {
        val links = holder.bindChat(item) {
            actionHandler.onDelegateChatProfileClicked(item.userId)
        }

        holder.setClick { _ ->
            actionHandler.onDelegateChatClicked(item, links, holder.itemView.y)
        }

        holder.setReactionClick { reactionType ->
            actionHandler.onDelegateChatReactionClicked(
                item,
                reactionType,
                !(item.userReactions?.contains(reactionType) ?: false)
            )
        }

        holder.setLongClick {
            actionHandler.onDelegateChatLongPressed(item, links, holder.itemView.y)
            true
        }

    }

    override fun onRecycle(holder: SampleChatViewHolder) {
        holder.onRecycled()
    }
}

internal class SampleChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val chatView = view.findViewById<ChatTextView>(R.id.chat_text)
    private val image = view.findViewById<CircleImageView>(R.id.profile_image)
    private val likeReaction = view.findViewById<ReactionView>(R.id.reaction_like)
    private val fireReaction = view.findViewById<ReactionView>(R.id.reaction_fire)
    private val laughReaction = view.findViewById<ReactionView>(R.id.reaction_laugh)
    private val angerReaction = view.findViewById<ReactionView>(R.id.reaction_anger)
    private val sadnessReaction = view.findViewById<ReactionView>(R.id.reaction_sadness)

    fun bindChat(chat: Chat, imageClick: (View) -> Unit): List<ChatLink> {
        val isHost = false

        if (chat.profilePic != null) {
            image.isVisible = true
            Glide.with(image)
                .load(chat.profilePic)
                .circleCrop()
                .into(image)
            image.setOnClickListener(imageClick)

            image.borderWidth = if (isHost) BORDER_WIDTH else 0
        } else {
            image.isVisible = false
        }

        updateReactionView(
            ChatReaction.LIKE,
            likeReaction,
            chat.likeCount ?: 0,
            chat.userReactions.orEmpty()
        )
        updateReactionView(
            ChatReaction.FIRE,
            fireReaction,
            chat.fireCount ?: 0,
            chat.userReactions.orEmpty()
        )
        updateReactionView(
            ChatReaction.LAUGH,
            laughReaction,
            chat.laughCount ?: 0,
            chat.userReactions.orEmpty()
        )
        updateReactionView(
            ChatReaction.ANGER,
            angerReaction,
            chat.angerCount ?: 0,
            chat.userReactions.orEmpty()
        )
        updateReactionView(
            ChatReaction.SADNESS,
            sadnessReaction,
            chat.sadnessCount ?: 0,
            chat.userReactions.orEmpty()
        )

        return chatView.setChat(
            name = chat.userName,
            message = chat.message,
            isVerified = chat.verified ?: false,
            isHost = isHost
        )
    }

    private fun updateReactionView(
        reaction: ChatReaction,
        reactionView: ReactionView,
        count: Int,
        reactions: Set<ChatReaction>
    ) {
        reactionView.isVisible = count > 0
        reactionView.setReaction(reaction)
        reactionView.setCount(count)
        reactionView.setHighlight(reactions.contains(reaction))
    }

    fun setReactionClick(listener: (ChatReaction) -> Boolean) {
        likeReaction.setReactionClick(ChatReaction.LIKE, listener)
        fireReaction.setReactionClick(ChatReaction.FIRE, listener)
        laughReaction.setReactionClick(ChatReaction.LAUGH, listener)
        angerReaction.setReactionClick(ChatReaction.ANGER, listener)
        sadnessReaction.setReactionClick(ChatReaction.SADNESS, listener)
    }

    private fun ReactionView.setReactionClick(
        reaction: ChatReaction,
        listener: (ChatReaction) -> Boolean
    ) {
        this.setOnClickListener {
            this.toggleReaction()
            if (!listener(reaction)) {
                //Toggle it back in error
                this.toggleReaction()
            }
        }
    }

    fun setClick(listener: (View) -> Unit) {
        itemView.setOnClickListener(listener)
    }

    fun setLongClick(listener: (View) -> Boolean) {
        itemView.setOnLongClickListener(listener)
    }

    fun onRecycled() {
        try {
            Glide.with(image.context).clear(image)
        }
        catch(e: Exception) {
            //Ignore it, Glide sometimes throws IllegalArgument if the underlying activity
            // has got closed
        }
    }

    companion object {
        @LayoutRes
        val LAYOUT = R.layout.sample_chat_delegate

        private val BORDER_WIDTH = 2.toPx()
    }
}