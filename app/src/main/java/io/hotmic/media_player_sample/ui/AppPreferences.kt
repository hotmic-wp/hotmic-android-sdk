package io.hotmic.media_player_sample.ui

import android.content.Context

object AppPreferences {
    const val PREF_FILE_NAME = "SampleAppPreferences"

    fun isCustomChatEnabled(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean("CUSTOM_CHAT_ENABLED", false)
    }

    fun setCustomChatEnabled(context: Context, value: Boolean): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )

        sharedPreferences.edit()
            .putBoolean("CUSTOM_CHAT_ENABLED", value)
            .apply()

        return sharedPreferences.getBoolean("CUSTOM_CHAT_ENABLED", false)
    }
}