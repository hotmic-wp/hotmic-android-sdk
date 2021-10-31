package io.hotmic.media_player_sample

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import io.hotmic.media_player_sample.data.Credentials
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

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = applicationContext.getString(R.string.title)

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
        } else {
            super.onBackPressed()
        }
    }

    fun onStreamClicked(stream: HMStreamBasic) {

        var callback = object : PlayerCallbacks {
            override val tipSkus: LiveData<List<AppSku>>
                get() = TODO("Not yet implemented")

            override fun closeOpenPanels() {
                Log.d(TAG, "Close Open Panels")
            }

            override fun closePlayer(reason: ANVideoCloseReason, message: String?) {
                Log.d(TAG, "closePlayer")
            }

            override fun fetchSkuDetails(skuId: String?): Flowable<List<AppSku>> {
                Log.d(TAG, "fetchSkuDetails")
                return Flowable.create({
                }, BackpressureStrategy.BUFFER)
            }

            override fun followUser(
                user: HMUserBasic,
                shouldFollow: Boolean
            ): Observable<Unit> {
                Log.d(TAG, "followUser")
                return Observable.create {
                }
            }

            override fun getPlatformToken(): String {
                Log.d(TAG, "getPlatformToken")
                return Credentials.PLATFORM_TOKEN
            }

            override fun getShareLink(context: Context, streamId: String): Observable<String> {
                Log.d(TAG, "getShareLink")
                return Observable.create {
                }
            }

            override fun getUserFollowObservable(): Observable<Unit> {
                Log.d(TAG, "getUserFollowObservable")
                return Observable.create {
                }
            }

            override fun isBlockedByHost(hostId: String): Observable<Boolean>? {
                Log.d(TAG, "isBlockedByHost")
                return Observable.create {
                }
            }

            override fun isFollowing(uid: String): Boolean {
                Log.d(TAG, "isFollowing")
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
                Log.d(TAG, "makeJoinGuestPurchase")
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
                Log.d(TAG, "makeTipPurchase")
            }

            override fun onAdClick(streamId: String, adId: String) {
                Log.d(TAG, "onAdClick")
            }
        }
        HotMicPlayer.Builder(this).setStreamId(stream.id).setUICallback(callback)
            .credential(Credentials.API_KEY).show(R.id.player_fragment_container)
    }
}