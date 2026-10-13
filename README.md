# Cherry Messages // Asta v1.0

<p align="center">
  <img src="screenshots/1.png" width="250" alt="Cherry Messages Screenshot 1" />
  <img src="screenshots/2.png" width="250" alt="Cherry Messages Screenshot 2" />
  <img src="screenshots/3.png" width="250" alt="Cherry Messages Screenshot 3" />
</p>

**Cherry Messages** is a modern, high-performance, offline-first Android SMS & MMS messenger built with Jetpack Compose and Material Design 3. Designed with a striking "Cherry Red Futuristic" dark aesthetic, it delivers a lightning-fast messaging experience, robust default SMS app compliance, full-text search, automated retry queues, and secure backup/restore capabilities.

---

## ✨ Key Features

- **Cherry Red Futuristic M3 UI**: Immersive dark theme featuring vibrant cherry red accents (`#FF2A55`), custom background images, dynamic profile pictures, and smooth Material Design 3 transitions.
- **Offline-First & High Performance**: Local database powered by **Room** with **FTS4 (Full-Text Search)** for instant message querying across massive conversation threads.
- **Default SMS App Compliance**: Fully compliant with Android's default SMS handler role (`Telephony.Sms`), enabling native SMS/MMS sending, receiving, broadcast receivers, and content provider synchronization.
- **Reliable Delivery & WorkManager**: Background queueing and auto-retry mechanism for outgoing messages via **WorkManager** to ensure zero message loss even under poor network or radio signal conditions.
- **Secure Backup & Restore**: Export and import encrypted or structured message backups directly using Android's Storage Access Framework (SAF).
- **Rich Conversation Management**: Conversation list, thread detail view, contact search, attachment support, delivery status indicators, and notification management.

---

## 🏗️ Architecture & Tech Stack

Cherry Messages adheres to clean architecture principles and modern Android development best practices:

- **UI Framework**: Jetpack Compose, Material 3, Material Icons Extended, Edge-to-Edge display.
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with Repository pattern.
- **Dependency Injection**: Hilt (Dagger).
- **Concurrency**: Kotlin Coroutines & Flow for reactive asynchronous data streams.
- **Local Storage**: Room Persistence Library with FTS4 full-text search extensions.
- **Background Tasks**: Android WorkManager for reliable message retry worker management.
- **Telephony**: `SmsManager`, `TelephonyProvider`, broadcast receivers (`SMS_RECEIVED_ACTION`, `WAP_PUSH_DELIVER`).

---

## 📱 Screenshots

| Conversation List | Chat Thread | Search & Filter | Settings & Customization |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/1.png" width="220"/> | <img src="screenshots/2.png" width="220"/> | <img src="screenshots/3.png" width="220"/> | <img src="screenshots/4.png" width="220"/> |

---

## 🚀 Getting Started & Build Instructions

### Prerequisites
- Android Studio Ladybug / Koala or newer.
- JDK 17 or higher.
- Android SDK API level 26+ (Android 8.0 Oreo or higher; Android 14/15 recommended).

### Building and Running
1. Clone the repository:
   ```bash
   git clone https://github.com/asta-JC/Cherry-Messages.git
   cd Cherry-Messages
   ```
2. Open the project in Android Studio, or build via command line:
   ```bash
   ./gradlew assembleDebug
   ```
3. Install the debug APK onto your connected physical Android device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## ⚙️ Setting as Default SMS App

To enable full SMS sending, receiving, and database synchronization on real devices:
1. Open **Cherry Messages**.
2. When prompted on launch (or via Settings), grant the required permissions (`SEND_SMS`, `READ_SMS`, `RECEIVE_SMS`, `READ_CONTACTS`).
3. Accept the system prompt to set **Cherry Messages** as your device's **Default SMS Application**.

---

## 📄 License

```
Copyright (c) 2025 asta-JC. All rights reserved.
```

---

Cherry Messages // Asta v1.0
