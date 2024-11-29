package com.example.parkingappfront_end.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.UserDetails
import com.example.parkingappfront_end.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: AccountRepository): ViewModel() {
    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: StateFlow<UserDetails?> = _userDetails

    private val _onError = MutableLiveData<String>()
    val onError: LiveData<String> get() = _onError

    fun loadUserDetails(forceReload : Boolean = false) {

        if (_userDetails.value != null && !forceReload) {
            return
        }

        viewModelScope.launch {
            try {
                if (SessionManager.user != null) {
                    _userDetails.value = repository.getLoggedInUser(SessionManager.user!!.id)
                    Log.d("UserDebug", "loadUserDetails: ${_userDetails.value}")
                }
            } catch (e: Exception) {
                // Gestisci eccezione
                Log.e("UserDebug", "Errore durante il caricamento dei dati", e)
            }
        }
    }


}