package com.naveen.hiltdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.naveen.hiltdemo.datastore.SecureDataStore
import com.naveen.hiltdemo.ui.theme.HiltDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DataStoreActivity : ComponentActivity() {
    
    @Inject
    lateinit var secureDataStore: SecureDataStore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiltDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DataStoreScreen(
                        modifier = Modifier.padding(innerPadding),
                        secureDataStore = secureDataStore
                    )
                }
            }
        }
    }
}

@Composable
fun DataStoreScreen(
    modifier: Modifier = Modifier,
    secureDataStore: SecureDataStore
) {
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var userSettings by remember { mutableStateOf("") }
    var isSensitive by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Read current values
    val currentUserName by secureDataStore.getUserName(isSensitive).collectAsState(initial = "")
    val currentUserEmail by secureDataStore.getUserEmail(isSensitive).collectAsState(initial = "")
    val currentUserPhone by secureDataStore.getUserPhone(isSensitive).collectAsState(initial = "")
    val currentUserAge by secureDataStore.getUserAge(isSensitive).collectAsState(initial = 0)
    val currentUserSettings by secureDataStore.getUserSettings(isSensitive).collectAsState(initial = "")
    val isLoggedIn by secureDataStore.getLoginStatus().collectAsState(initial = false)
    val lastLoginTime by secureDataStore.getLastLoginTime().collectAsState(initial = 0L)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Secure DataStore Operations",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Status Message
        if (statusMessage.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (statusMessage.contains("Success")) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(16.dp),
                    color = if (statusMessage.contains("Success")) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        // Sensitive Data Toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isSensitive,
                    onCheckedChange = { isSensitive = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSensitive) "Encrypted Storage" else "Regular Storage",
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Input Fields
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "User Data Input",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = userPhone,
                    onValueChange = { userPhone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = userAge,
                    onValueChange = { userAge = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = userSettings,
                    onValueChange = { userSettings = it },
                    label = { Text("Settings (JSON)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }
        
        // Action Buttons
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (userName.isNotEmpty() && userEmail.isNotEmpty() && userPhone.isNotEmpty() && userAge.isNotEmpty()) {
                                isLoading = true
                                statusMessage = "Saving user data..."
                                // This will be handled in the coroutine scope
                            } else {
                                statusMessage = "Please fill in all required fields"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Save User Data")
                    }
                    
                    Button(
                        onClick = {
                            isLoading = true
                            statusMessage = "Setting login status..."
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Set Login")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            isLoading = true
                            statusMessage = "Clearing all data..."
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Clear All")
                    }
                    
                    Button(
                        onClick = {
                            isLoading = true
                            statusMessage = "Refreshing data..."
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("Refresh")
                    }
                }
            }
        }
        
        // Current Data Display
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Current Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                DataRow("Name", currentUserName)
                DataRow("Email", currentUserEmail)
                DataRow("Phone", currentUserPhone)
                DataRow("Age", currentUserAge.toString())
                DataRow("Settings", currentUserSettings)
                DataRow("Logged In", isLoggedIn.toString())
                DataRow("Last Login", if (lastLoginTime > 0) java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(lastLoginTime)) else "Never")
            }
        }
    }
    
    // Handle coroutine operations
    LaunchedEffect(Unit) {
        // This will be triggered when the component is first composed
    }
    
    // Handle button clicks with coroutines
    LaunchedEffect(isLoading) {
        if (isLoading) {
            when (statusMessage) {
                "Saving user data..." -> {
                    try {
                        secureDataStore.saveUserData(
                            userName, userEmail, userPhone, 
                            userAge.toIntOrNull() ?: 0, isSensitive
                        )
                        if (userSettings.isNotEmpty()) {
                            secureDataStore.saveUserSettings(userSettings, isSensitive)
                        }
                        statusMessage = "Success: User data saved ${if (isSensitive) "securely" else "normally"}"
                    } catch (e: Exception) {
                        statusMessage = "Error: ${e.message}"
                    }
                    isLoading = false
                }
                "Setting login status..." -> {
                    try {
                        secureDataStore.setLoginStatus(true)
                        statusMessage = "Success: Login status set"
                    } catch (e: Exception) {
                        statusMessage = "Error: ${e.message}"
                    }
                    isLoading = false
                }
                "Clearing all data..." -> {
                    try {
                        secureDataStore.clearAll()
                        statusMessage = "Success: All data cleared"
                        userName = ""
                        userEmail = ""
                        userPhone = ""
                        userAge = ""
                        userSettings = ""
                    } catch (e: Exception) {
                        statusMessage = "Error: ${e.message}"
                    }
                    isLoading = false
                }
                "Refreshing data..." -> {
                    try {
                        // Data will be refreshed automatically through the flows
                        statusMessage = "Success: Data refreshed"
                    } catch (e: Exception) {
                        statusMessage = "Error: ${e.message}"
                    }
                    isLoading = false
                }
            }
        }
    }
}

@Composable
fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(120.dp),
            fontSize = 12.sp
        )
        Text(
            text = value.ifEmpty { "Not set" },
            modifier = Modifier.weight(1f),
            fontSize = 12.sp
        )
    }
}
