package com.example.parkingappfront_end.ui.user

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.UserDetails
import com.example.parkingappfront_end.viewmodels.AccountViewModel
//sam@gm.com
//Ciaobello!10

@Composable
fun AccountManagerScreen(viewModel: AccountViewModel, navHostController: NavHostController, onLogout: () -> Unit) {

    val userDetails by viewModel.userDetails.collectAsState()


    val isLoggingOut by viewModel.isLoggingOut.collectAsState()

    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            val result = viewModel.snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
                withDismissAction = false,
            )

            if (result == SnackbarResult.Dismissed) {
                viewModel.onSnackbarDismissed()
            }
        }
    }


    Scaffold(topBar = {
        TopAppBar(
            title = { androidx.compose.material.Text("Account Manager") },
            backgroundColor = Color(0xFF1F1F1F),
            contentColor = Color.White
        )
    },snackbarHost = { ; SnackbarHost(viewModel.snackbarHostState) }){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), verticalArrangement = Arrangement.SpaceAround
        ) {
            if(isLoggingOut){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }else {
                userDetails?.let { UserCard(it) }
                OptionsSection(navHostController)

                Buttons(viewModel, navHostController, onLogout = onLogout)
            }
        }
    }
}

@Composable
fun UserCard(userDetails: UserDetails){
    Row(modifier = Modifier.fillMaxWidth()) {
        Column() {
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = "Welcome back, ${userDetails.firstName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

        }
    }
}


@Composable
fun OptionsSection(navHostController: NavHostController){
    Box(modifier = Modifier.fillMaxWidth(),  contentAlignment = Alignment.Center)
    {
        Column {
            Row {
                Button(onClick = {navHostController.navigate("my-account"){
                    popUpTo("my-account") { saveState = true }
                }}) {
                    Text(text = "My Account")
                }
                if(SessionManager.user != null && SessionManager.user!!.role!="ROLE_ADMIN") {
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = {navHostController.navigate("orders"){
                        popUpTo("account-manager") { saveState = true }
                    }}) {
                        Text(text = "Past Reservations")
                    }
                }
            }
        }
    }
}




@Composable
fun Buttons(viewModel: AccountViewModel, navHostController: NavHostController, onLogout: ()->Unit){
    val context = LocalContext.current

    Column {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { navHostController.navigate("change-password"){
                popUpTo("account-manager") { saveState = true }
            }}) {
                Text(text = "Change Password")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                Log.d("LOGOUT DEBUG", "User: ${SessionManager.getUser()}")
                SessionManager.getUser().let { viewModel.logoutUser(it.userId, onLogout = {
                    onLogout()
                    SessionManager.clearSession()
                    })
                }
                navHostController.navigate("userAuth"){
                    popUpTo(0) {inclusive= true}
                }
            }) {
                Text(text = "Logout")
            }
        }
    }
}

/*
@Preview
@Composable
fun AccountScreenPreview(){
    val _userApiService = RetrofitClient.userApiService
    val repository = AccountRepository(_userApiService)
    val viewModel = AccountViewModel(repository)
    val navController = rememberNavController()
    AccountManagerScreen(viewModel, navHostController = navController)
}*/