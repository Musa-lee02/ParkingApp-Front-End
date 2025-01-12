package com.example.parkingappfront_end.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.parkingappfront_end.model.Credential
import com.example.parkingappfront_end.model.SaveUser
import com.example.parkingappfront_end.model.UserDetails
import com.example.parkingappfront_end.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDate

data class RegistrationData(
    var name: String = "",
    var surname: String = "",
    var email: String = "",
    var password: String = "",
    var birthDate: LocalDate = LocalDate.parse("1980-01-01"),
    var admin: String = ""
)

class RegistrationViewModel(private val registrationRepository: AuthRepository) : ViewModel() {
    var registrationData = mutableStateOf(RegistrationData())

    private val _registrationResponse = MutableLiveData<UserDetails>()
    val loginResponse: LiveData<UserDetails> get() = _registrationResponse

    private val _registrationError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _registrationError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showSnackbar = MutableStateFlow(false)
    val showSnackbar: StateFlow<Boolean> get() = _showSnackbar

    private val _snackbarMessage = MutableStateFlow("")
    val snackbarMessage: StateFlow<String> get() = _snackbarMessage

    // Funzione per aggiornare i dati della registrazione
    fun updateUserDetails(name: String, surname: String, email: String, password: String) {
        registrationData.value = registrationData.value.copy(
            name = name,
            surname = surname,
            email = email,
            password = password,
        )
    }

    fun updateUserDetails(birthDate: LocalDate, admin: String) {
        registrationData.value = registrationData.value.copy(
            birthDate = birthDate,
            admin = admin
        )
    }


    fun register(onRegistrationComplete: ()-> Unit){
        viewModelScope.launch {
            // Imposta lo stato di caricamento
            _isLoading.value = true
            var message = ""

            try {

                var user = SaveUser(
                    registrationData.value.name,
                    registrationData.value.surname,
                    registrationData.value.birthDate,

                    Credential(registrationData.value.email,
                        registrationData.value.password),
                    )

                var response: Response<UserDetails>

                if(registrationData.value.admin.isNotBlank() && registrationData.value.admin == "0000") {
                    Log.d(TAG, "registrazione: tentativo admin $user")
                    response = registrationRepository.registerAdmin(user)
                    Log.d(TAG, "response: $response")
                    message = "Admin"
                }

                if(registrationData.value.admin.isNotBlank() && registrationData.value.admin == "1111") {
                    Log.d(TAG, "registrazione: tentativo owner $user")
                    response = registrationRepository.registerOwner(user)
                    Log.d(TAG, "response: $response")
                    message = "Owner"
                }

                else {
                    Log.d(TAG, "registrazione: tentativo user$user")
                    response = registrationRepository.registerUser(user)
                    Log.d(TAG, "response: $response")
                    message = "User"
                }


                if (response.isSuccessful && response.body() != null) {
                    _registrationResponse.value = response.body()
                    triggerSnackbar("$message ${user.firstName} sign up successful! ")
                    onRegistrationComplete()

                } else {
                    val errorBody = response.errorBody()?.string()

                    _registrationError.value = "An error occurred: ${errorBody}"
                    triggerSnackbar("An error occurred: ${errorBody}")
                    Log.d(TAG, "register: $errorBody")

                }

            } catch (e: Exception) {
                _registrationError.value = "An error occurred: ${e.message}"
                Log.e(TAG, "An error occurred during registration: ${e.localizedMessage}", e)
                triggerSnackbar("An error occurred during registration: ${e.localizedMessage}")

            } finally {
                _isLoading.value = false
            }
        }
    }
    fun setShowSnackbar(b: Boolean) {
        _showSnackbar.value = b

    }


    fun triggerSnackbar(message: String) {
        _snackbarMessage.value = message
        _showSnackbar.value = true
    }



}