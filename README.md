# 🌸 Nurtura

**Nurtura** is an Android application designed for maternal and child health support. The app helps mothers care for themselves and their children by providing easy access to healthcare professionals, personalized immunization schedules, emergency assistance, and reliable health information.

Built using **Java** and **XML**, Nurtura focuses on usability, reliability, and timely health interventions.

Sure — here’s a cleaner, more consistent version with tighter language and aligned formatting. I kept the tone professional but friendly:

---

## 📱 Features

* 💬 **Real-Time Chat with Healthcare Professionals**
  Secure, in-app messaging between mothers and qualified healthcare providers.

* 🔔 **Immunization Reminder Notifications**
  Firebase-powered push notifications to help ensure children never miss important vaccinations.

* 📅 **Personalized Immunization Schedules**
  Automatically generated schedules based on each child’s date of birth.

* 🚨 **Emergency Assistance Button**
  One-tap access to immediate support from a healthcare professional during urgent situations.

* 📚 **Maternal & Child Health Resources**
  Trusted articles and educational content to support mothers throughout pregnancy and childcare.

* 🧾 **Child Health Records**
  Centralized storage for medical history, growth tracking, and health notes for each child.

* 🥗 **Healthy Recipe Recommendations**
  Nutritious meal ideas for mothers, powered by the Spoonacular API.

* 🏥 **Staff Management Portal**
  Healthcare staff can search and manage mother and child health records, including updating vaccination data.

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
  * Firebase Firestore (data storage)
* **External API:** Spoonacular

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
