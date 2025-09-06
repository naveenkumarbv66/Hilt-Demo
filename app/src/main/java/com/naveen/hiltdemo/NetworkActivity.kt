package com.naveen.hiltdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naveen.hiltdemo.data.model.*
import com.naveen.hiltdemo.ui.theme.HiltDemoTheme
import com.naveen.hiltdemo.viewmodel.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NetworkActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiltDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NetworkScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val workManagerState by viewModel.workManagerState.collectAsState()
    val users by viewModel.users.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val lastApiResult by viewModel.lastApiResult.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var userIdInput by remember { mutableStateOf("1") }
    var postIdInput by remember { mutableStateOf("1") }
    var userNameInput by remember { mutableStateOf("John Doe") }
    var userEmailInput by remember { mutableStateOf("john@example.com") }
    var userPhoneInput by remember { mutableStateOf("1234567890") }
    var postTitleInput by remember { mutableStateOf("Test Post") }
    var postBodyInput by remember { mutableStateOf("This is a test post body") }
    var postUserIdInput by remember { mutableStateOf("1") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Network API Testing",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Status Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // API Status
                Text(
                    text = "API Status: ${uiState::class.simpleName}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val currentState = uiState
                when (currentState) {
                    is NetworkState.Loading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                            Text(text = "Loading...")
                        }
                    }
                    is NetworkState.Success -> {
                        Text(
                            text = "✅ ${currentState.message}",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is NetworkState.Error -> {
                        Text(
                            text = "❌ ${currentState.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    is NetworkState.Idle -> {
                        Text(text = "Ready to make API calls")
                    }
                }
                
                // WorkManager Status
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "WorkManager Status: ${workManagerState::class.simpleName}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val currentWorkState = workManagerState
                when (currentWorkState) {
                    is NetworkState.Loading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                            Text(text = "WorkManager running...")
                        }
                    }
                    is NetworkState.Success -> {
                        Text(
                            text = "✅ WorkManager: ${currentWorkState.message}",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is NetworkState.Error -> {
                        Text(
                            text = "❌ WorkManager: ${currentWorkState.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    is NetworkState.Idle -> {
                        Text(text = "WorkManager ready")
                    }
                }
            }
        }
        
        // Tab Row
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("GET APIs") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("POST APIs") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("WorkManager") }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (selectedTab) {
            0 -> GetApiTab(
                users = users,
                posts = posts,
                userIdInput = userIdInput,
                postIdInput = postIdInput,
                onUserIdInputChange = { userIdInput = it },
                onPostIdInputChange = { postIdInput = it },
                onGetUsers = { viewModel.getUsers() },
                onGetUserById = { viewModel.getUserById(it) },
                onGetPosts = { viewModel.getPosts() },
                onGetPostById = { viewModel.getPostById(it) },
                onGetPostsByUserId = { viewModel.getPostsByUserId(it) }
            )
            1 -> PostApiTab(
                userNameInput = userNameInput,
                userEmailInput = userEmailInput,
                userPhoneInput = userPhoneInput,
                postTitleInput = postTitleInput,
                postBodyInput = postBodyInput,
                postUserIdInput = postUserIdInput,
                onUserNameInputChange = { userNameInput = it },
                onUserEmailInputChange = { userEmailInput = it },
                onUserPhoneInputChange = { userPhoneInput = it },
                onPostTitleInputChange = { postTitleInput = it },
                onPostBodyInputChange = { postBodyInput = it },
                onPostUserIdInputChange = { postUserIdInput = it },
                onCreateUser = { name, email, phone -> viewModel.createUser(name, email, phone) },
                onCreatePost = { title, body, userId -> viewModel.createPost(title, body, userId) }
            )
            2 -> WorkManagerTab(
                userNameInput = userNameInput,
                userEmailInput = userEmailInput,
                userPhoneInput = userPhoneInput,
                postTitleInput = postTitleInput,
                postBodyInput = postBodyInput,
                postUserIdInput = postUserIdInput,
                onUserNameInputChange = { userNameInput = it },
                onUserEmailInputChange = { userEmailInput = it },
                onUserPhoneInputChange = { userPhoneInput = it },
                onPostTitleInputChange = { postTitleInput = it },
                onPostBodyInputChange = { postBodyInput = it },
                onPostUserIdInputChange = { postUserIdInput = it },
                onScheduleGetUsers = { viewModel.scheduleGetUsersWork() },
                onScheduleGetPosts = { viewModel.scheduleGetPostsWork() },
                onScheduleCreateUser = { name, email, phone -> viewModel.scheduleCreateUserWork(name, email, phone) },
                onScheduleCreatePost = { title, body, userId -> viewModel.scheduleCreatePostWork(title, body, userId) },
                onSchedulePeriodicSync = { viewModel.schedulePeriodicSync() },
                onScheduleTestWork = { viewModel.scheduleTestWork() },
                onCancelCurrentWork = { viewModel.cancelCurrentWork() },
                onClearResults = { viewModel.clearResults() }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Last API Result
        if (lastApiResult.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Last API Result:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = lastApiResult,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun GetApiTab(
    users: List<User>,
    posts: List<Post>,
    userIdInput: String,
    postIdInput: String,
    onUserIdInputChange: (String) -> Unit,
    onPostIdInputChange: (String) -> Unit,
    onGetUsers: () -> Unit,
    onGetUserById: (Int) -> Unit,
    onGetPosts: () -> Unit,
    onGetPostById: (Int) -> Unit,
    onGetPostsByUserId: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Users Section
        Text(
            text = "Users API",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onGetUsers,
                modifier = Modifier.weight(1f)
            ) {
                Text("Get All Users")
            }
            
            OutlinedTextField(
                value = userIdInput,
                onValueChange = onUserIdInputChange,
                label = { Text("User ID") },
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = { onGetUserById(userIdInput.toIntOrNull() ?: 1) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Get User")
            }
        }
        
        if (users.isNotEmpty()) {
            Text(
                text = "Users (${users.size}):",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                items(users.take(5)) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "${user.id}: ${user.name} (${user.email})",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Posts Section
        Text(
            text = "Posts API",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onGetPosts,
                modifier = Modifier.weight(1f)
            ) {
                Text("Get All Posts")
            }
            
            OutlinedTextField(
                value = postIdInput,
                onValueChange = onPostIdInputChange,
                label = { Text("Post ID") },
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = { onGetPostById(postIdInput.toIntOrNull() ?: 1) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Get Post")
            }
        }
        
        Button(
            onClick = { onGetPostsByUserId(userIdInput.toIntOrNull() ?: 1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Posts by User ID")
        }
        
        if (posts.isNotEmpty()) {
            Text(
                text = "Posts (${posts.size}):",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                items(posts.take(5)) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "${post.id}: ${post.title}",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostApiTab(
    userNameInput: String,
    userEmailInput: String,
    userPhoneInput: String,
    postTitleInput: String,
    postBodyInput: String,
    postUserIdInput: String,
    onUserNameInputChange: (String) -> Unit,
    onUserEmailInputChange: (String) -> Unit,
    onUserPhoneInputChange: (String) -> Unit,
    onPostTitleInputChange: (String) -> Unit,
    onPostBodyInputChange: (String) -> Unit,
    onPostUserIdInputChange: (String) -> Unit,
    onCreateUser: (String, String, String) -> Unit,
    onCreatePost: (String, String, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Create User Section
        Text(
            text = "Create User",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = userNameInput,
            onValueChange = onUserNameInputChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = userEmailInput,
            onValueChange = onUserEmailInputChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = userPhoneInput,
            onValueChange = onUserPhoneInputChange,
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { onCreateUser(userNameInput, userEmailInput, userPhoneInput) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create User")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Create Post Section
        Text(
            text = "Create Post",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = postTitleInput,
            onValueChange = onPostTitleInputChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = postBodyInput,
            onValueChange = onPostBodyInputChange,
            label = { Text("Body") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = postUserIdInput,
            onValueChange = onPostUserIdInputChange,
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { onCreatePost(postTitleInput, postBodyInput, postUserIdInput.toIntOrNull() ?: 1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Post")
        }
    }
}

@Composable
fun WorkManagerTab(
    userNameInput: String,
    userEmailInput: String,
    userPhoneInput: String,
    postTitleInput: String,
    postBodyInput: String,
    postUserIdInput: String,
    onUserNameInputChange: (String) -> Unit,
    onUserEmailInputChange: (String) -> Unit,
    onUserPhoneInputChange: (String) -> Unit,
    onPostTitleInputChange: (String) -> Unit,
    onPostBodyInputChange: (String) -> Unit,
    onPostUserIdInputChange: (String) -> Unit,
    onScheduleGetUsers: () -> Unit,
    onScheduleGetPosts: () -> Unit,
    onScheduleCreateUser: (String, String, String) -> Unit,
    onScheduleCreatePost: (String, String, Int) -> Unit,
    onSchedulePeriodicSync: () -> Unit,
    onScheduleTestWork: () -> Unit,
    onCancelCurrentWork: () -> Unit,
    onClearResults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Background Tasks",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Button(
            onClick = onScheduleGetUsers,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Get Users Work")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onScheduleGetPosts,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Get Posts Work")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onSchedulePeriodicSync,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Periodic Sync")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onScheduleTestWork,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Test Simple Worker (No Dependencies)")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCancelCurrentWork,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cancel Work")
            }
            
            Button(
                onClick = onClearResults,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Clear Results")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Create User Work",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = userNameInput,
            onValueChange = onUserNameInputChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = userEmailInput,
            onValueChange = onUserEmailInputChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = userPhoneInput,
            onValueChange = onUserPhoneInputChange,
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { onScheduleCreateUser(userNameInput, userEmailInput, userPhoneInput) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Create User Work")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Create Post Work",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = postTitleInput,
            onValueChange = onPostTitleInputChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = postBodyInput,
            onValueChange = onPostBodyInputChange,
            label = { Text("Body") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = postUserIdInput,
            onValueChange = onPostUserIdInputChange,
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { onScheduleCreatePost(postTitleInput, postBodyInput, postUserIdInput.toIntOrNull() ?: 1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Create Post Work")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkScreenPreview() {
    HiltDemoTheme {
        NetworkScreen()
    }
}
