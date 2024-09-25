package com.nikohy.barcodereader

import androidx.lifecycle.MutableLiveData

/**
 * Extension function to notify MutableLiveData observers
 * myListLiveData.notifyObserver()
 */
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}