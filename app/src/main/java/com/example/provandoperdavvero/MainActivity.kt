package com.example.provandoperdavvero

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.Alignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text


import androidx.compose.ui.tooling.preview.Preview
import com.example.provandoperdavvero.ui.theme.ProvandoPerDavveroTheme



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
//import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.compose.foundation.Image

import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.foundation.border



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProvandoPerDavveroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") { LoginScreen(navController) }
                        composable("home") { HomeScreen(navController) }
                        composable("prenotazione") { PrenotazioneScreen(navController) }
                        composable("pagamento") { PagamentoScreen(navController) }

                    }
                }
            }
        }
    }
}










@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    // Creiamo un NavController fittizio per l'anteprima
    val navController = rememberNavController()
    LoginScreen(navController)
}


// Aggiungi questo fuori dalla funzione LoginScreen
object UserCredentials {
    var registeredEmail = ""
    var registeredPassword = ""
}




@Composable
fun LoginScreen(navController: NavController) {
    // Variabili per la registrazione
    var username by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Variabili per il login
    var loginEmail by remember { mutableStateOf(UserCredentials.registeredEmail) }
    var loginPassword by remember { mutableStateOf(UserCredentials.registeredPassword) }
    var loginMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PARKING APP",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Sezione di registrazione ---

        Text("Registrazione", style = MaterialTheme.typography.titleMedium)

        // Nome Utente
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nome Utente") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Email per registrazione
        TextField(
            value = registerEmail,
            onValueChange = { registerEmail = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password per registrazione
        TextField(
            value = registerPassword,
            onValueChange = { registerPassword = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Conferma Password
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Conferma Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Pulsante di Registrazione
        Button(onClick = {
            loginMessage = if (registerPassword == confirmPassword) {
                UserCredentials.registeredEmail = registerEmail
                UserCredentials.registeredPassword = registerPassword
                "Registrazione riuscita"
            } else {
                "Le password non corrispondono"
            }
        }) {
            Text("Registrati")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Sezione di accesso ---

        Text("Accesso", style = MaterialTheme.typography.titleMedium)

        // Email per login (precompilata se esiste una registrazione)
        TextField(
            value = loginEmail,
            onValueChange = { loginEmail = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password per login (precompilata se esiste una registrazione)
        TextField(
            value = loginPassword,
            onValueChange = { loginPassword = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Pulsante di Login
        Button(onClick = {
            if (loginEmail == UserCredentials.registeredEmail &&
                loginPassword == UserCredentials.registeredPassword) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                loginMessage = "Email o password errati"
            }
        }) {
            Text("Accedi")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Messaggio di feedback
        Text(text = loginMessage, color = MaterialTheme.colorScheme.error)
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    ProvandoPerDavveroTheme {
        HomeScreen(navController)
    }
}



@Composable
fun HomeScreen(navController: NavController) {
    var vehiclePlate by remember { mutableStateOf("") }  // Stato per gestire l'input della targa

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titolo
        Text(
            text = "BENVENUTO SU PARKING APP",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Immagine in alto
        Image(
            painter = painterResource(id = R.drawable.parcheggio),
            contentDescription = "Mappa del parcheggio",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)  // Altezza dell'immagine (regolabile)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Inserisci la targa del veicolo e prenota il tuo parcheggio!",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))



        // Campo di input per la targa del veicolo
        TextField(
            value = vehiclePlate,
            onValueChange = { vehiclePlate = it },
            label = { Text("Inserisci la targa del veicolo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Pulsante "Prenota"
        Button(
            onClick = {
                println("Targa inserita: $vehiclePlate")  // Log per il debug
                navController.navigate("prenotazione")
            },
            enabled = vehiclePlate.isNotBlank()  // Abilita il pulsante solo se la targa è compilata
        ) {
            Text("Prenota")
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewPrenotazioneScreen() {
    ProvandoPerDavveroTheme {
        PrenotazioneScreen(rememberNavController())
    }
}

@Composable
fun PrenotazioneScreen(navController: NavController) {
    // Stato per gestire la visualizzazione del messaggio di conferma
    var prenotazioneConfermata by remember { mutableStateOf(false) }

    // Stato per memorizzare le selezioni dell'utente
    var colonnaSelezionata by remember { mutableStateOf("") }
    var numeroSelezionato by remember { mutableStateOf(0) }
    var oreSelezionate by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sei pronto per prenotare il tuo parcheggio?",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Menu a tendina Scegli colonna
        Spinner(
            label = "Scegli colonna",
            selectedItem = colonnaSelezionata.ifEmpty { "Scegli colonna" },
            items = listOf("A", "B", "C", "D"),
            onItemSelected = { colonnaSelezionata = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menu a tendina Seleziona numero
        Spinner(
            label = "Seleziona numero",
            selectedItem = numeroSelezionato.takeIf { it != 0 }?.toString() ?: "Seleziona numero",
            items = (1..10).map { it.toString() },
            onItemSelected = { numeroSelezionato = it.toInt() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menu a tendina Seleziona ore
        Spinner(
            label = "Seleziona ore",
            selectedItem = oreSelezionate.takeIf { it != 0 }?.toString() ?: "Seleziona ore",
            items = (1..24).map { it.toString() },
            onItemSelected = { oreSelezionate = it.toInt() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pulsante Conferma Prenotazione
        Button(onClick = {
            prenotazioneConfermata = true
        }) {
            Text("Conferma Prenotazione")
        }

        // Mostra il messaggio di conferma se prenotazioneConfermata è true
        if (prenotazioneConfermata) {
            Spacer(modifier = Modifier.height(16.dp))

            // Riquadro con sfondo blu per il recap
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
                    .border(2.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Prenotazione Confermata",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hai prenotato la colonna $colonnaSelezionata, numero $numeroSelezionato per $oreSelezionate ore.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pulsante per tornare alla home
            Button(
                onClick = {
                    navController.navigate("pagamento") {
                        // Navigazione con il popbackstack per rimuovere la schermata corrente dalla pila
                        popUpTo("prenotazione") { inclusive = true }
                    }
                }
            ) {
                Text("Procedi al Pagamento")
            }
        }
    }
}




@Composable
fun Spinner(
    label: String,
    selectedItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.ArrowDropDown

    Column {
        // Mostra la selezione attuale sopra il menu
        Text(
            text = "$label: $selectedItem",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mostra il label del menu (es. "Scegli colonna")
            Text(label)
            Icon(imageVector = icon, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp) // Imposta una altezza massima per il menu
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier
                        .height(40.dp), // Imposta l'altezza dell'item per ridurre la dimensione del menu
                    text = { Text(item, style = MaterialTheme.typography.bodySmall) } // Imposta una dimensione più piccola per il testo
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewPagamentoScreen() {
    ProvandoPerDavveroTheme {
        PagamentoScreen(rememberNavController())
    }
}


@Composable
fun PagamentoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Benvenuto nella schermata di pagamento",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("home")
        }) {
            Text("Torna alla Home")
        }
    }
}







