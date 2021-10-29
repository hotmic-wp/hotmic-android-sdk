package io.hotmic.media_player_sample.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.hotmic.player.HotMicPlayer
import io.hotmic.player.models.HMStreamBasic
import io.reactivex.schedulers.Schedulers

class StreamRepository(private val context: Context) {

    private val API_KEY = "d34c0ae5-185b-4c3b-a55c-2d7ef9ebf5b6"

    // Live Data
    private var streamListLiveData = MutableLiveData<List<HMStreamBasic>>()

    fun getStreamListLiveData(): LiveData<List<HMStreamBasic>> {
        return streamListLiveData
    }

    fun retrieveStreamList() {

        HotMicPlayer.getStreams(context, API_KEY)?.let { api ->
            api.subscribeOn(Schedulers.io()).subscribe({ sList ->
                Log.d("HotMic", "${sList.size} streams are fetched.")
                streamListLiveData.postValue(sList)
            }, { e ->
                Log.d("HotMic", "Fetching stream error occurred: $e")
            })
        }
    }

}