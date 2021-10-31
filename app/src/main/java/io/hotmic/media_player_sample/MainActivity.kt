package io.hotmic.media_player_sample

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import io.hotmic.media_player_sample.data.Credentials
import io.hotmic.media_player_sample.databinding.ActivityMainBinding
import io.hotmic.media_player_sample.ui.MainFragment
import io.hotmic.player.HotMicPlayer
import io.hotmic.player.analytics.ANVideoCloseReason
import io.hotmic.player.main.PlayerCallbacks
import io.hotmic.player.models.AppBillingResult
import io.hotmic.player.models.AppSku
import io.hotmic.player.models.HMStreamBasic
import io.hotmic.player.models.HMUserBasic
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import timber.log.Timber

class MainActivity : AppCompatActivity() {

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

        var callback = object : PlayerCallbacks {
            override val tipSkus: LiveData<List<AppSku>>
                get() = TODO("Not yet implemented")

            override fun closeOpenPanels() {
                Timber.d("closeOpenPanels")
            }

            override fun closePlayer(reason: ANVideoCloseReason, message: String?) {
                Timber.d("closePlayer")
            }

            override fun fetchSkuDetails(skuId: String?): Flowable<List<AppSku>> {
                Timber.d("fetchSkuDetails")
                return Flowable.create({
                }, BackpressureStrategy.BUFFER)
            }

            override fun followUser(
                user: HMUserBasic,
                shouldFollow: Boolean
            ): Observable<Unit> {
                Timber.d("followUser")
                return Observable.create {
                }
            }

            override fun getPlatformToken(): String {
                Timber.d("getPlatformToken")
                return Credentials.PLATFORM_TOKEN
            }

            override fun getShareLink(context: Context, streamId: String): Observable<String> {
                Timber.d("getShareLink")
                return Observable.create {
                }
            }

            override fun getUserFollowObservable(): Observable<Unit> {
                Timber.d("getUserFollowObservable")
                return Observable.create {
                }
            }

            override fun isBlockedByHost(hostId: String): Observable<Boolean>? {
                Timber.d("isBlockedByHost")
                return Observable.create {
                }
            }

            override fun isFollowing(uid: String): Boolean {
                Timber.d("isFollowing")
                return stream.user.isFollowingMe
            }

            override fun makeJoinGuestPurchase(
                activity: Activity,
                userId: String,
                hostId: String,
                streamId: String,
                streamType: String,
                appSkuDetail: AppSku
            ): Observable<AppBillingResult> {
                Timber.d("makeJoinGuestPurchase")
                return Observable.create {
                }
            }

            override fun makeTipPurchase(
                activity: Activity,
                userId: String,
                hostId: String,
                streamId: String,
                streamType: String,
                message: String,
                anonymous: Boolean,
                appSkuDetail: AppSku
            ) {
                Timber.d("makeTipPurchase")
            }

            override fun onAdClick(streamId: String, adId: String) {
                Timber.d("onAdClick")
            }
        }

        showPlayerScreen()
        HotMicPlayer.Builder(this).setStreamId(stream.id).setUICallback(callback)
            .credential(Credentials.API_KEY).show(R.id.player_fragment_container)
    }
}