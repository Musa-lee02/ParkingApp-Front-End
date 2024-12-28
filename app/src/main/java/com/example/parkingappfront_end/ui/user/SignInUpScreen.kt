package com.example.parkingappfront_end.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingappfront_end.viewmodels.RegistrationViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import com.example.parkingappfront_end.model.Credential
import com.example.parkingappfront_end.viewmodels.LoginViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun SignInUpScreen(loginViewModel: LoginViewModel, registrationViewModel: RegistrationViewModel, navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) } //Serve per tenere traccia della tab selezionata per mostrare la pagina di accesso o di registrazione

    Column(modifier = Modifier.fillMaxSize()) { //Crea una colonna che occupa tutto lo spazio disponibile
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab( //Serve per visualizzare le tab di accesso e registrazione
            selected = selectedTabIndex == 0,
            onClick = { selectedTabIndex = 0 },
            text = { Text("Accedi") }
        )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Registrati") }
            )
        }

        // Il resto del codice per visualizzare LoginPage o RegistrationScreen
        when (selectedTabIndex) { //In base alla tab selezionata mostra la pagina di accesso o di registrazione
            0 -> LoginPage(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("search") { //Se il login ha successo naviga alla schermata home
                        popUpTo("search") { inclusive = true } //Chiude tutte le schermate precedenti
                    }
                }
            )
            1 -> RegistrationScreen( // Mostra la schermata di registrazione
                registrationViewModel,
                onRegistrationComplete = {
                    navController.navigate("userAuth") { //Se la registrazione ha successo naviga alla schermata di autenticazione
                        popUpTo("userAuth") { inclusive = true }
                    }
                },
                onSwitchToLogin = { selectedTabIndex = 0 }//Permette di tornare alla schermata di accesso quando si è già registrati
            )
        }
    }
}

