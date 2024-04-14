package io.hotmic.media_player_sample.chat

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.hotmic.media_player_sample.R
import io.hotmic.media_player_sample.ui.addTo
import io.hotmic.media_player_sample.ui.getColorStateList
import io.hotmic.media_player_sample.ui.toPx
import io.hotmic.player.models.Chat
import io.hotmic.player.models.ChatReaction
import io.hotmic.player.models.Tip
import io.hotmic.player.shared.HMChatViewModel
import io.hotmic.player.shared.HotMicChatHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import polyadapter.ListProvider
import polyadapter.PolyAdapter

class SampleChatFragment: Fragment(), ChatDelegatesListener {

    companion object {
        private const val LOG_TAG = "SampleChatFragment"
        private const val FRAGMENT_TAG_CHAT_INTERACTION = "chat_interaction_fragment"
        private const val FRAGMENT_TAG_TIP = "tip_fragment"
        private const val FRAGMENT_EXPANDED_CHAT_TAG = "expanded_chat_fragment"

        fun getInstance(): SampleChatFragment {
            return SampleChatFragment()
        }
    }

    private lateinit var chatListView: RecyclerView
    private lateinit var bottomOption: TextView

    private lateinit var sendChatView: EditText
    private lateinit var optionSend: TextView

    private lateinit var optionTip: ImageView
    private lateinit var optionMembers: ImageView
    private lateinit var optionPolls: ImageView
    private lateinit var memberCountView: TextView
    private lateinit var activePollIndicator: ImageView

    private lateinit var listAdapter: PolyAdapter
    private lateinit var listProvider: ListProvider
    private var listDisposable = CompositeDisposable()

    private lateinit var recoListView: RecyclerView
    private lateinit var recoListAdapter: PolyAdapter
    private lateinit var recoListProvider: ListProvider
    private var recoListDisposable = CompositeDisposable()

    private var mEnableMemberOption = true
    private var mMemberCount = 0

    private var mEnablePollOption = false
    private var mActivePollCount = 0

    private var mLatestMessageHashCode: Int = 0

    private val chatViewModel: HMChatViewModel by activityViewModels()

    private var mIsChatUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mIsChatUpdated = false

