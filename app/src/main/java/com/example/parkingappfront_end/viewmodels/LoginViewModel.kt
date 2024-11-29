package com.example.parkingappfront_end.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.Credential
import com.example.parkingappfront_end.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: AuthRepository) : ViewModel() { // ViewModel() è una classe di Android Jetpack che serve a mantenere i dati in modo da poterli visualizzare in caso di rotazione dello schermo
                                                                                    // authRepository è un'istanza di AuthRepository e serve per fare richieste al server di tipo autehtication
    private val _loginResponse = MutableLiveData<Map<String, String>>()
    val loginResponse: LiveData<Map<String, String>> get() = _loginResponse

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(credentials: Credential, onLoginSuccess: () -> Unit) { // funzione che permette di fare il login
        viewModelScope.launch { // viewModelScope è un CoroutineScope che è legato al ciclo di vita del ViewModel. Quando il ViewModel viene distrutto, tutte le coroutine vengono cancellate

            _isLoading.value = true         // Imposta lo stato di caricamento
            onLoginSuccess()
            /*
            try {
                Log.d(TAG, "login: tentativo $credentials")
                val response = loginRepository.loginUser(credentials)
                Log.d(TAG, "response: $response")





                if (response.isSuccessful && response.body() != null) { // Se la risposta è stata ricevuta con successo e il corpo della risposta non è nullo
                    _loginResponse.value = response.body()
                    response.body()?.let { // let è una funzione di Kotlin che permette di eseguire un blocco di codice su un oggetto non nullo
                        _loginResponse.value?.get("access_token") // get è una funzione di Kotlin che permette di ottenere un valore da una mappa
                            ?.let { it1 -> SessionManager.saveAuthToken(it1) } //SessionManager è una classe che serve a gestire la sessione dell'utente
                        _loginResponse.value?.get("refresh_token")
                            ?.let { it2 -> SessionManager.saveRefreshToken(it2) }
                    }
                    Log.d("SessionManagerDebug", "user: ${SessionManager.user}")
                    onLoginSuccess()
                } else {
                    _loginError.value = "Login failed: ${response.message()}"  // Se il login fallisce, viene visualizzato un messaggio di errore
                }
            } catch (e: Exception) {
                _loginError.value = "An error occurred: ${e.message}"
                Log.e(TAG, "An error occurred during login: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }*/
        }
    }
}