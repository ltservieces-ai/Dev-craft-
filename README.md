# Dev Craft (ACS) 🚀
> **Your Ideas, Anywhere.** A full-featured mobile IDE designed to develop, build, and deploy real native Android applications directly on your Android device.
---
## 📱 Overview
**Dev Craft** is a mobile-first integrated development environment (IDE) built for developers who want to code on the go. Featuring Kotlin and Java support, direct APK compilation, an integrated Linux terminal, and seamless Git sync, Dev Craft brings the desktop development workflow straight to your pocket.
---
## ✨ Features
- **Project Templates:** Start fast with pre-configured templates including Empty Activity, Basic Activity, Jetpack Compose, Bottom Navigation, and Navigation Drawer[span_0](start_span)[span_0](end_span).
- **Code Editor & Quick Toolbar:** Full syntax highlighting for Kotlin, Java, and XML with quick access to essential symbols (`{ }`, `( )`, `[ ]`, `;`, `"`)[span_1](start_span)[span_1](end_span).
- **Integrated Terminal:** Powered by Termux bootstrap to run shell commands (`git`, `gradle`, `python`, `ls`) directly inside your project root[span_2](start_span)[span_2](end_span).
- **One-Tap Build & Run:** Compile Gradle projects, review live build outputs, and directly install APKs without leaving the app[span_3](start_span)[span_3](end_span)[span_4](start_span)[span_4](end_span).
- **Permissions Dashboard:** Visual interface to toggle common permissions (`POST_NOTIFICATIONS`, `READ_MEDIA_IMAGES`, `ACCESS_FINE_LOCATION`, `INTERNET`) which automatically update your `AndroidManifest.xml`[span_5](start_span)[span_5](end_span).
- **Version Control & Sync:** Built-in Git support to clone remote repositories, push commits, and back up projects to cloud storage[span_6](start_span)[span_6](end_span)[span_7](start_span)[span_7](end_span).
- **App Bundling & Export:** Export Android App Bundles, share release/debug APKs, and customize application logos directly within project settings[span_8](start_span)[span_8](end_span).
---
## 🛠️ Tech Stack & Requirements

| Specification | Details |
| :--- | :--- |
| **Primary Languages** | Kotlin, Java[span_9](start_span)[span_9](end_span)[span_10](start_span)[span_10](end_span) |
| **Build System** | Gradle Kotlin DSL (`.kts`) & Groovy[span_11](start_span)[span_11](end_span)[span_12](start_span)[span_12](end_span) |
| **Minimum SDK** | API 24 (Android 7.0 Nougat) and above[span_13](start_span)[span_13](end_span) |
| **Terminal Core** | Linux bootstrap environment (`util-linux`, shell utilities)[span_14](start_span)[span_14](end_span) |

---
## 🚀 Getting Started
### Creating Your First Project
1. Tap **Create project** on the welcome dashboard[span_15](start_span)[span_15](end_span).
2. Choose your layout (e.g., **Empty Activity** or **Compose Activity**)[span_16](start_span)[span_16](end_span).
3. Enter your **Project Name**, **Package Name**, and select your **Target SDK**[span_17](start_span)[span_17](end_span).
4. Hit **Create** to initialize your workspace[span_18](start_span)[span_18](end_span).
### Running and Installing
1. Open your code files inside the project tree (`MainActivity.kt`, `activity_main.xml`)[span_19](start_span)[span_19](end_span).
2. Tap the **Play** button on the top toolbar to trigger Gradle build execution[span_20](start_span)[span_20](end_span).
3. Select **Direct Install APK** from the project menu to launch your app on your device[span_21](start_span)[span_21](end_span).
---
## 📂 Project Structure
```text
MyAwesomeApp/
├── .acside/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/ (or kotlin/)
│   │       │   └── MainActivity.kt
│   │       └── res/
│   │           └── layout/
│   │               └── activity_main.xml
├── build.gradle.kts
├── settings.gradle
├── gradle.properties
└── proguard-rules.pro
