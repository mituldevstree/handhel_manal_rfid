package com.nada.tech.viewmodel

import androidx.lifecycle.ViewModel
import com.nada.tech.network.Resource
import com.nada.tech.network.ResponseHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {
    var jobs = HashMap<String, Job>()

    val dispatcher by lazy {
        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Resource.error(data = null, message = ResponseHandler.handleErrorResponse(throwable))
        }
    }

    fun addNewJob(name: String, job: Job) {
        this.jobs[name] = job
    }

    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.value.cancel() }
    }
}