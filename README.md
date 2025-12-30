# 🌸 Nurtura

**Nurtura** is an Android application designed for maternal and child health support. The app helps mothers care for themselves and their children by providing easy access to healthcare professionals, personalized immunization schedules, emergency assistance, and reliable health information.

Built using **Java** and **XML**, Nurtura focuses on usability, reliability, and timely health interventions.

---

## 📱 Features

* 💬 **Chat with Healthcare Professionals**
  Secure communication between mothers and qualified healthcare providers.

* 🔔 **Push Notifications for Immunization Reminders**
  Firebase-powered notifications to ensure children never miss important vaccinations.

* 📅 **Personalized Immunization Schedule**
  Automatically generated schedules based on each child’s date of birth.

* 🚨 **Emergency Button**
  One-tap emergency call to a healthcare professional during urgent situations.

* 📚 **Maternal & Child Health Articles**
  Access trusted educational content to support mothers throughout pregnancy and childcare.

* 🧾 **Child Health History Records**
  Maintain medical history, growth details, and health notes for each child.

---

## 👶 Multi-Child Support

* A single mother account can **register multiple children**
* Each child has:

  * A unique profile
  * Individual immunization schedules
  * Separate health history records

---

## 🛠️ Tech Stack

* **Platform:** Android
* **Language:** Java
* **UI Design:** XML
* **Backend Services:** Firebase

  * Firebase Authentication
  * Firebase Cloud Messaging (Push Notifications)
  * Firebase Realtime Database / Firestore (data storage)

---

## 🏗️ Project Structure (Typical)

```
Nurtura/
├── app/
│   ├── java/com/example/nurtura/
│   │   ├── activities/
│   │   ├── adapters/
│   │   ├── models/
│   │   ├── services/
│   │   └── utils/
│   └── res/
│       ├── layout/
│       ├── drawable/
│       ├── values/
│       └── xml/
├── AndroidManifest.xml
└── README.md
```

---

## ⚙️ Setup & Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/Nurtura.git
   ```

2. Open the project in **Android Studio**

3. Connect Firebase:

   * Create a Firebase project
   * Add your `google-services.json` file to the `app/` directory
   * Enable Authentication, Firestore/Realtime Database, and Cloud Messaging

4. Build and run the app on an emulator or physical device

---

## 🔐 Permissions Used

* Internet access
* Phone call permission (Emergency feature)
* Notifications

---

## 🎯 Target Users

* Mothers and caregivers
* Healthcare professionals supporting maternal and child health

---
