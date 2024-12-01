package com.example.parkingappfront_end.viewmodels

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.OrderSummary
import com.example.parkingappfront_end.model.PasswordUser
import com.example.parkingappfront_end.model.User
import com.example.parkingappfront_end.model.UserDetails
import com.example.parkingappfront_end.repository.AccountRepository
import com.example.parkingappfront_end.utils.ErrorMessageParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.UUID

class AccountViewModel(private val repository: AccountRepository): ViewModel() {
    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: StateFlow<UserDetails?> = _userDetails

    private val _userOrders = MutableStateFlow<List<OrderSummary>>(emptyList())
    val userOrders: StateFlow<List<OrderSummary>> get() = _userOrders


    private val _onError = MutableLiveData<String>()
    val onError: LiveData<String> get() = _onError

    private val _isLoadingOrders = MutableStateFlow(false)
    val isLoadingOrders: StateFlow<Boolean> get() = _isLoadingOrders

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> get() = _isLoggingOut

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount: StateFlow<Boolean> get() = _isDeletingAccount

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val snackbarHostState = SnackbarHostState()

    fun loadUserDetails() {
        viewModelScope.launch {
            try {
                if (SessionManager.user != null) {
                    _userDetails.value = repository.getLoggedInUser(SessionManager.user!!.id)
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException)
                    _errorMessage.value = "Error while fetching the user details: Connection problem."
                else
                    _errorMessage.value = "Error while fetching the user details."
            }
        }
    }

    fun editFunction(key:String, value : String, onSuccess: ()->Unit){
        viewModelScope.launch {
            try {
                val response: Response<User>?;

                if(key == "Email") {
                    response = userDetails.value?.let { repository.editEmail(it.id, value) }
                }
                else{
                    response = null
                }

                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        onSuccess()
                    }
                    else{
                        val errorBody = response.errorBody()?.string()

                        _errorMessage.value = ErrorMessageParser(errorBody)
                    }
                } else{
                    _errorMessage.value = "Unexpected Error."
                }
            }catch (e: Exception) {
                if (e is SocketTimeoutException)
                    _errorMessage.value = "Error while changing user $key: Connection problem."
                else
                    _errorMessage.value = "Error while changing user $key."
            }
        }
    }

    fun fetchOrders() {
        viewModelScope.launch {
            _isLoadingOrders.value = true
            try {
                val user = SessionManager.user

                if (user != null && user.role != "ROLE_ADMIN") {
                    val response = repository.getUserOrders(user.id)
                    if (response.isSuccessful) {
                        val responseBody = response.body() as? List<OrderSummary>
                        if (responseBody != null) {
                            _userOrders.value = responseBody
                        } else {
                            _errorMessage.value = "Error while fetching the orders"
                        }
                    } else {
                        _errorMessage.value = "Error while fetching the orders"
                    }
                } else {
                    _errorMessage.value = "Session Error"
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException)
                    _errorMessage.value = "Error while fetching the orders: Connection problem."
                else
                    _errorMessage.value = "Error while fetching the orders."
            } finally {
                _isLoadingOrders.value = false
            }
        }

    }

    fun changePassword(password: PasswordUser){
        viewModelScope.launch {
            try {
                val user = SessionManager.user
                if (user != null) {
                    val response = repository.changePassword(user.id, password)

                    if(!response.isSuccessful){
                        val errorBody = response.errorBody()?.string()

                        _errorMessage.value = ErrorMessageParser(errorBody)
                    }
                } else {
                    _errorMessage.value = "Session Error."
                }

            } catch(e: Exception){
                if (e is SocketTimeoutException)
                    _errorMessage.value = "Error while updating password: Connection problem."
                else
                    _errorMessage.value = "Error while updating password."
            }
        }
    }

    fun logoutUser(userId: UUID, onLogout: ()->Unit){
        _isLoggingOut.value = true
        viewModelScope.launch {
            try{
                val response = repository.logout(userId)

                if(response.isSuccessful)
                {
                    onLogout()
                }
                else{
                    _errorMessage.value = "Logout Error."
                }
            }catch(e: Exception){
                if (e is SocketTimeoutException)
                    _errorMessage.value = "Logout Error: Connection problem."
                else
                    _errorMessage.value = "Logout Error."
            }finally{
                _isLoggingOut.value = false
            }
        }
    }

    fun deleteUser(userId: UUID, onDelete: ()->Unit){
        _isDeletingAccount.value = true
        viewModelScope.launch {
            try{
                val response = repository.deleteAccount(userId)

                if(response.isSuccessful)
                {
                    onDelete()
                }
                else{
                    _errorMessage.value = "Delete Error."
                }
            }catch(e: Exception){
                if (e is SocketTimeoutException)
                    _errorMessage.value = "Delete Error: Connection problem."
                else
                    _errorMessage.value = "Delete Error."
            }finally{
                _isDeletingAccount.value = false
            }
        }
    }

    fun onSnackbarDismissed(){
        _errorMessage.value = null
    }

    fun onLogout(){
        //viewModelScope.cancel()
        _userDetails.value = null
        _userOrders.value = emptyList()
    }
}