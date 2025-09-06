package com.naveen.hiltdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.hiltdemo.data.model.*
import com.naveen.hiltdemo.data.repository.NetworkRepository
import com.naveen.hiltdemo.worker.WorkManagerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val uiState: StateFlow<NetworkState> = _uiState.asStateFlow()
    
    // Data State
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()
    
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()
    
    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()
    
    // Network call results
    private val _lastApiResult = MutableStateFlow<String>("")
    val lastApiResult: StateFlow<String> = _lastApiResult.asStateFlow()
    
    // GET Operations
    fun getUsers() {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            when (val result = networkRepository.getUsers()) {
                is NetworkResult.Success -> {
                    _users.value = result.data
                    _uiState.value = NetworkState.Success("Users loaded successfully")
                    _lastApiResult.value = "GET Users: Success - ${result.data.size} users loaded"
                }
                is NetworkResult.Error -> {
                    _uiState.value = NetworkState.Error(result.message)
                    _lastApiResult.value = "GET Users: Error - ${result.message}"
                }
                is NetworkResult.Loading -> {
                    // This case should never occur as repository methods don't return Loading
                    _uiState.value = NetworkState.Error("Unexpected loading state")
                }
            }
        }
    }
    
    fun getUserById(id: Int) {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            when (val result = networkRepository.getUserById(id)) {
                is NetworkResult.Success -> {
                    _selectedUser.value = result.data
                    _uiState.value = NetworkState.Success("User loaded successfully")
                    _lastApiResult.value = "GET User by ID: Success - ${result.data.name}"
                }
                is NetworkResult.Error -> {
                    _uiState.value = NetworkState.Error(result.message)
                    _lastApiResult.value = "GET User by ID: Error - ${result.message}"
                }
                is NetworkResult.Loading -> {
                    // This case should never occur as repository methods don't return Loading
                    _uiState.value = NetworkState.Error("Unexpected loading state")
                }
            }
        }
    }
    
    fun getPosts() {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            when (val result = networkRepository.getPosts()) {
                is NetworkResult.Success -> {
                    _posts.value = result.data
                    _uiState.value = NetworkState.Success("Posts loaded successfully")
                    _lastApiResult.value = "GET Posts: Success - ${result.data.size} posts loaded"
                }
                is NetworkResult.Error -> {
                    _uiState.value = NetworkState.Error(result.message)
                    _lastApiResult.value = "GET Posts: Error - ${result.message}"
                }
                is NetworkResult.Loading -> {
                    // This case should never occur as repository methods don't return Loading
                    _uiState.value = NetworkState.Error("Unexpected loading state")
                }
            }
        }
    }
    
    fun getPostById(id: Int) {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            when (val result = networkRepository.getPostById(id)) {
                is NetworkResult.Success -> {
                    _selectedPost.value = result.data
                    _uiState.value = NetworkState.Success("Post loaded successfully")
                    _lastApiResult.value = "GET Post by ID: Success - ${result.data.title}"
                }
                is NetworkResult.Error -> {
                    _uiState.value = NetworkState.Error(result.message)
                    _lastApiResult.value = "GET Post by ID: Error - ${result.message}"
                }
                is NetworkResult.Loading -> {
                    // This case should never occur as repository methods don't return Loading
                    _uiState.value = NetworkState.Error("Unexpected loading state")
                }
            }
        }
    }
    
    fun getPostsByUserId(userId: Int) {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            when (val result = networkRepository.getPostsByUserId(userId)) {
                is NetworkResult.Success -> {
                    _posts.value = result.data
                    _uiState.value = NetworkState.Success("User posts loaded successfully")
                    _lastApiResult.value = "GET Posts by User ID: Success - ${result.data.size} posts loaded"
                }
                is NetworkResult.Error -> {
                    _uiState.value = NetworkState.Error(result.message)
                    _lastApiResult.value = "GET Posts by User ID: Error - ${result.message}"
                }
                is NetworkResult.Loading -> {
                    // This case should never occur as repository methods don't return Loading
                    _uiState.value = NetworkState.Error("Unexpected loading state")
                }
            }
        }
    }
    
    // POST Operations
    fun createUser(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            val userRequest = CreateUserRequest(name, email, phone)
            when (val result = networkRepository.createUser(userRequest)) {
                is NetworkResult.Success -> {
                    _uiState.value = NetworkState.Success("User created successfully")
                    _lastApiResult.value = "POST Create User: Success - ${result.data.name}"
                }
                is NetworkResult.Error -> {
                    _uiState.value = NetworkState.Error(result.message)
                    _lastApiResult.value = "POST Create User: Error - ${result.message}"
                }
                is NetworkResult.Loading -> {
                    // This case should never occur as repository methods don't return Loading
                    _uiState.value = NetworkState.Error("Unexpected loading state")
                }
            }
        }
    }
    
    fun createPost(title: String, body: String, userId: Int) {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            val postRequest = CreatePostRequest(
                title = title,
                body = body,
                userId = userId,
                image = null,
                file = null
            )
            when (val result = networkRepository.createPost(postRequest)) {
                is NetworkResult.Success -> {
                    _uiState.value = NetworkState.Success("Post created successfully")
                    _lastApiResult.value = "POST Create Post: Success - ${result.data.title}"
                }
                is NetworkResult.Error -> {
                    _uiState.value = NetworkState.Error(result.message)
                    _lastApiResult.value = "POST Create Post: Error - ${result.message}"
                }
                is NetworkResult.Loading -> {
                    // This case should never occur as repository methods don't return Loading
                    _uiState.value = NetworkState.Error("Unexpected loading state")
                }
            }
        }
    }
    
    // WorkManager Operations
    fun scheduleGetUsersWork() {
        workManagerHelper.scheduleGetUsersWork()
        _lastApiResult.value = "WorkManager: Scheduled GET Users work"
    }
    
    fun scheduleGetPostsWork() {
        workManagerHelper.scheduleGetPostsWork()
        _lastApiResult.value = "WorkManager: Scheduled GET Posts work"
    }
    
    fun scheduleCreateUserWork(name: String, email: String, phone: String) {
        workManagerHelper.scheduleCreateUserWork(name, email, phone)
        _lastApiResult.value = "WorkManager: Scheduled Create User work"
    }
    
    fun scheduleCreatePostWork(title: String, body: String, userId: Int) {
        workManagerHelper.scheduleCreatePostWork(title, body, userId)
        _lastApiResult.value = "WorkManager: Scheduled Create Post work"
    }
    
    fun schedulePeriodicSync() {
        workManagerHelper.schedulePeriodicSyncWork()
        _lastApiResult.value = "WorkManager: Scheduled periodic sync work"
    }
    
    // Utility methods
    fun clearResults() {
        _lastApiResult.value = ""
        _uiState.value = NetworkState.Idle
    }
    
    fun resetData() {
        _users.value = emptyList()
        _posts.value = emptyList()
        _selectedUser.value = null
        _selectedPost.value = null
        _lastApiResult.value = ""
        _uiState.value = NetworkState.Idle
    }
}
