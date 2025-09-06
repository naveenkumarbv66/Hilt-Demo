# Hilt Demo Android App

A comprehensive Android application demonstrating Hilt dependency injection, MVVM architecture, networking with Retrofit, WorkManager, secure data storage with DataStore, and Jetpack Compose UI.

## Overview

This project showcases modern Android development practices including:
- **Hilt Dependency Injection** for clean architecture
- **MVVM Pattern** with ViewModels and StateFlow
- **Networking** with Retrofit, OkHttp, and JSON parsing
- **Background Processing** with WorkManager
- **Secure Data Storage** with DataStore and EncryptedSharedPreferences
- **Jetpack Compose UI** with Material3 design
- **Repository Pattern** for data management

## Features

### 🏗️ **Architecture & Dependency Injection**
- **Hilt Integration**: Complete dependency injection setup
- **MVVM Pattern**: ViewModels with reactive state management
- **Repository Pattern**: Clean data layer abstraction
- **Clean Architecture**: Proper separation of concerns

### 🌐 **Networking & API Management**
- **Retrofit Integration**: RESTful API calls with type safety
- **JSON Parsing**: Automatic serialization/deserialization with Gson
- **File Upload**: Support for images and files with MultipartBody
- **Error Handling**: Comprehensive error management
- **Network Monitoring**: Internet connectivity checks

### ⚙️ **Background Processing**
- **WorkManager**: Reliable background task execution
- **Network Constraints**: Tasks only run when connected
- **Retry Policies**: Automatic retry on failure
- **Periodic Sync**: Scheduled background operations

### 🔐 **Secure Data Storage**
- **DataStore Integration**: Modern preferences storage
- **Encrypted Storage**: AES256 encryption for sensitive data
- **Dual Storage System**: Regular DataStore + EncryptedSharedPreferences
- **Reactive Updates**: Real-time UI updates with Flow
- **Simple API**: Easy-to-use utility classes

### 🎨 **Modern UI**
- **Jetpack Compose**: Declarative UI toolkit
- **Material3 Design**: Modern design system
- **Reactive UI**: Real-time updates with StateFlow
- **Tabbed Interface**: Organized feature navigation

## Project Structure

```
app/
├── src/main/java/com/naveen/hiltdemo/
│   ├── MainActivity.kt              # Main activity with navigation
│   ├── PersonActivity.kt            # Counter demo with ViewModel
│   ├── NetworkActivity.kt           # Comprehensive networking demo
│   ├── DataStoreActivity.kt         # Secure data storage demo
│   ├── HiltDemoApplication.kt       # Application class with @HiltAndroidApp
│   ├── service/
│   │   └── GreetingService.kt       # Service class for dependency injection
│   ├── data/
│   │   ├── model/                   # Data models for JSON parsing
│   │   │   ├── ApiResponse.kt       # Generic API response wrapper
│   │   │   └── NetworkResult.kt     # Network result sealed classes
│   │   ├── api/
│   │   │   └── ApiService.kt        # Retrofit API service interface
│   │   └── repository/
│   │       └── NetworkRepository.kt # Repository for network operations
│   ├── datastore/
│   │   ├── SecureDataStore.kt       # Hilt-managed secure data storage
│   │   └── DataStoreUtils.kt        # Utility class for easy access
│   ├── di/
│   │   └── NetworkModule.kt         # Hilt network dependencies
│   ├── viewmodel/
│   │   ├── PersonViewModel.kt       # Counter ViewModel
│   │   └── NetworkViewModel.kt      # Network operations ViewModel
│   ├── worker/
│   │   ├── NetworkWorker.kt         # WorkManager background tasks
│   │   └── WorkManagerHelper.kt     # WorkManager utility class
│   └── ui/theme/                    # Compose theming
├── build.gradle.kts                 # App-level dependencies
└── AndroidManifest.xml              # Manifest with permissions
```

## Key Components

### 1. **HiltDemoApplication**
```kotlin
@HiltAndroidApp
class HiltDemoApplication : Application()
```
- Entry point for Hilt dependency injection
- Annotated with `@HiltAndroidApp` to generate the necessary Dagger components

### 2. **GreetingService** (Basic Hilt Demo)
```kotlin
@Singleton
open class GreetingService @Inject constructor() {
    open fun getGreeting(): String = "Hello Hilt!"
    open fun getPersonalizedGreeting(name: String): String = "Hello $name from Hilt!"
}
```
- Service class with `@Singleton` annotation for single instance
- Constructor injection with `@Inject`

### 3. **PersonViewModel** (Counter Demo)
```kotlin
@HiltViewModel
class PersonViewModel @Inject constructor() : ViewModel() {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()
    
    fun startCounting() { /* Auto-increment 1-10 */ }
}
```
- ViewModel with Hilt integration
- Auto-incrementing counter from 1 to 10
- Reactive state management with StateFlow

