package io.hotmic.media_player_sample.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.hotmic.media_player_sample.util.AppPreferences
import io.hotmic.player.HotMicPlayer
import io.hotmic.player.models.HMStreamBasic
import io.reactivex.schedulers.Schedulers

class StreamRepository(private val context: Context) {

    private val TAG = this.javaClass.simpleName.toString()

    // Live Data
    private var streamListLiveData = MutableLiveData<List<HMStreamBasic>>()

    fun getStreamListLiveData(): LiveData<List<HMStreamBasic>> {
        return streamListLiveData
    }

    fun retrieveStreamList() {
        HotMicPlayer.getStreams(context, AppPreferences.getApiKey(context))?.let { api ->
            api.subscribeOn(Schedulers.io()).subscribe({ sList ->
                Log.d(TAG,"${sList.size} streams are fetched.")
                streamListLiveData.postValue(sList)
            }, { e ->
                Log.d(TAG,"Fetching stream error occurred: $e")
                streamListLiveData.postValue(emptyList())
            })
        }
    }
}