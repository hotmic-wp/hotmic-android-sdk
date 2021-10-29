package io.hotmic.media_player_sample.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.hotmic.media_player_sample.repository.StreamRepository
import io.hotmic.player.models.HMStreamBasic

class StreamViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    // Repo & Live Data
    private val repository = StreamRepository(context)
    private var streamListLiveData = repository.getStreamListLiveData()

    fun retrieveStreamList() {
        repository.retrieveStreamList()
    }

    fun getStreamListLiveData(): LiveData<List<HMStreamBasic>> {
        return streamListLiveData
    }

    fun getStreamList(): List<HMStreamBasic>? {
        return streamListLiveData.value
    }

}