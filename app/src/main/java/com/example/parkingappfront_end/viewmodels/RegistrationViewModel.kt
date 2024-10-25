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
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RegistrationData(
    var name: String = "",
    var surname: String = "",
    var email: String = "",
    var password: String = "",
    var birthDate: LocalDate = LocalDate.parse("1980-01-01"),
    var phoneNumber: String = "",
    var shippingAddress: ShippingAddress = ShippingAddress()
)

data class ShippingAddress(
    var street: String = "",
    var city: String = "",
    var postalCode: String = "",
    var country: String = ""
)

class RegistrationViewModel(private val registrationRepository: AuthRepository) : ViewModel() {
    var registrationData = mutableStateOf(RegistrationData())

    private val _registrationResponse = MutableLiveData<UserDetails>()
    val loginResponse: LiveData<UserDetails> get() = _registrationResponse

    private val _registrationError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _registrationError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Funzione per aggiornare i dati della registrazione
    fun updateUserDetails(name: String, surname: String, email: String, password: String) {
        registrationData.value = registrationData.value.copy(
            name = name,
            surname = surname,
            email = email,
            password = password,
        )
    }

    fun updateUserDetails(birthDate: LocalDate, phoneNumber: String) {
        registrationData.value = registrationData.value.copy(
            birthDate = birthDate,
            phoneNumber = phoneNumber
        )
    }



    fun updateShippingAddress(street: String, city: String, postalCode: String, country: String) {
        registrationData.value = registrationData.value.copy(
            shippingAddress = ShippingAddress(street, city, postalCode, country)
        )
    }

    fun register(onRegistrationComplete: ()-> Unit){
        viewModelScope.launch {
            // Imposta lo stato di caricamento
            _isLoading.value = true

            try {

                var user = SaveUser(registrationData.value.name,
                    registrationData.value.surname,
                    registrationData.value.birthDate,
                    Credential(registrationData.value.email,registrationData.value.password),
                    registrationData.value.phoneNumber)

                Log.d(TAG, "registrazione: tentativo $user")
                val response = registrationRepository.registerUser(user)
                Log.d(TAG, "response: $response")


                if (response.isSuccessful && response.body() != null) {
                    _registrationResponse.value = response.body()
                    onRegistrationComplete()
                } else {
                    _registrationError.value = "Registration failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _registrationError.value = "An error occurred: ${e.message}"
                Log.e(TAG, "An error occurred during registration: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}