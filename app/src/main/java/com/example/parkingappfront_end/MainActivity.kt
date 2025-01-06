package com.example.parkingappfront_end

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
//import androidx.compose.material3.BottomNavigation
//import androidx.compose.material3.BottomNavigationItem


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parkingappfront_end.model.SearchParams

import com.example.parkingappfront_end.network.RetrofitClient
import com.example.parkingappfront_end.repository.AccountRepository
import com.example.parkingappfront_end.repository.AdminRepository

import com.example.parkingappfront_end.repository.AuthRepository
import com.example.parkingappfront_end.repository.ParkingSpaceRep
import com.example.parkingappfront_end.repository.ParkingSpotRep
import com.example.parkingappfront_end.repository.ReservationRep
import com.example.parkingappfront_end.ui.theme.ParkingAppFrontEndTheme
import com.example.parkingappfront_end.ui.user.SignInUpScreen
import com.example.parkingappfront_end.ui.home.ParkingSearchScreen
import com.example.parkingappfront_end.ui.payment.PaymentScreen
import com.example.parkingappfront_end.ui.reservation.MainSearchResults
import com.example.parkingappfront_end.ui.reservation.MainStatisticView
import com.example.parkingappfront_end.ui.user.AccountManagerScreen
import com.example.parkingappfront_end.ui.user.MyAccountScreen
import com.example.parkingappfront_end.viewmodels.AccountViewModel
import com.example.parkingappfront_end.viewmodels.AdminViewModel
import com.example.parkingappfront_end.viewmodels.LoginViewModel
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import com.example.parkingappfront_end.viewmodels.RegistrationViewModel
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import kotlinx.coroutines.async
import java.time.LocalDateTime


class MainActivity : ComponentActivity() { // ComponentActivity è una classe di base per attività che utilizzano il framework di composizione
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) { // onCreate è un metodo che viene chiamato quando l'attività viene creata
        super.onCreate(savedInstanceState)
        setContent {
            ParkingAppFrontEndTheme { // ParkingAppFrontEndTheme è un tema personalizzato, fai CTRL + Click per visualizzare il codice
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController() // rememberNavController è una funzione di Compose che serve a mantenere il NavController in modo che non venga distrutto quando lo schermo viene ruotato
                    NavigationView(navController)
                    SessionManager.init(this) // Inizializza la sessione
                }
            }
        }

    }
}


