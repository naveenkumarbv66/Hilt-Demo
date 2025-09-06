package com.naveen.hiltdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naveen.hiltdemo.ui.theme.HiltDemoTheme
import com.naveen.hiltdemo.viewmodel.PersonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiltDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PersonScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PersonScreen(
    modifier: Modifier = Modifier,
    viewModel: PersonViewModel = viewModel()
) {
    val counter by viewModel.counter.collectAsState()
    val isCounting by viewModel.isCounting.collectAsState()
    val countComplete by viewModel.countComplete.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Person Activity",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            text = "Auto-increment Counter Demo",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        // Counter Display
        Text(
            text = "Count: $counter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 48.sp,
            modifier = Modifier.padding(24.dp)
        )
        
        // Progress indicator when counting
        if (isCounting) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Counting...",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
        
        // Completion message
        if (countComplete) {
            Text(
                text = "Count Complete! 🎉",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Control buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.startCounting() },
                enabled = !isCounting
            ) {
                Text(if (countComplete) "Count Again" else "Start Counting")
            }
            
            Button(
                onClick = { viewModel.resetCounter() },
                enabled = !isCounting
            ) {
                Text("Reset")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PersonScreenPreview() {
    HiltDemoTheme {
        PersonScreen()
    }
}
