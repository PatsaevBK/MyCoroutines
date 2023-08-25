package com.example.mycoroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {

    private val parentJob = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log("catch: $throwable")
    }
    private val childExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        log("child catch: $throwable, ${coroutineContext.job}")
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob + exceptionHandler)

    fun method() {
        val childJob1 = coroutineScope.launch {
            delay(3000)
            log("First coroutine end")
        }
        val childJob2 = coroutineScope.launch {
            delay(2000)
            log("Second coroutine end")
        }
        val childJob3 = coroutineScope.async {
            delay(1000)
            launch {
                delay(100)
                error()
                log("child third end")
            }
            log("Third coroutine end")
            "S"
        }
        log(childJob3.toString())
        coroutineScope.launch {
            withContext(Dispatchers.Default) {
                delay(1000)
                log("withcontext wait")
            }
            log("end $this")
        }
        log("go ahead, dont wait")
    }

    private fun error() {
        throw RuntimeException("OOOpps")
    }

    private fun log(message: String) {
        Log.d(LOG_TAG, message)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    companion object {

        private const val LOG_TAG = "MainViewModel"
    }
}