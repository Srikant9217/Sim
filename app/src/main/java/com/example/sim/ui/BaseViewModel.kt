package com.example.sim.ui

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.example.sim.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

abstract class BaseViewModel<ViewState> : ViewModel() {

    val TAG: String = "AppDebug"
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    val viewState: LiveData<ViewState>
        get() = _viewState

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    fun getCurrentViewStateOrNew(): ViewState {
        return viewState.value ?: initNewViewState()
    }

    abstract fun initNewViewState(): ViewState

    abstract fun setStateEvent(stateEvent: StateEvent)

    abstract fun handleNewData(data: ViewState)

    private val dataChannelManager: DataChannelManager<ViewState> =
        object : DataChannelManager<ViewState>() {
            override fun handleNewData(data: ViewState) {
                this@BaseViewModel.handleNewData(data)
            }
        }

    val numActiveJobs: LiveData<Int> = dataChannelManager.numActiveJobs

    val stateMessage: LiveData<StateMessage?>
        get() = dataChannelManager.messageStack.stateMessage

    // FOR DEBUGGING
    fun getMessageStackSize(): Int {
        return dataChannelManager.messageStack.size
    }

    fun setupChannel() = dataChannelManager.setupChannel()

    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<ViewState>>
    ) {
        dataChannelManager.launchJob(stateEvent, jobFunction)
    }

    fun areAnyJobsActive(): Boolean {
        return dataChannelManager.numActiveJobs.value?.let {
            it > 0
        } ?: false
    }

    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        Log.d(TAG, "isJobAlreadyActive?: ${dataChannelManager.isJobAlreadyActive(stateEvent)} ")
        return dataChannelManager.isJobAlreadyActive(stateEvent)
    }

    fun clearStateMessage(index: Int = 0) {
        dataChannelManager.clearStateMessage(index)
    }

    open fun cancelActiveJobs() {
        if (areAnyJobsActive()) {
            Log.d(TAG, "cancel active jobs: ${dataChannelManager.numActiveJobs.value ?: 0}")
            dataChannelManager.cancelJobs()
        }
    }
}