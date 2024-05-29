package io.hotmic.media_player_sample.util

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val PREF_FILE_NAME = "SampleAppPreferences"
    private const val PREF_API_KEY = "API_KEY"
    private const val PREF_PLATFORM_TOKEN = "PLATFORM_TOKEN"
    private const val PREF_CUSTOM_CHAT_ENABLED = "CUSTOM_CHAT_ENABLED"
    private const val PREF_CUSTOM_PLAYER_ENABLED = "CUSTOM_PLAYER_ENABLED"

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun getApiKey(context: Context): String {
        val sp = getSharedPrefs(context)
        return sp.getString(PREF_API_KEY, "") ?: ""
    }

    fun setApiKey(context: Context, key: String) {
        val sp = getSharedPrefs(context)
        sp.edit().putString(PREF_API_KEY, key).apply()
    }

    fun getPlatformToken(context: Context): String {
        val sp = getSharedPrefs(context)
        return sp.getString(PREF_PLATFORM_TOKEN, "") ?: ""
    }

    fun setPlatformToken(context: Context, token: String) {
        val sp = getSharedPrefs(context)
        sp.edit().putString(PREF_PLATFORM_TOKEN, token).apply()
    }

    fun isCustomChatEnabled(context: Context): Boolean {
        val sp = getSharedPrefs(context)
        return sp.getBoolean(PREF_CUSTOM_CHAT_ENABLED, false)
    }

    fun setCustomChatEnabled(context: Context, value: Boolean) {
        val sp = getSharedPrefs(context)
        sp.edit().putBoolean(PREF_CUSTOM_CHAT_ENABLED, value).apply()
    }

    fun isCustomPlayerEnabled(context: Context): Boolean {
        val sp = getSharedPrefs(context)
        return sp.getBoolean(PREF_CUSTOM_PLAYER_ENABLED, false)
    }

    fun setCustomPlayerEnabled(context: Context, value: Boolean) {
        val sp = getSharedPrefs(context)
        sp.edit().putBoolean(PREF_CUSTOM_PLAYER_ENABLED, value).apply()
    }
}