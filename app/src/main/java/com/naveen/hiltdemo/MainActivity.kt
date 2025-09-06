package com.naveen.hiltdemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naveen.hiltdemo.service.GreetingService
import com.naveen.hiltdemo.ui.theme.HiltDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var greetingService: GreetingService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiltDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GreetingScreen(
                        greetingService = greetingService,
                        onNavigateToPerson = {
                            startActivity(Intent(this@MainActivity, PersonActivity::class.java))
                        },
                        onNavigateToNetwork = {
                            startActivity(Intent(this@MainActivity, NetworkActivity::class.java))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingScreen(
    greetingService: GreetingService,
    onNavigateToPerson: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = greetingService.getGreeting(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            text = greetingService.getPersonalizedGreeting("Android Developer"),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        Button(
            onClick = onNavigateToPerson,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Go to Person Activity")
        }
        
        Button(
            onClick = onNavigateToNetwork,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Go to Network Activity")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    HiltDemoTheme {
        // For preview, we'll create a mock service
        val mockService = object : GreetingService() {
            override fun getGreeting(): String = "Hello Hilt!"
            override fun getPersonalizedGreeting(name: String): String = "Hello $name from Hilt!"
        }
        GreetingScreen(
            greetingService = mockService,
            onNavigateToPerson = { /* Preview doesn't need navigation */ },
            onNavigateToNetwork = { /* Preview doesn't need navigation */ }
        )
    }
}