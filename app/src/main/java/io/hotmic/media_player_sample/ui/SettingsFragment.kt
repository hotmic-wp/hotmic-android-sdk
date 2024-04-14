package io.hotmic.media_player_sample.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.hotmic.media_player_sample.R

internal class SettingsFragment: BottomSheetDialogFragment() {

    private lateinit var toggleOptionChat: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val themeWrapper = ContextThemeWrapper(activity, R.style.HMPlayerSdkTheme)
        return inflater.cloneInContext(themeWrapper)
            .inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleOptionChat = view.findViewById(R.id.custom_chat_switch)

        toggleOptionChat.isChecked = AppPreferences.isCustomChatEnabled(requireContext())
        toggleOptionChat.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(LOG_TAG, "Changed chat option, new: $isChecked")
            AppPreferences.setCustomChatEnabled(requireContext(), isChecked)
        }

    }


    companion object {
        private const val LOG_TAG = "SettingsFragment"
    }


}