@Composable // @Composable è un'annotazione che serve a creare un'interfaccia utente
fun NavigationView(navController: NavHostController) { // NavigationView è una funzione che serve a creare la navigazione
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedIndex = remember { mutableIntStateOf(0) }

    // Usa remember per mantenere i ViewModel
    val accountRepository = remember { AccountRepository(RetrofitClient.userApiService) }
    val reservationRepository = remember { ReservationRep(RetrofitClient.reservationApiService) }

    val adminViewModel = remember { AdminViewModel(repository = AdminRepository(RetrofitClient.adminApiService)) }
    val accountViewModel = remember { AccountViewModel(repository = accountRepository) } // AccountViewModel è una classe che serve a mantenere i dati dell'utente

    val parkingRepository = ParkingSpaceRep(RetrofitClient.parkingSpaceApiService)
    val parkingSpotRepository = ParkingSpotRep(RetrofitClient.parkingSpotApiService)
    val parkingViewModel = ParkingViewModel(parkingSpaceRep =  parkingRepository, parkingSpotRep =parkingSpotRepository )  // ParkingViewModel è una classe che serve a mantenere i dati del parcheggio
    val reservationViewModel = remember { ReservationViewModel(reservationRep = reservationRepository) } // ReservationViewModel è una classe che serve a mantenere i dati della prenotazione

    Scaffold(
        topBar = { TopBar(navController) }, // TopBar è una funzione che serve a creare la barra superiore
        bottomBar = { BottomBar(selectedIndex, navController) } // BottomBar è una funzione che serve a creare la barra inferiore
    ) { innerPadding -> // innerPadding è un parametro che serve a creare il padding
        NavHost(
            navController = navController,
            startDestination = if (SessionManager.user != null){
                                    if(SessionManager.user!!.role == "ROLE_OWNER") {
                                        "myParkingSpaces"
                                    }
                                    else {
                                        "search"
                                    }
                               }
                               else {
                                   "userAuth"
                               },
            // startDestination è una stringa che serve a indicare la destinazione iniziale
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("search") { //route = "home" è una stringa che serve a indicare la destinazione, quando si preme il pulsante Home si va alla destinazione "home"
                selectedIndex.value = 0 // selectedIndex.value = 0 è una funzione che serve a indicare che l'indice selezionato è 0

                ParkingSearchScreen(
                    onSearch = { city, startDate, endDate ->
                        // Logica di gestione dei dati di ricerca
                        parkingViewModel.loadParkingSpacesBySearch(city, startDate.toString(), endDate.toString())

                        val startDateStr = startDate.toString()
                        val endDateStr = endDate.toString()

                        navController.navigate("parkingResults/${city}/${startDateStr}/${endDateStr}") // Naviga con i parametri
                    },
                    parkingViewModel = parkingViewModel,
                    reservationViewModel = reservationViewModel
                )
            }

            composable(
                "parkingResults/{city}/{startDate}/{endDate}",
                arguments = listOf(
                    navArgument("city") { type = NavType.StringType },
                    navArgument("startDate") { type = NavType.StringType },
                    navArgument("endDate") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                // Recupera i parametri dalla backStackEntry
                val city = backStackEntry.arguments?.getString("city") ?: ""
                val startDateStr = backStackEntry.arguments?.getString("startDate") ?: ""
                val endDateStr = backStackEntry.arguments?.getString("endDate") ?: ""

                // Converte le stringhe in LocalDateTime
                val startDate = LocalDateTime.parse(startDateStr)
                val endDate = LocalDateTime.parse(endDateStr)


                // Chiama ParkingSpaceList
                MainSearchResults(
                    navController =  navController,
                    viewModel = parkingViewModel,
                    reservationViewModel =  reservationViewModel,
                    searchCriteria = SearchParams(city, startDate, endDate)
                )
            }

            composable("myParkingSpaces") {
                selectedIndex.value = 0

                parkingViewModel.loadParkingSpacesByOwner()

                MainSearchResults(
                    navController =  navController,
                    viewModel = parkingViewModel,
                    reservationViewModel =  reservationViewModel,
                    searchCriteria = null
                )

            }

            composable("myStats") {
                selectedIndex.value = 1

                parkingViewModel.loadParkingSpacesByOwner()

                MainStatisticView(
                    navController =  navController,
                    viewModel = parkingViewModel,
                    reservationViewModel =  reservationViewModel,
                )

            }


            composable("userAuth") {
                selectedIndex.value = 2
                val _authApiService = RetrofitClient.authApiService
                val repository = AuthRepository(_authApiService)
                SignInUpScreen(loginViewModel = LoginViewModel(repository), registrationViewModel = RegistrationViewModel(repository), navController)
            }

            composable("account-manager") {
                selectedIndex.value = 2

                LaunchedEffect(Unit) {
                    accountViewModel.loadUserDetails()
                }
                val _userApiService = RetrofitClient.userApiService
                val repository = AccountRepository(_userApiService)

                AccountManagerScreen(viewModel = accountViewModel, navHostController = navController, onLogout = {
                    accountViewModel.onLogout()
                    adminViewModel.onLogout()
                })
            }

            composable("my-account"){

                LaunchedEffect(Unit) {
                    Log.d("MyAccountScreen", "SessionManager.user: ${SessionManager.user}")
                    val userDetailsJob = async {accountViewModel.loadUserDetails()}
                    userDetailsJob.await()
                }

                MyAccountScreen(
                    accountViewModel = accountViewModel,
                    navController = navController, onLogout = {
                        accountViewModel.onLogout()
                        adminViewModel.onLogout()
                    })
            }



            composable("payment") { //route = "home" è una stringa che serve a indicare la destinazione, quando si preme il pulsante Home si va alla destinazione "home"
                selectedIndex.value = 3
                    //selectedIndex.value = 0 è una funzione che serve a indicare che l'indice selezionato è 0
                PaymentScreen(navController = navController)
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navHostController: NavHostController) {
    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBackIcon by remember(currentBackStackEntry) { derivedStateOf { navHostController.previousBackStackEntry != null } }
    val colorScheme = MaterialTheme.colorScheme

    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.app_name)
                    )
                }
            }
        },
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.statusBars.only(WindowInsetsSides.Top)
        ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary, // Usa il colore primario del tema
            titleContentColor = colorScheme.onPrimary, // Usa il colore onPrimary del tema
            navigationIconContentColor = colorScheme.onPrimary, // Usa il colore onPrimarydel tema
            actionIconContentColor = colorScheme.onPrimary // Usa il colore onPrimary del tema
        )
    )

}

@Composable
fun BottomBar(selectedIndex: MutableState<Int>, navHostController: NavHostController) {
    NavigationBar {

        if (SessionManager.user != null && SessionManager.user!!.role != "ROLE_OWNER") {
            NavigationBarItem(
                selected = selectedIndex.value == 0,
                onClick = {
                    selectedIndex.value = 0
                    navHostController.navigate("search") {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = stringResource(R.string.app_name)
                    )
                }

            )
        }
        if (SessionManager.user != null  && SessionManager.user!!.role == "ROLE_OWNER") {
            NavigationBarItem(
                selected = selectedIndex.value == 0,
                onClick = {
                    selectedIndex.value = 0
                    navHostController.navigate("myParkingSpaces") {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        Icons.Filled.LocalParking,
                        contentDescription = stringResource(R.string.app_name)
                    )
                }

            )
            NavigationBarItem(
                selected = selectedIndex.value == 1,
                onClick = {
                    selectedIndex.value = 1
                    navHostController.navigate("myStats") {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        Icons.Filled.AutoGraph,
                        contentDescription = stringResource(R.string.app_name)
                    )
                }

            )

        }
        NavigationBarItem(
            selected = selectedIndex.value == 2,
            onClick = {
                selectedIndex.value = 2

                if(SessionManager.user == null) //SessionManager.user == null
                    navHostController.navigate("userAuth") {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                else
                    navHostController.navigate("account-manager") {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
            },
            icon = {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = stringResource(R.string.app_name)
                )
            }

        )

    }
}
//commenti

