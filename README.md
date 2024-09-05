# Abz.Agency | Test Assignment

## Overview

The Android application is built using the latest technologies to provide a smooth and reliable user experience. It uses Kotlin for writing clean and efficient code, and Jetpack Compose to create a responsive and modern user interface. Hilt is used for managing dependencies, making the app easier to develop and test. Retrofit handles all network requests, ensuring handy and type-safe communication with API. Glide is used for loading and caching images, helping the app run smoothly. Together, these technologies make the app high-performing and easy to maintain.

## Configuration Options

The project is configurable via `%project_root%/app/build.gradle.kts`.
- **namespace:** `com.abz.agency.testtask`
- **compileSdk:** 34
- **applicationId:** `com.abz.agency.testtask`
- **minSdk:** 24
- **targetSdk:** 34
- **versionCode:** 1
- **versionName:** `"1.0"`
- **buildTypes:**
  - release
- **compileOptions:**
  - sourceCompatibility: `JavaVersion.VERSION_1_8`
  - targetCompatibility: `JavaVersion.VERSION_1_8`
- **kotlinOptions:**
  - jvmTarget: `"1.8"`
- **buildFeatures:**
  - compose: `true`
- **composeOptions:**
  - kotlinCompilerExtensionVersion: `"1.5.1"`
- **kapt:**
  - correctErrorTypes: `true`

## Dependencies

### External Dependencies

#### Glide

Glide is a popular image loading and caching library for Android. It simplifies the process of fetching, displaying, and managing images from various sources, such as the web or local storage. Glide automatically handles image loading and caching.

```
implementation("com.github.bumptech.glide:compose:1.0.0-beta01") // the dependency
kapt("com.github.bumptech.glide:compiler:4.9.0") // annotation processing
```

#### Hilt

Hilt is a dependency injection library for Android that simplifies the process of providing dependencies in the app. Built on top of Dagger, Hilt automates much of the setup required for dependency injection, reducing boilerplate code and making codebase easier to manage and test. Hilt integrates seamlessly with Android components like activities and view models, ensuring that dependencies are provided where they’re needed, helping to create a more modular and scalable application.

```
implementation("com.google.dagger:hilt-android:2.52") // the dependency
implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // additional features to use with compose
kapt("com.google.dagger:hilt-android-compiler:2.52") // annotation processing
```

App level plugins:
```
id("com.google.dagger.hilt.android")
id("kotlin-kapt")
```

Project level plugins:
```
id("com.google.dagger.hilt.android") version "2.52" apply false
```

#### Retrofit

Retrofit is a powerful and flexible HTTP client library for Android that simplifies network communication by converting REST API endpoints into easy-to-use Kotlin interfaces. It handles the process of making network requests, sending parameters, and parsing responses, supporting a variety of data formats like JSON and XML. With Retrofit, you can easily define API calls and manage asynchronous or synchronous requests, making it a go-to solution for interacting with web services in Android applications.

```
implementation("com.squareup.retrofit2:retrofit:2.11.0") // the dependency
implementation("com.squareup.retrofit2:converter-gson:2.11.0") // json to kotlin objects converter
```

### Android Libraries

#### Core

- Core KTX is a set of Kotlin extensions for the Android platform that enhances the Android SDK by making it more Kotlin-friendly.
- Lifecycle Runtime KTX is a Kotlin extension library for Android's Lifecycle components. It provides concise and idiomatic Kotlin functions and coroutines support to help manage the lifecycle of Android components, such as activities.
- Activity Compose is a Jetpack Compose library that provides utilities for integrating Jetpack Compose with traditional Android activities.

```
implementation("androidx.core:core-ktx:1.13.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
implementation("androidx.activity:activity-compose:1.9.1")
```

#### Core compose

```
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3:")
```

#### Navigation-compose

Navigation Compose is a library within Jetpack Compose that provides a simple and declarative way to handle in-app navigation in Android applications. It allows developers to define navigation paths, manage the back stack, and pass data between composable screens using a consistent, type-safe API.

```
implementation("androidx.navigation:navigation-compose:2.7.7")
```

#### Core-splashscreen

Core SplashScreen is a part of the Android Jetpack library that provides a standardized way to implement splash screens in Android applications.

```
implementation("androidx.core:core-splashscreen:1.0.1")
```

## Code structure and important modules

### Project root
- `%project_root%/app/src/main/java/com/abz/agency/testtask` - Main source directory of project. All next files and directories paths are written relative to it.
- `MainActivity.kt` - Application entry point.
- `HiltApplication.kt` - Holder of Singleton modules for DI. Also global app constants should be defined here.

### UI

- `./ui/Navigation.kt` - Contains nav graph of the app. And some related stuff.
- `./ui/theme/` - Typography, color schemes, fonts.
- `./ui/screen/` - Contains app screens composables with their ViewModels.
- `./ui/components/` - Custom app widgets like Buttons, TextFields and more.

### Model

- `./model/api` - Retrofit interfaces for API access / Gson type converters.
- `./model/data` - UsersRepository, DataSource.

### Dependency Injection

- `./di/Modules.kt` - DI modules / How to provide app dependencies and their scope.

## Troubleshooting

- Missing classes/functions/types - Run gradle sync. `Sync Project with Gradle Files` - `Ctrl + Shift + O`.
