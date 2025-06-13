# 🛣️ Make Roads Better

**MakeRoadsBetter** is a Kotlin-based Android application built with Jetpack Compose that empowers users to capture and report poor road conditions or road closures. The app collects user-submitted photos and geolocation data, stores them in a global Firebase Firestore database, and displays them on a real-time map using OpenStreetMap. 

## 📌 Problem It Solves
While traveling or visiting new areas, road conditions can be unknown. This app helps users:
- Identify bad roads or closures beforehand.
- Contribute to a crowdsourced road quality monitoring platform.
- Improve travel planning and awareness in unfamiliar areas.

---

## 🚀 Features

- 📸 **Capture and Report**  
  Users can take a photo of a bad road or road closure. The app automatically fetches the GPS coordinates and uploads them to the cloud.

- 🗺️ **Map View**  
  All reported road issues are visible on a shared map using OpenStreetMap, helping others plan better routes.

- 🧭 **Manual Road Selection Mode**  
  Users can select multiple coordinates to mark a path on the map. Each tapped point is connected in a straight line to highlight affected roads.

- ☁️ **Cloud Storage**  
  All submissions are saved in Firebase Firestore, accessible globally.

- 🔓 **Open Access**  
  No login required—anyone can contribute and view reports.

---

## 🧰 Tech Stack

- **Jetpack Compose** – Modern UI toolkit for native Android.
- **Kotlin** – Primary language for Android development.
- **OpenStreetMap Library** – For displaying reports and map interactions.
- **OkHttp** – Efficient network requests.
- **Firebase Firestore** – Cloud-based NoSQL database for storing reports.

---
