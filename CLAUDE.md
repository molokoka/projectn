# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop, and Server platforms using Compose Multiplatform for UI and Ktor for the server component.

## Project Architecture

The project follows a modular Kotlin Multiplatform structure:

- **`/composeApp`** - Compose Multiplatform application targeting Android, iOS, and Desktop
  - `commonMain` - Shared UI code across all platforms
  - `androidMain` - Android-specific code
  - `desktopMain` - Desktop-specific code  
  - `iosMain` - iOS-specific code
  - Main class: `molokoka.project.n.MainKt` (Desktop), `molokoka.project.n.MainActivity` (Android)

- **`/server`** - Ktor server application (JVM)
  - Main class: `molokoka.project.n.ApplicationKt`
  - Uses Netty engine with Logback for logging

- **`/shared`** - Shared business logic across all targets
  - Supports Android, iOS, JVM targets
  - Contains common utilities and platform abstractions

- **`/iosApp`** - iOS application entry point (Xcode project)
  - Swift entry point that integrates with Kotlin Multiplatform framework

## Development Commands

### Building
- **Build all targets**: `./gradlew build`
- **Build specific module**: `./gradlew :composeApp:build`, `./gradlew :server:build`, `./gradlew :shared:build`

### Running Applications
- **Desktop app**: `./gradlew :composeApp:run`
- **Server**: `./gradlew :server:run`
- **Android**: Use Android Studio or `./gradlew :composeApp:installDebug`

### Testing
- **Run all tests**: `./gradlew test`
- **Run specific module tests**: `./gradlew :composeApp:test`, `./gradlew :server:test`, `./gradlew :shared:test`

### Platform-Specific Tasks
- **Android tasks**: `./gradlew :composeApp:assembleDebug`, `./gradlew :composeApp:assembleRelease`
- **Desktop distribution**: `./gradlew :composeApp:packageDistributionForCurrentOS`
- **iOS framework**: Build through Xcode or `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`

## Key Dependencies

- **Kotlin**: 2.1.21
- **Compose Multiplatform**: 1.8.1  
- **Ktor**: 3.1.3
- **Coroutines**: 1.10.2
- **Android Compile/Target SDK**: 35, Min SDK: 24

## Package Structure

All code uses the base package `molokoka.project.n` with platform-specific subpackages as needed.

## Development Notes

- Uses Gradle version catalogs (`gradle/libs.versions.toml`) for dependency management
- Compose Hot Reload is enabled for faster development iteration
- JVM target is Java 11 across all modules
- iOS targets support x64, arm64, and simulator arm64 architectures
- **Do not use Material components** - prefer basic Compose components and custom styling