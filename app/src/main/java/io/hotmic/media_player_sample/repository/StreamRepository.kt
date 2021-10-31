package io.hotmic.media_player_sample.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.hotmic.media_player_sample.data.Credentials
import io.hotmic.player.HotMicPlayer
import io.hotmic.player.models.HMStreamBasic
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class StreamRepository(private val context: Context) {

    // Live Data
    private var streamListLiveData = MutableLiveData<List<HMStreamBasic>>()

    fun getStreamListLiveData(): LiveData<List<HMStreamBasic>> {
        return streamListLiveData
    }

    fun retrieveStreamList() {

        HotMicPlayer.getStreams(context, Credentials.API_KEY)?.let { api ->
            api.subscribeOn(Schedulers.io()).subscribe({ sList ->
                Timber.d("${sList.size} streams are fetched.")
                streamListLiveData.postValue(sList)
            }, { e ->
                Timber.d("Fetching stream error occurred: $e")
            })
        }
    }
}