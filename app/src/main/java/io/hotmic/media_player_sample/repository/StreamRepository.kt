package io.hotmic.media_player_sample.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.hotmic.media_player_sample.data.Credentials
import io.hotmic.player.HotMicPlayer
import io.hotmic.player.models.HMStreamBasic
import io.hotmic.player.models.HMStreamState
import io.reactivex.schedulers.Schedulers

class StreamRepository(private val context: Context) {

    private val TAG = this.javaClass.simpleName.toString()

    // Live Data
    private var streamListLiveData = MutableLiveData<List<HMStreamBasic>>()

    fun getStreamListLiveData(): LiveData<List<HMStreamBasic>> {
        return streamListLiveData
    }

    fun retrieveStreamList() {
        HotMicPlayer.getStreams(context, Credentials.API_KEY)?.let { api ->
            api.subscribeOn(Schedulers.io()).subscribe({ sList ->
                Log.d(TAG,"${sList.size} streams are fetched.")
                streamListLiveData.postValue(sList)
            }, { e ->
                Log.d(TAG,"Fetching stream error occurred: $e")
            })
        }
    }
}