@Composable
fun LoginPage(loginViewModel: LoginViewModel, onLoginSuccess: () -> Unit) { //Pagina di accesso
    var email by remember { mutableStateOf("") } //Variabili per tenere traccia dell'email e della password
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } //Variabile per mostrare o nascondere la password

    var isEmailValid by remember { mutableStateOf(false) } //Variabili per controllare se l'email e la password sono valide
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") //Regex per la validazione dell'email

    var isPasswordValid by remember { mutableStateOf(false) } //Variabile per controllare se la password è valida

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bentornato!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { //Controlla se l'email è valida
                email = it
                isEmailValid = emailRegex.matches(it)
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp), //Imposta i bordi arrotondati
            singleLine = true  //Permette di scrivere su una sola riga
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordValid =
                    it.length in 8..20   // Controlla se la password ha almeno 8 caratteri
            },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None //Mostra o nasconde la password
                                    else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { //Quando si preme il pulsante di accesso
                loginViewModel.login(Credential(email, password), onLoginSuccess) //Esegue il login
            },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = 16.dp),
            enabled = isEmailValid && isPasswordValid //Il pulsante è abilitato solo se l'email e la password sono valide, le variabili di sopra
        ) {
            Text("Accedi", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun RegistrationScreen(registrationViewModel : RegistrationViewModel, onRegistrationComplete: () -> Unit, onSwitchToLogin: () -> Unit) {
    var currentStep by remember { mutableStateOf(1) } //Variabile per tenere traccia dello step corrente (2 step inseriti per adesso)

    Column(modifier = Modifier.padding(16.dp)) {
        when (currentStep) {
            1 -> RegistrationStep1(registrationViewModel, onNext = { currentStep = 2 }) //Mostra il primo step e passa al secondo quando si preme il pulsante
            2 -> RegistrationStep2(registrationViewModel = registrationViewModel, onRegistrationComplete, onNext = { currentStep = 3 }, onBack = { currentStep = 1 })
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (currentStep == 1) { //Se si è nel primo step mostra il pulsante per passare al secondo
            TextButton(onClick = onSwitchToLogin) {
                Text("Hai già un account? Accedi")
            }
        }
    }
}

@Composable
fun RegistrationStep1(registrationViewModel: RegistrationViewModel, onNext: () -> Unit) {
    var name by remember { mutableStateOf("") }   // Variabili per tenere traccia del nome, cognome, email, password e conferma password
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("")}
    var passwordVisible by remember { mutableStateOf(false) }

    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isConfirmPasswordValid by remember { mutableStateOf(true) }

    // Regex per la validazione dell'email
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") //Regex per la validazione dell'email

    // Regex per la validazione della password
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])[\\w@#$%^&+=!]{8,20}$")

    val isFormValid = isEmailValid && isPasswordValid && isConfirmPasswordValid // Il form è valido se l'email, la password e la conferma password sono valide e non vuote
            && name.isNotBlank() && surname.isNotBlank() && email.isNotBlank()
            && password.isNotBlank() && confirmPassword.isNotBlank()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Step 1 su 2", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Cognome") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = emailRegex.matches(it)
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                isError = !isEmailValid
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isPasswordValid = passwordRegex.matches(it)
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password"
                        )
                    }
                },

                isError = !isPasswordValid // Mostra un errore se la password non è valida
            )
            Text(
                text = "La password deve contenere minimo 8 caratteri e massimo 20, di cui una lettera maiuscola, " +
                        "una lettera minuscola, un numero e un carattere speciale",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .offset(x = 8.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    isConfirmPasswordValid = (it == password) // Controlla se le password corrispondono
                },
                label = { Text("Conferma Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility
                                            else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Nascondi password"
                                                else "Mostra password"
                        )
                    }
                },
                isError = !isConfirmPasswordValid // Mostra un errore se le password non corrispondono
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (password == confirmPassword) {// Se le password corrispondono
                        registrationViewModel.updateUserDetails(name, surname, email, password) // Aggiorna i dettagli dell'utente
                        onNext() // Passa al prossimo step
                    } else {
                        // Mostra un messaggio di errore: le password non corrispondono
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                 enabled = isFormValid
            ) {
                Text("Continua->", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationStep2(registrationViewModel: RegistrationViewModel, onRegistrationComplete: () -> Unit, onNext: () -> Unit, onBack: () -> Unit) {

    val minYear = 1910
    val maxDate = LocalDate.now().minusYears(16)

    var selectedDate by remember { mutableStateOf(maxDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var admin by remember { mutableStateOf("") }

    fun isValidDate(date: LocalDate): Boolean = date.isBefore(LocalDate.now())

    /*
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }*/


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Step 2 / 2", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(10.dp))

            // Data di nascita
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onValueChange = { },
                label = { Text("Birth Date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Select data")
                    }
                }
            )
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },confirmButton = {
                        Button(onClick = { showDatePicker = false }) {
                            Text("OK")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {


                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault())
                            .toInstant().toEpochMilli(),
                        yearRange = IntRange(minYear, maxDate.year),
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                val date = Instant.ofEpochMilli(utcTimeMillis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                return !date.isAfter(maxDate)
                            }
                        }
                    )

                    // Aggiorna selectedDate quando la data selezionata cambia
                    LaunchedEffect(datePickerState.selectedDateMillis) {
                        if (datePickerState.selectedDateMillis != null) {
                            selectedDate = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    }

                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = admin,
                onValueChange = { admin = it },
                label = { Text("ADMIN Code") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Text(
                text = "Insert Admin Code",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .offset(x = 8.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onBack) {
                    Text("Indietro")
                }

                Button(onClick = {
                    registrationViewModel.updateUserDetails(selectedDate, admin)
                    registrationViewModel.register(onRegistrationComplete)
                    onNext() // Passa al prossimo step
                },
                    enabled = true
                ) {
                    Text("Complete Sing Up")
                }
            }
        }
    }
}



// (NON ANCORA USATO) Serve per vedere i dati dell utente una volta registrato e per modificarle
@Composable
fun UserDetailsScreen(navController: NavController, viewModel: RegistrationViewModel) {
    var name by remember { mutableStateOf(viewModel.registrationData.value.name) }
    var email by remember { mutableStateOf(viewModel.registrationData.value.email) }
    var password by remember { mutableStateOf(viewModel.registrationData.value.password) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dettagli Utente", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome e Cognome") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password"
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )

        Button(
            onClick = {
                // Salva i dati nel ViewModel
                viewModel.registrationData.value = viewModel.registrationData.value.copy(
                    name = name,
                    email = email,
                    password = password
                )
                // Naviga alla schermata successiva
                navController.navigate("shippingAddress")
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Continua")
        }
    }
}




