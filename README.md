# Conan - Android Content Monitoring & Blocking App

**Conan** is an Android application designed to support **digital wellness** and **productivity** by monitoring and blocking content across installed apps. Using Android’s Accessibility framework, Conan detects potentially problematic content and responds with interventions such as overlays or alerts.

---

## 📌 Purpose & Scope

Conan helps users maintain focus and avoid triggering or distracting material by:
- Monitoring screen content and foreground apps
- Displaying emergency overlays to block harmful content
- Alerting users when detection thresholds are reached

It is especially useful for:
- Individuals seeking digital detox or content moderation
- Users with focus issues or content-trigger-related sensitivity
- Productivity-focused routines (e.g., study or work sessions)

---

## ⚙️ Technology Stack & Dependencies

| Category              | Technology               | Key Dependencies                            |
|----------------------|--------------------------|---------------------------------------------|
| UI Framework         | Jetpack Compose           | `androidx.compose.bom`, `androidx.material3` |
| Architecture         | Clean Architecture, MVVM  | `androidx.lifecycle.viewmodel.compose`      |
| Dependency Injection | Hilt                      | `hilt.android`, `androidx.hilt.navigation.compose` |
| Database             | Room                      | `androidx.room.runtime`, `androidx.room.ktx` |
| Preferences Storage  | DataStore                 | `androidx.datastore.preferences`            |
| Background Tasks     | WorkManager               | `androidx.work.runtime.ktx`                 |
| Navigation           | Navigation Compose        | `androidx.navigation.compose`               |
| Async Programming    | Kotlin Coroutines         | `kotlinx.coroutines.android`                |

---

## 🚀 Key Features

### 🔍 Content Monitoring
Monitors screen content using `ContentBlockerAccessibilityService` to detect inappropriate or distracting patterns in real time.

### 🛡️ Intervention System
Displays:
- Full-screen overlays to block apps/content
- Alert dialogs to notify users about violations or warnings

### 📈 Progress Tracking
- Tracks user behavior over time
- Maintains streaks and stores motivational quotes via Room

### ⚙️ User Preferences
- Stores and manages configurable options using DataStore
- Sensitivity level, blocking modes, alert frequency, etc.

### ⏱️ Background Processing
- Periodic cleanups and syncs managed using WorkManager

---

## 🔐 Required Permissions

Conan uses system-level permissions to function effectively:
- `BIND_ACCESSIBILITY_SERVICE`
- `SYSTEM_ALERT_WINDOW`
- `PACKAGE_USAGE_STATS`
- `KILL_BACKGROUND_PROCESSES`
- `FOREGROUND_SERVICE`
- `WAKE_LOCK`
- `POST_NOTIFICATIONS`

---

## 📦 Project Setup

- **Minimum SDK**: 28 (Android 9)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle with Kotlin DSL
- **Architecture**: Single-module Clean Architecture
- **Optimization**:
  - ProGuard + resource shrinking
  - APK splitting by ABI, density, and language

## 📌 This project is for educational and personal use.
---