### 4. **NetworkRepository** (API Management)
```kotlin
@Singleton
class NetworkRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUsers(): NetworkResult<List<User>> {
        return safeApiCall { apiService.getUsers() }
    }
    // ... more API methods
}
```
- Repository pattern for clean data access
- Safe API calls with error handling
- Support for all CRUD operations

### 5. **NetworkViewModel** (MVVM Pattern)
```kotlin
@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {
    // Reactive state management
    // All network operations
    // WorkManager integration
}
```
- Complete MVVM implementation
- Reactive UI with StateFlow
- Background task management

### 6. **NetworkWorker** (Background Processing)
```kotlin
@HiltWorker
class NetworkWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkRepository: NetworkRepository
) : CoroutineWorker(context, workerParams) {
    // Background network operations
}
```
- WorkManager for reliable background tasks
- Hilt integration for dependency injection
- Network constraints and retry policies

### 7. **SecureDataStore** (Secure Data Storage)
```kotlin
@Singleton
class SecureDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun writeString(key: String, value: String, isSensitive: Boolean = false)
    fun readString(key: String, defaultValue: String = "", isSensitive: Boolean = false): Flow<String>
    suspend fun deleteString(key: String, isSensitive: Boolean = false)
    // ... more operations
}
```
- Hilt-managed singleton for secure data operations
- Dual storage: DataStore + EncryptedSharedPreferences
- AES256 encryption for sensitive data
- Reactive updates with Flow

### 8. **DataStoreUtils** (Simple Data Access)
```kotlin
object DataStoreUtils {
    suspend fun writeString(context: Context, key: String, value: String, encrypted: Boolean = false)
    fun readString(context: Context, key: String, defaultValue: String = "", encrypted: Boolean = false): Flow<String>
    // ... more operations
}
```
- Static utility class for easy access anywhere
- No dependency injection required
- Same security features as SecureDataStore

## Dependencies

The project uses the following key dependencies:

### **Core Dependencies**
- **Hilt Android**: `com.google.dagger:hilt-android:2.48`
- **Hilt Compiler**: `com.google.dagger:hilt-compiler:2.48`
- **Hilt Navigation Compose**: `androidx.hilt:hilt-navigation-compose:1.1.0`
- **Jetpack Compose**: Latest Compose BOM with Material3
- **Kotlin**: Version 2.0.21

### **Networking Dependencies**
- **Retrofit**: `com.squareup.retrofit2:retrofit:2.9.0`
- **Retrofit Gson**: `com.squareup.retrofit2:converter-gson:2.9.0`
- **OkHttp**: `com.squareup.okhttp3:okhttp:4.12.0`
- **OkHttp Logging**: `com.squareup.okhttp3:logging-interceptor:4.12.0`
- **Gson**: `com.google.code.gson:gson:2.10.1`

### **Background Processing**
- **WorkManager**: `androidx.work:work-runtime-ktx:2.9.0`
- **Hilt Work**: `androidx.hilt:hilt-work:2.48`

### **Data Storage**
- **DataStore Preferences**: `androidx.datastore:datastore-preferences:1.0.0`
- **Security Crypto**: `androidx.security:security-crypto:1.1.0-alpha06`

### **Coroutines**
- **Kotlinx Coroutines Core**: `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3`
- **Kotlinx Coroutines Android**: `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3`

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK with API level 24+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Hilt-Demo
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory and select it

3. **Sync the project**
   - Android Studio will automatically sync the project
   - Wait for the Gradle sync to complete

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

## How It Works

### **1. Application Startup**
- `HiltDemoApplication` initializes Hilt dependency injection
- All modules and dependencies are configured
- Network services and repositories are ready

### **2. Activity Navigation**
- **MainActivity**: Entry point with navigation to other activities
- **PersonActivity**: Demonstrates ViewModel with auto-incrementing counter
- **NetworkActivity**: Comprehensive networking demo with tabbed interface
- **DataStoreActivity**: Secure data storage demo with encryption toggle

### **3. Dependency Injection Flow**
- Hilt creates and manages all service instances
- ViewModels receive dependencies through constructor injection
- Repository pattern provides clean data access layer

### **4. Network Operations**
- **API Calls**: Retrofit handles all HTTP requests
- **JSON Parsing**: Gson automatically serializes/deserializes data
- **Error Handling**: Comprehensive error management at all levels
- **Background Tasks**: WorkManager executes network operations reliably

### **5. UI Updates**
- **StateFlow**: Reactive state management updates UI automatically
- **Compose UI**: Declarative UI responds to state changes
- **Real-time Feedback**: Loading states, success/error messages

### **6. Data Storage Operations**
- **Secure Storage**: AES256 encryption for sensitive data
- **Reactive Updates**: Real-time UI updates with Flow
- **Dual Storage**: Regular DataStore + EncryptedSharedPreferences
- **Simple Access**: Utility classes for easy data operations

## Key Hilt Annotations

