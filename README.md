# Hilt Demo Android App

A simple Android application demonstrating Hilt dependency injection with Jetpack Compose UI.

## Overview

This project showcases how to integrate and use Hilt (Dagger) for dependency injection in an Android application. The app displays "Hello Hilt!" text using a service that is injected through Hilt's dependency injection framework.

## Features

- **Hilt Dependency Injection**: Demonstrates proper setup and usage of Hilt
- **Jetpack Compose UI**: Modern Android UI toolkit
- **Service Layer**: Example of a service class with dependency injection
- **Clean Architecture**: Separation of concerns with proper dependency management

## Project Structure

```
app/
├── src/main/java/com/naveen/hiltdemo/
│   ├── MainActivity.kt              # Main activity with Hilt integration
│   ├── HiltDemoApplication.kt       # Application class with @HiltAndroidApp
│   └── service/
│       └── GreetingService.kt       # Service class for dependency injection
├── build.gradle.kts                 # App-level dependencies
└── AndroidManifest.xml              # Manifest with Application class
```

## Key Components

### 1. HiltDemoApplication
```kotlin
@HiltAndroidApp
class HiltDemoApplication : Application()
```
- Entry point for Hilt dependency injection
- Annotated with `@HiltAndroidApp` to generate the necessary Dagger components

### 2. GreetingService
```kotlin
@Singleton
class GreetingService @Inject constructor() {
    fun getGreeting(): String = "Hello Hilt!"
    fun getPersonalizedGreeting(name: String): String = "Hello $name from Hilt!"
}
```
- Service class with `@Singleton` annotation for single instance
- Constructor injection with `@Inject`

### 3. MainActivity
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var greetingService: GreetingService
    // ...
}
```
- Activity annotated with `@AndroidEntryPoint`
- Field injection of `GreetingService`

## Dependencies

The project uses the following key dependencies:

- **Hilt Android**: `com.google.dagger:hilt-android:2.48`
- **Hilt Compiler**: `com.google.dagger:hilt-compiler:2.48`
- **Hilt Navigation Compose**: `androidx.hilt:hilt-navigation-compose:1.1.0`
- **Jetpack Compose**: Latest Compose BOM with Material3
- **Kotlin**: Version 2.0.21

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

1. **Application Startup**: When the app starts, `HiltDemoApplication` initializes Hilt
2. **Dependency Injection**: Hilt creates and manages the `GreetingService` instance
3. **Activity Injection**: `MainActivity` receives the `GreetingService` through field injection
4. **UI Display**: The service provides greeting text that is displayed in the Compose UI

## Key Hilt Annotations

- `@HiltAndroidApp`: Applied to Application class to enable Hilt
- `@AndroidEntryPoint`: Applied to Android components (Activity, Fragment, etc.)
- `@Inject`: Used for constructor injection and field injection
- `@Singleton`: Creates a single instance of the class

## Learning Resources

- [Hilt Documentation](https://dagger.dev/hilt/)
- [Android Dependency Injection Guide](https://developer.android.com/training/dependency-injection)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)

## Screenshots

The app displays:
- "Hello Hilt!" as the main greeting
- "Hello Android Developer from Hilt!" as a personalized message
