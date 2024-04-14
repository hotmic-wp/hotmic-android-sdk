package io.hotmic.media_player_sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.add
import androidx.fragment.app.commit
import io.hotmic.media_player_sample.chat.SampleChatFragment
import io.hotmic.media_player_sample.data.Credentials
import io.hotmic.media_player_sample.databinding.ActivityMainBinding
import io.hotmic.media_player_sample.ui.AppPreferences
import io.hotmic.media_player_sample.ui.MainFragment
import io.hotmic.player.HotMicPlayer
import io.hotmic.player.analytics.ANVideoCloseReason
import io.hotmic.player.main.PlayerCallbacks
import io.hotmic.player.models.HMStreamBasic

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName.toString()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.fcv_main_container)
            }
        }
    }

    override fun onBackPressed() {
        Log.d(TAG,"onBackPressed()")
        if (HotMicPlayer.isPlayerOpen(this)) {
            HotMicPlayer.closePlayer(this, ANVideoCloseReason.USER_CLOSED)
            showStreamListScreen()
        } else {
            super.onBackPressed()
        }
    }

    private fun showStreamListScreen() {
        binding.fcvMainContainer.visibility = View.VISIBLE
        binding.playerFragmentContainer.visibility = View.GONE
    }

    private fun showPlayerScreen() {
        binding.fcvMainContainer.visibility = View.GONE
        binding.playerFragmentContainer.visibility = View.VISIBLE
    }

    fun onStreamClicked(stream: HMStreamBasic) {
        showPlayerScreen()
        val builder = HotMicPlayer.Builder(this)
            .setStreamId(stream.id)
            .setUICallback(playerCallback)
            .credential(Credentials.API_KEY)

        if (AppPreferences.isCustomChatEnabled(applicationContext))
            builder.setChatFragment(SampleChatFragment())

        builder.show(R.id.player_fragment_container)

    }

    private val playerCallback = object : PlayerCallbacks {

        override fun getPlatformToken(): String {
            return Credentials.PLATFORM_TOKEN
        }

        override fun onPlayerClosed() {
            showStreamListScreen()
        }
    }
}