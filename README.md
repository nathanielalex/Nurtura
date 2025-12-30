# 🌸 Nurtura

**Nurtura** is an Android application focused on maternal and child health support. It empowers mothers to care for themselves and their children by providing direct access to healthcare professionals, personalized immunization tracking, emergency assistance, and trusted health resources.

🎥 **Video Demo:** [Watch here](https://binusianorg-my.sharepoint.com/personal/nathaniel_alexander_binus_ac_id/_layouts/15/guestaccess.aspx?share=IQDKvVyzL9n6Tr82X3S3mS7sAU9q-y-Zg32yD3xVZQGL4iM&nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=VKZVHW)

---

## 📑 Table of Contents

- [Key Features](#-key-features)
- [Multi-Child Support](#-multi-child-support)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Activities & Fragments](#-activities--fragments)
  - [Authentication](#authentication)
  - [Mother Features](#mother-features)
  - [Staff Features](#staff-features)
- [Setup & Installation](#-setup--installation)

---

## 📱 Key Features

* 💬 **Real-Time Chat with Healthcare Professionals**
  Secure in-app messaging between mothers and qualified healthcare staff.

* 🔔 **Immunization Reminder Notifications**
  Firebase-powered push notifications to ensure children never miss scheduled vaccinations.

* 📅 **Personalized Immunization Schedules**
  Automatically generated vaccination timelines based on each child’s date of birth.

* 🚨 **Emergency Assistance Button**
  One-tap access to immediate support from healthcare professionals during urgent situations.

* 📚 **Maternal & Child Health Resources**
  Curated articles and educational content supporting pregnancy and childcare.

* 🧾 **Child Health Records**
  Centralized storage for medical history, growth tracking, and health notes per child.

* 🥗 **Healthy Recipe Recommendations**
  Nutritious meal ideas for mothers, powered by the Spoonacular API.

* 🏥 **Staff Management Portal**
  Tools for healthcare staff to search, view, and update mother and child health records, including vaccination data.

---

## 👶 Multi-Child Support

* One mother account can **register multiple children**
* Each child has:

  * A unique profile
  * Individual immunization schedules
  * Separate health history records

---

## 🛠️ Tech Stack

* **Platform:** Android
* **Language:** Java
* **UI:** XML
* **Backend:** Firebase

  * Firebase Authentication
  * Firebase Firestore
  * Firebase Cloud Messaging
* **External API:** Spoonacular

---

## 🏗️ Project Structure

```
Nurtura/
├── app/
│   ├── java/com/example/nurtura/
│   │   ├── fragment/
│   │   ├── adapter/
│   │   ├── model/
│   │   ├── repository/
│   │   └── utils/
│   └── res/
│       ├── layout/
│       ├── drawable/
│       ├── values/
│       └── xml/
└── AndroidManifest.xml
```

---

## 📲 Activities & Fragments

### Authentication

* **LoginActivity**
  Email/password and Google Sign-In for mothers and staff.
* **RegisterActivity**
  Account creation for mothers.

### Mother Features

#### Home

* **HomeFragment**

  * Emergency panic call
  * Health article navigation
  * Upcoming immunization widget
  * Healthy meal suggestions

#### Articles & Recipes

* **ArticleActivity** – List of health articles
* **ArticleDetailActivity** – Full article view
* **RecipeDetailActivity** – Recipe ingredients and preparation steps

#### Children & Milestones

* **MilestoneFragment** – Upcoming and completed vaccination schedules
* **AddChildActivity** – Add or update child details (DOB, gender, blood type, etc.)

#### Chat

* **ChatFragment** – Conversation list
* **ChatActivity** – Chat room between mothers and staff

#### Profile

* **ProfileFragment**

  * Edit mother profile
  * Register and manage children
  * View child health records
  * Sign out
* **EditProfileActivity** – Update profile information

### Staff Features

#### Mother & Child Management

* **SearchFragment** – Search mothers by email
* **MotherDetailActivity**

  * View mother profile
  * Initiate chat
  * View registered children
* **StaffMotherProfileActivity** – Mother profile details
* **StaffChildDetailActivity** – Child health data (height, weight, blood type, etc.)

#### Vaccines

* **VaccineFragment** – Vaccine list and information
* **AddVaccineActivity** – Add new vaccines to the system

#### Staff Profile

* **StaffProfileFragment** – Staff account information

---

## ⚙️ Setup & Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/nathanielalex/Nurtura.git
   ```

2. Open the project in **Android Studio**

3. Configure Firebase:

   * Create a Firebase project
   * Add `google-services.json` to the `app/` directory
   * Enable Authentication, Firestore, and Cloud Messaging

4. Build and run the app on an emulator or physical device

---

## 🔐 Permissions

* Internet access
* Phone call access (Emergency feature)
* Notifications
