package com.naveen.hiltdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.hiltdemo.data.model.*
import com.naveen.hiltdemo.data.repository.NetworkRepository
import com.naveen.hiltdemo.worker.WorkManagerHelper
import com.naveen.hiltdemo.worker.WorkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
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
    
    // WorkManager state
    private val _workManagerState = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val workManagerState: StateFlow<NetworkState> = _workManagerState.asStateFlow()
    
    // Current work ID being observed
    private var currentWorkId: UUID? = null
    
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
        val workId = workManagerHelper.scheduleGetUsersWork()
        currentWorkId = workId
        _workManagerState.value = NetworkState.Loading
        _lastApiResult.value = "WorkManager: Scheduled GET Users work"
        
        // Observe work result
        observeWorkResult(workId, "GET Users")
    }
    
    fun scheduleGetPostsWork() {
        val workId = workManagerHelper.scheduleGetPostsWork()
        currentWorkId = workId
        _workManagerState.value = NetworkState.Loading
        _lastApiResult.value = "WorkManager: Scheduled GET Posts work"
        
        // Observe work result
        observeWorkResult(workId, "GET Posts")
    }
    
    fun scheduleCreateUserWork(name: String, email: String, phone: String) {
        val workId = workManagerHelper.scheduleCreateUserWork(name, email, phone)
        currentWorkId = workId
        _workManagerState.value = NetworkState.Loading
        _lastApiResult.value = "WorkManager: Scheduled Create User work"
        
        // Observe work result
        observeWorkResult(workId, "Create User")
    }
    
    fun scheduleCreatePostWork(title: String, body: String, userId: Int) {
        val workId = workManagerHelper.scheduleCreatePostWork(title, body, userId)
        currentWorkId = workId
        _workManagerState.value = NetworkState.Loading
        _lastApiResult.value = "WorkManager: Scheduled Create Post work"
        
        // Observe work result
        observeWorkResult(workId, "Create Post")
    }
    
    fun schedulePeriodicSync() {
        workManagerHelper.schedulePeriodicSyncWork()
        _lastApiResult.value = "WorkManager: Scheduled periodic sync work"
    }
    
    fun scheduleTestWork() {
        val workId = workManagerHelper.scheduleTestWork()
        currentWorkId = workId
        _workManagerState.value = NetworkState.Loading
        _lastApiResult.value = "WorkManager: Scheduled test work"
        
        // Observe work result
        observeWorkResult(workId, "Test Work")
    }
    
    // Private method to observe work results
    private fun observeWorkResult(workId: UUID, operationName: String) {
        workManagerHelper.observeWorkResultWithData(workId)
            .onEach { workResult ->
                when (workResult) {
                    is WorkResult.Success -> {
                        _workManagerState.value = NetworkState.Success(workResult.message)
                        _lastApiResult.value = "WorkManager $operationName: Success - ${workResult.message}"
                    }
                    is WorkResult.Error -> {
                        _workManagerState.value = NetworkState.Error(workResult.message)
                        _lastApiResult.value = "WorkManager $operationName: Error - ${workResult.message}"
                    }
                    is WorkResult.Cancelled -> {
                        _workManagerState.value = NetworkState.Error("Work cancelled: ${workResult.message}")
                        _lastApiResult.value = "WorkManager $operationName: Cancelled - ${workResult.message}"
                    }
                    is WorkResult.Running -> {
                        _workManagerState.value = NetworkState.Loading
                        _lastApiResult.value = "WorkManager $operationName: Running - ${workResult.message}"
                    }
                    is WorkResult.Queued -> {
                        _workManagerState.value = NetworkState.Loading
                        _lastApiResult.value = "WorkManager $operationName: Queued - ${workResult.message}"
                    }
                    is WorkResult.Blocked -> {
                        _workManagerState.value = NetworkState.Error("Work blocked: ${workResult.message}")
                        _lastApiResult.value = "WorkManager $operationName: Blocked - ${workResult.message}"
                    }
                    is WorkResult.Unknown -> {
                        _workManagerState.value = NetworkState.Error("Unknown work state: ${workResult.message}")
                        _lastApiResult.value = "WorkManager $operationName: Unknown - ${workResult.message}"
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    // Utility methods
    fun clearResults() {
        _lastApiResult.value = ""
        _uiState.value = NetworkState.Idle
        _workManagerState.value = NetworkState.Idle
    }
    
    fun resetData() {
        _users.value = emptyList()
        _posts.value = emptyList()
        _selectedUser.value = null
        _selectedPost.value = null
        _lastApiResult.value = ""
        _uiState.value = NetworkState.Idle
        _workManagerState.value = NetworkState.Idle
        currentWorkId = null
    }
    
    fun cancelCurrentWork() {
        currentWorkId?.let { workId ->
            workManagerHelper.cancelWork(workId)
            _workManagerState.value = NetworkState.Error("Work cancelled by user")
            _lastApiResult.value = "WorkManager: Work cancelled"
            currentWorkId = null
        }
    }
}
