# ElektrykApp

ElektrykApp is an Android application designed for CKZiU "Elektryk" students and teachers.  
It provides a fast and convenient way to check timetables and substitutions without relying on the official school website.


## 📸 Preview

<img width="1200" alt="ElektrykApp_Preview" src="https://github.com/user-attachments/assets/d92809fd-0a38-4357-b810-b4e2dd94201b" />


## 💡 Why this project?

The official school website is not optimized for quick mobile access and can be inconvenient to use.  
This app was created to deliver a cleaner, faster, and more user-friendly experience for browsing timetables and substitutions.


## 🚀 Features

- Browse the school timetable
- Check substitutions (replacements)
- Search and filter data (classes, teachers, classrooms)
- Customize displayed timetable (class/teacher/classroom)
- Simple and mobile-friendly UI


## 🛠 Tech Stack

- Android
- Java
- Gradle


## ⚙️ Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Bokeher/ElektrykApp.git
   ```
2. Open the project in Android Studio.
3. Run the app on an emulator or physical device.

The project can run out of the box in preview mode using local demo data.

If you want to use real backend data instead:

1. Create `app/src/main/assets/app.properties` based on `app/src/main/assets/app.example.properties`.
2. Set the following values:
   - `preview_mode=false`
   - `token`
   - `rest_api_url`
3. Run the app again.


## 🔌 API Dependency

The application relies on a backend API developed by **timsixth**, hosted in a **private repository**.

> Real-time data requires access to this API.  
> Without valid credentials and endpoint, the app can still run in preview mode with local data.


## 📂 Project Status

- Mobile application is fully functional
- Preview mode allows the project to build and run without the private API
- Private API access is still required for real data
- Backend is not included in this repository


## 👥 Contributions

- [Bokeher](https://github.com/Bokeher) – Android application, UI, app logic, and integration layer
- [timsixth](https://github.com/timsixth) – Backend API and client/server communication layer


## 📌 Notes

This repository focuses on the **mobile application layer**.  
It demonstrates UI design, state handling, and integration with a REST API.