- `@HiltAndroidApp`: Applied to Application class to enable Hilt
- `@AndroidEntryPoint`: Applied to Android components (Activity, Fragment, etc.)
- `@HiltViewModel`: Applied to ViewModels for Hilt integration
- `@HiltWorker`: Applied to WorkManager workers
- `@Inject`: Used for constructor injection and field injection
- `@Singleton`: Creates a single instance of the class
- `@Module` & `@InstallIn`: Used for dependency modules

## Learning Resources

- [Hilt Documentation](https://dagger.dev/hilt/)
- [Android Dependency Injection Guide](https://developer.android.com/training/dependency-injection)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)

## App Features & Screenshots

### **MainActivity**
- "Hello Hilt!" greeting with Hilt dependency injection
- Navigation buttons to other activities
- Demonstrates basic Hilt setup

### **PersonActivity**
- Auto-incrementing counter from 1 to 10
- ViewModel with reactive state management
- Progress indicator and completion message
- Start/Reset controls

### **NetworkActivity**
- **GET APIs Tab**: Test all GET operations (Users, Posts, by ID)
- **POST APIs Tab**: Test all POST operations (Create User, Create Post)
- **WorkManager Tab**: Test background network tasks
- Real-time status display and results
- Input forms for all operations
- Scrollable results with data preview

### **DataStoreActivity**
- **Secure Data Storage**: Test encrypted and regular data storage
- **User Data Management**: Save/read user information securely
- **Encryption Toggle**: Switch between encrypted and regular storage
- **Real-time Updates**: Live display of stored data
- **Data Operations**: Save, read, delete, and clear operations
- **Status Feedback**: Success/error messages for all operations

## API Testing Features

### **GET Operations**
- Get all users from JSONPlaceholder API
- Get user by ID
- Get all posts
- Get post by ID
- Get posts by user ID

### **POST Operations**
- Create new user with custom data
- Create new post with title, body, and user ID
- Real-time JSON parsing and validation

### **Background Processing**
- Schedule GET users work
- Schedule GET posts work
- Schedule create user work
- Schedule create post work
- Periodic sync every 15 minutes

### **File Upload Support**
- Image upload with MultipartBody
- File upload with custom descriptions
- Progress tracking and error handling

## Data Storage Features

### **Secure Data Operations**
- **Encrypted Storage**: AES256 encryption for sensitive data
- **Regular Storage**: Standard DataStore for non-sensitive data
- **Data Types**: String, Int, Boolean, Long support
- **Reactive Updates**: Real-time UI updates with Flow
- **Simple API**: Easy-to-use utility classes

### **Data Management**
- **User Data**: Name, email, phone, age storage
- **App Settings**: JSON settings and preferences
- **Login Status**: Authentication state management
- **Timestamps**: Last login and sync times
- **Clear Operations**: Delete individual keys or all data

## Usage Examples

### **Easy Network Calls Anywhere in App**
```kotlin
// Inject NetworkRepository anywhere
@Inject lateinit var networkRepository: NetworkRepository

// Make any network call easily
val result = networkRepository.getUsers()
val result = networkRepository.createUser(userRequest)
val result = networkRepository.uploadImage(file, description)
```

### **Background Task Scheduling**
```kotlin
// Inject WorkManagerHelper anywhere
@Inject lateinit var workManagerHelper: WorkManagerHelper

// Schedule background tasks
workManagerHelper.scheduleGetUsersWork()
workManagerHelper.scheduleCreateUserWork(name, email, phone)
workManagerHelper.schedulePeriodicSync()
```

### **ViewModel Integration**
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            when (val result = networkRepository.getUsers()) {
                is NetworkResult.Success -> { /* Handle success */ }
                is NetworkResult.Error -> { /* Handle error */ }
                is NetworkResult.Loading -> { /* Handle loading */ }
            }
        }
    }
}
```

### **Secure Data Storage**
```kotlin
// Using SecureDataStore with Hilt
@Inject lateinit var secureDataStore: SecureDataStore

// Save user data securely
secureDataStore.saveUserData("John", "john@example.com", "1234567890", 25, isSensitive = true)

// Read data reactively
val userName by secureDataStore.getUserName().collectAsState(initial = "")

// Using DataStoreUtils (no dependency injection needed)
DataStoreUtils.saveUserToken(context, "jwt_token", encrypted = true)
val token by DataStoreUtils.getUserToken(context, encrypted = true).collectAsState(initial = "")
```

## Technical Highlights

- **Clean Architecture**: Proper separation of concerns
- **Dependency Injection**: Hilt throughout the entire app
- **Reactive Programming**: StateFlow for UI updates
- **Error Handling**: Comprehensive error management
- **Background Processing**: Reliable WorkManager integration
- **Secure Data Storage**: AES256 encryption with DataStore
- **Type Safety**: Kotlin generics and sealed classes
- **Modern UI**: Jetpack Compose with Material3

## Contributing

Feel free to submit issues and enhancement requests!
