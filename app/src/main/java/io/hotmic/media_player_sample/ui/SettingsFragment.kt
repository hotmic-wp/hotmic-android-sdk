package io.hotmic.media_player_sample.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.hotmic.media_player_sample.R
import io.hotmic.media_player_sample.util.AppPreferences

internal class SettingsFragment: BottomSheetDialogFragment() {

    private lateinit var toggleOptionChat: SwitchCompat
    private lateinit var editApiKey: EditText
    private lateinit var editToken: EditText

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
        editApiKey = view.findViewById(R.id.api_key_edit)
        editToken = view.findViewById(R.id.token_edit)

        toggleOptionChat.isChecked = AppPreferences.isCustomChatEnabled(requireContext())
        toggleOptionChat.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(LOG_TAG, "Changed chat option, new: $isChecked")
            AppPreferences.setCustomChatEnabled(requireContext(), isChecked)
        }

        editApiKey.setText(AppPreferences.getApiKey(requireContext()))
        editApiKey.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                AppPreferences.setApiKey(requireContext(), editApiKey.text.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        editToken.setText(AppPreferences.getPlatformToken(requireContext()))
        editToken.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                AppPreferences.setPlatformToken(requireContext(), editToken.text.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

    }


    companion object {
        private const val LOG_TAG = "SettingsFragment"
    }


}