        val themeWrapper = ContextThemeWrapper(activity, R.style.HMPlayerSdkTheme)
        return inflater.cloneInContext(themeWrapper)
            .inflate(R.layout.sample_chat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatListView = view.findViewById(R.id.chat_list)
        bottomOption = view.findViewById(R.id.chat_bottom_option)
        sendChatView = view.findViewById(R.id.chat_message)
        optionSend = view.findViewById(R.id.option_send)
        optionTip = view.findViewById(R.id.option_tip)
        optionPolls = view.findViewById(R.id.option_polls)
        memberCountView = view.findViewById(R.id.member_count)
        activePollIndicator = view.findViewById(R.id.active_poll_indicator)
        optionMembers = view.findViewById(R.id.tab_option_members)
        recoListView = view.findViewById(R.id.reco_list)

        bottomOption.visibility = View.GONE
        bottomOption.setOnClickListener {
            scrollToBottom()
        }

        listProvider = ListProvider()
        listAdapter = PolyAdapter(
            listProvider, listOf(
                SampleChatDelegate(this),
                SampleTipDelegate(this)
            )
        )

        chatListView.addItemDecoration(RVVerticalDividerDecorator(8))
        chatListView.adapter = listAdapter

        chatListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && !chatListView.canScrollVertically(1)) {

                    showScrollBottomOption(false)
                }
            }
        })

        chatListView.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (!chatListView.canScrollVertically(1)) {
                showScrollBottomOption(false)
            }
        }

        optionSend.apply {
            isEnabled = false
            setTextColor(getColorStateList(context,
                pressedColor = io.hotmic.player.R.color.hmp_tintPrimaryContrast,
                enabledColor = io.hotmic.player.R.color.hmp_tintPrimary,
                disabledColor = io.hotmic.player.R.color.hmp_tintDisabled
            ))
            setOnClickListener { onSendMessageActionClicked() }
        }

        sendChatView.apply{
            addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    enableSendChat()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            })

            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus)
                    chatViewModel.logSendChatTappedAnalytics()
            }

            setOnEditorActionListener { textView, i, keyEvent ->
                if (i == EditorInfo.IME_ACTION_SEND) {
                    optionSend.performClick()
                    true
                }
                else
                    false
            }
        }

        optionTip.apply {
            isVisible = false
            setOnClickListener { onSendTipActionClicked() }
        }

        optionPolls.apply {
            isVisible = false
            setOnClickListener { onOpenPollsClicked() }
        }

        optionMembers.apply {
            isVisible = true
            setOnClickListener { onOpenMembersPanel() }
        }

        memberCountView.setOnClickListener {
            onOpenMembersPanel()
        }

        initializeRecoList()

        refreshMemberOption()
        refreshPollOption()

        setChatStateObserver()

        //Set observer to receive chat/tips updates
        setChatDataObserver()

        //Set the observer to monitor any changes in stream participants
        setRoomActiveCountObserver()

        setPollActiveDataObserver()

        val recoList = ArrayList<SampleReco>()
        for (i in 0..15) {
            recoList.add(SampleReco(id = i))
        }

        loadRecoList(recoList)
    }

    private fun initializeRecoList() {
        recoListProvider = ListProvider()
        recoListAdapter = PolyAdapter(
            recoListProvider, listOf(
                SampleRecoDelegate()
            )
        )

        recoListView.addItemDecoration(RVHorizontalDividerDecorator(5))
        recoListView.adapter = recoListAdapter
    }

    private fun loadRecoList(data: List<SampleReco>) {
        Observable
            .fromCallable {
                recoListProvider.updateItems(data)
            }
            .observeOn(Schedulers.computation())
            .map { diffWork ->
                diffWork()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map { listChanges ->
                listChanges()
            }
            .subscribe({

            }, {
                Log.e(LOG_TAG, "Error in rendering reco list", it)
            }) addTo recoListDisposable
    }

    fun update(social: List<Any>): Boolean {
        val latestHashCode = if (social.isNotEmpty()) social[social.size - 1].hashCode() else 0
        val changed = mLatestMessageHashCode != latestHashCode
        listDisposable.clear()

        var isScrollBottomed = false
        Observable
            .fromCallable {
                listProvider.updateItems(social)
            }
            .observeOn(Schedulers.computation())
            .map { diffWork ->
                diffWork()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map { listChanges ->
                isScrollBottomed = !chatListView.canScrollVertically(1)
                listChanges()
            }
            .subscribe({
                //Scroll to the end only if the list cursor is also at the bottom, we dont want to
                // disturb if the user is scrolling
                if (isScrollBottomed) {
                    Log.d(LOG_TAG, "Scrolling to end")
                    chatListView.scrollToPosition(social.size - 1)
                }
                else {
                    showScrollBottomOption(true)
                }
            }, {
                Log.e(LOG_TAG, "Error in rendering list", it)
            }) addTo listDisposable

        return changed
    }

    private fun scrollToBottom() {
        chatListView.adapter?.itemCount?.let {
            chatListView.scrollToPosition(it - 1 )
        }

        showScrollBottomOption(false)
    }

    fun updateRoomCount(roomCount: Int) {
        mMemberCount = roomCount
        refreshMemberOption()
    }

    fun updateActivePollCount(count: Int) {
        mActivePollCount = count
        refreshPollOption()
    }

    private fun refreshMemberOption() {
        optionMembers.isVisible = mEnableMemberOption
        memberCountView.isVisible = mEnableMemberOption && mMemberCount > 1
        memberCountView.text = mMemberCount.toString()
    }

    private fun refreshPollOption() {
        optionPolls.isVisible = mEnablePollOption
        activePollIndicator.isVisible = mEnablePollOption && mActivePollCount > 0
    }

    var sendMessageText: String
        get() = sendChatView.text.toString().trim()
        set(value) = sendChatView.setText(value)

    var isTipOptionEnabled: Boolean
        get() = optionTip.isVisible
        set(value) {
            optionTip.isVisible = value
        }

    var isPollOptionEnabled: Boolean
        get() = mEnablePollOption
        set(value) {
            mEnablePollOption = value
            refreshPollOption()
        }

    var isMembersOptionEnabled: Boolean
        get() = mEnableMemberOption
        set(value) {
            mEnableMemberOption = value
            refreshMemberOption()
        }

    private fun enableSendChat() {
        optionSend.isEnabled = sendChatView.text.toString().isNotBlank()
    }

    fun clearSendText() {
        sendChatView.text.clear()
        sendChatView.clearFocus()
    }

    private fun showScrollBottomOption(show: Boolean) {
        bottomOption.isVisible = show
    }

    fun close() {
        listDisposable.clear()
    }

    override fun onDelegateChatClicked(chat: Chat, links: List<ChatLink>, vy: Float) {
        onChatClicked(chat, links, vy + (view?.y ?: 0f))
    }

    override fun onDelegateChatLongPressed(chat: Chat, links: List<ChatLink>, vy: Float) {
        onChatOptionClicked(chat, ChatArgument(links))
    }

    override fun onDelegateTipClicked(tip: Tip, links: List<ChatLink>) {
        onTipClicked(tip, links)
    }

    override fun onDelegateChatReactionClicked(
        chat: Chat,
        reaction: ChatReaction,
        selected: Boolean
    ): Boolean {
        return onChatReactionClicked(chat, reaction, selected) ?: false
    }

    override fun onDelegateChatProfileClicked(userId: String) {
        HotMicChatHandler.showProfilePanel(requireActivity(), userId = userId)
    }

    private fun setChatStateObserver() {
        chatViewModel.streamChatStateLiveData.observe(viewLifecycleOwner) { state ->
            Log.d(LOG_TAG, "Chat state update received, state:$state")

            isTipOptionEnabled = state.isTipEnabled
            isPollOptionEnabled = state.isPollEnabled
            isMembersOptionEnabled = true

        }
    }

    private fun setChatDataObserver() {
        chatViewModel.chatTipsData.observe(viewLifecycleOwner) { chatData ->
            Log.d(LOG_TAG, "Chat data received, size: ${chatData.size}")

            val viewUpdate = update(chatData)
            mIsChatUpdated = !isVisible and (mIsChatUpdated or viewUpdate)
            updateExpandedChat(chatData)

            isPollOptionEnabled = chatViewModel.streamChatStateLiveData.value?.isPollEnabled == true
        }
    }

    private fun setRoomActiveCountObserver() {
        chatViewModel.roomActiveCountData.observe(viewLifecycleOwner) { count ->
            Log.d(LOG_TAG, "Participant update received, count: $count")
            updateRoomCount(count)
        }
    }

    private fun setPollActiveDataObserver() {
        chatViewModel.pollActiveData.observe(viewLifecycleOwner) { count ->
            Log.d(LOG_TAG, "Poll active update received: $count")
            isPollOptionEnabled = chatViewModel.streamChatStateLiveData.value?.isPollEnabled == true
            updateActivePollCount(count)
        }
    }

    override fun onDestroyView() {
        close()
        super.onDestroyView()
    }

    /**
     * Dismiss all overlays shown on top of the player
     */
    private fun dismissAllOverlays() {
        activity?.supportFragmentManager?.let { fragManager ->
            (fragManager.findFragmentByTag(FRAGMENT_TAG_CHAT_INTERACTION)
                    as? BottomSheetDialogFragment)?.dismiss()
        }

        childFragmentManager.let { fm ->
            (fm.findFragmentByTag(FRAGMENT_TAG_TIP) as? BottomSheetDialogFragment)?.dismiss()

            (fm.findFragmentByTag(FRAGMENT_EXPANDED_CHAT_TAG) as? DialogFragment)?.dismiss()
        }
    }

    private fun showSendTipsPanel() {
        //Anonymous user check
        /*if (!HMSession.sessionRestriction.tip) {
            if (HotMic.uiCallback?.onRestrictedUserAction("tips") != true) {
                //Show message only if the implementation is returning false
                Toast.makeText(context, R.string.feature_restricted_msg, Toast.LENGTH_LONG).show()
            }
            return
        }

        TipBottomSheetFragment.newInstance().show(childFragmentManager, FRAGMENT_TAG_TIP)*/
    }

    private fun updateExpandedChat(items: List<Any>) {
        /*(childFragmentManager.findFragmentByTag(FRAGMENT_EXPANDED_CHAT_TAG) as? ReactionsDialog)
            ?.updateDetails(items)*/
    }

    /*private fun showChatInteractionSheet(chatSheet: ChatInteractionSheet) {
        activity?.supportFragmentManager?.let { fragManager ->
            if (fragManager.findFragmentByTag(FRAGMENT_TAG_CHAT_INTERACTION) == null)
                chatSheet.show(fragManager, FRAGMENT_TAG_CHAT_INTERACTION)
        }
    }*/

    private fun onChatOptionClicked(chat: Chat?, links: ChatArgument?) {
        /*chatViewModel.logChatOptionsTappedAnalytics()

        val sheet = ChatInteractionSheet.newInstance(
            chat = chat,
            links = links
        )

        showChatInteractionSheet(sheet)*/
    }

    private fun onChatClicked(chat: Chat, links: List<ChatLink>, vy: Float) {
        /*Log.d(LOG_TAG, "Chat interaction sheet requested, id: ${chat.userName}, ($vy)")

        childFragmentManager.let { fragManager ->
            if (fragManager.findFragmentByTag(FRAGMENT_EXPANDED_CHAT_TAG) == null)
                ReactionsDialog.newInstance(chat, vy).show(fragManager, FRAGMENT_EXPANDED_CHAT_TAG)
        }*/
    }

    private fun onTipClicked(tip: Tip, links: List<ChatLink>) {
        /*Log.d(LOG_TAG, "Tip interaction sheet requested, id: ${tip.userName}")
        val sheet = ChatInteractionSheet.newInstance(
            tip = tip,
            links = ChatArgument(links)
        )

        showChatInteractionSheet(sheet)*/
    }

    private fun onChatReactionClicked(
        chat: Chat,
        reaction: ChatReaction,
        selected: Boolean
    ): Boolean {
        Log.d(LOG_TAG, "Sending chat reaction, selected:$selected, ${reaction.name}, ${chat.id}")

        //Anonymous user check
        /*if (!HMSession.sessionRestriction.chat) {
            if (HotMic.uiCallback?.onRestrictedUserAction("send_chat_reaction") != true) {
                //Show message only if the implementation is returning false
                Toast.makeText(context, R.string.feature_restricted_msg, Toast.LENGTH_LONG).show()
            }
            return false
        }

        if (chatViewModel.streamChatStateLiveData.value?.isChatEnabled != true) {
            Log.d(LOG_TAG, "User is blocked from chat")
            Toast.makeText(context, R.string.chat_reactions_blocked, Toast.LENGTH_SHORT).show()
            return false
        }

        chatViewModel.sendReaction(chat, reaction, selected)*/
        return true
    }

    private fun onSendMessageActionClicked() {
        Log.d(LOG_TAG, "Send chat message clicked")

        //Anonymous user check
        /*if (!HMSession.sessionRestriction.chat) {
            if (HotMic.uiCallback?.onRestrictedUserAction("send_chat") != true) {
                //Show message only if the implementation is returning false
                Toast.makeText(context, R.string.feature_restricted_msg, Toast.LENGTH_LONG).show()
            }
            return
        }*/

        if (chatViewModel.streamChatStateLiveData.value?.isChatEnabled != true) {
            Log.d(LOG_TAG, "User is blocked from chat")
            Toast.makeText(context, "You have been blocked from this chat", Toast.LENGTH_LONG).show()
            return
        }

        val message = sendMessageText
        if (message.isNotBlank()) {
            clearSendText()
            view?.windowToken?.let { wt ->
                (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(wt, 0)
            }

            chatViewModel.sendChat(message)
        }
    }

    private fun onSendTipActionClicked() {
        Log.d(LOG_TAG, "Send tip clicked")
        showSendTipsPanel()
        chatViewModel.logTipTappedAnalytics()
    }

    private fun onOpenMembersPanel() {
        HotMicChatHandler.showPeoplePanel(requireActivity())
    }

    private fun onOpenPollsClicked() {
        Log.d(LOG_TAG, "On-open-polls-clicked")
        HotMicChatHandler.showPollPanel(requireActivity(), source = "info")
    }

    class RVVerticalDividerDecorator(spacingInDp: Int): RecyclerView.ItemDecoration() {

        private val spacingInPx = spacingInDp.toPx()

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = spacingInPx
        }
    }

    class RVHorizontalDividerDecorator(spacingInDp: Int): RecyclerView.ItemDecoration() {

        private val spacingInPx = spacingInDp.toPx()

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = spacingInPx
        }
    }

}

internal interface ChatDelegatesListener {
    fun onDelegateChatClicked(chat: Chat, links: List<ChatLink>, vy: Float)

    fun onDelegateChatLongPressed(chat: Chat, links: List<ChatLink>, vy: Float)

    fun onDelegateTipClicked(tip: Tip, links: List<ChatLink>)

    fun onDelegateChatReactionClicked(chat: Chat, reaction: ChatReaction, selected: Boolean): Boolean

    fun onDelegateChatProfileClicked(userId: String)

}