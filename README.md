# Mutual Fund Explorer

A native Android application built with **Kotlin** that enables users to discover mutual funds, analyze historical NAV performance through interactive charts, and manage custom offline portfolio watchlists.

This project was developed as part of a **Mobile SDE Intern assignment**, with a strong focus on building a production-ready application using modern Android development practices.

---

## Submission Details

* **GitHub Repository:** https://github.com/Ad12-Ad/Groww-Assignment

* **Screen Recording Video:** https://drive.google.com/file/d/1gkF2NCFRtNotixs-ITlNE6mavCsAFxYG/view?usp=sharing

* **Working APK:** https://drive.google.com/file/d/1yCxxZTomPJOwkNvz0Tfpw6ea5qwmY5JC/view?usp=drive_link

---

## Architecture: Clean Architecture + MVI

The application is designed with a strong emphasis on **scalability, maintainability, and testability**. It follows **Clean Architecture principles** combined with the **MVI (Model-View-Intent)** pattern to ensure a predictable and unidirectional data flow.

### Architectural Flow

```mermaid
graph TD
    %% Layers
    subgraph Presentation_Layer [Presentation Layer - Jetpack Compose]
        UI[Compose UI]
        VM[ViewModel]
    end

    subgraph Domain_Layer [Domain Layer - Pure Kotlin]
        UC[Use Cases]
        Model[Domain Models]
    end

    subgraph Data_Layer [Data Layer]
        Repo[Repository Implementation]
        API[Retrofit / MfApi]
        Local[Room / WatchlistDao]
    end

    %% Flow of Actions (MVI)
    UI -->|1. Sends Intent/Event| VM
    VM -->|2. Executes| UC
    UC -->|3. Requests Data| Repo
    
    %% Flow of Data
    API -->|4. NetworkResult DTO| Repo
    Local -->|4. Entity Flow| Repo
    
    Repo -->|5. Maps to Domain Models| UC
    UC -->|6. Converts to Resource Wrapper| VM
    VM -->|7. Updates StateFlow| UI
    VM -->|8. Emits Single Effect| UI
    
    %% Highlight the Boundary
    style Domain_Layer fill:#f0f8ff,stroke:#333,stroke-width:2px
```

### Architectural Highlights

* **Clear Separation of Concerns (No Leaky Abstractions):**
  The Data layer handles low-level concerns such as network errors and IO exceptions, exposing results through a `NetworkResult`.
  The Domain layer then maps this into a clean and UI-friendly `Resource<T>` wrapper.
  This ensures that ViewModels remain completely independent of networking or database implementation details.

* **Unidirectional Data Flow:**
  ViewModels act as the **single source of truth**, exposing:

  * `StateFlow` for UI state updates
  * `Channel` for one-time events (e.g., navigation, toasts)

---

## App Navigation Map

```mermaid
graph LR
    %% Main Tabs
    subgraph Main_Bottom_Nav [Main Navigation]
        Explore[Explore Screen]
        Watchlist[Watchlist/Portfolio Tab]
    end

    %% Explore Screen Flows
    Explore -->|Click Search Bar| Search[Search Screen]
    Explore -->|Click View All| SearchAll[View All / Search State]
    Explore -->|Click Fund Card| Product[Product Details Screen]

    %% Product Screen Flows
    Product -->|Click Bookmark/Add| BottomSheet[[Watchlist Bottom Sheet]]
    BottomSheet -->|Create New/Select Folder| DB[(Room DB)]
    
    %% Watchlist Tab Flows
    Watchlist -->|Click Folder| FolderDetail[Folder Detail Screen]
    FolderDetail -->|Click Fund| Product
    
    %% Common
    Search -->|Select Fund| Product
    SearchAll -->|Select Fund| Product
```

---

## Key Features & Technical Decisions

### 1. Concurrent Network Fetching

Since the provided API does not offer a dedicated "Categories" endpoint, the app dynamically generates category data using the search API.
With **Kotlin Coroutines (async/await)**, multiple category requests are executed in parallel, significantly reducing initial loading time.

---

### 2. Flow-Based Search Debouncing

To prevent excessive API calls and race conditions, the search functionality uses:

* `debounce(300ms)`
* `distinctUntilChanged()`
* `mapLatest()`

This ensures that API requests are triggered only after the user pauses typing, improving both performance and efficiency.

---

### 3. Advanced Local Storage (Room Database)

* **Many-to-Many Relationship:**
  A mutual fund can be saved across multiple portfolios using a **composite primary key**.

* **Reactive UI Synchronization:**
  The UI observes database changes in real time using `Flow.combine`, ensuring instant updates without blocking the main thread.

---

### 4. Off-Thread Chart Parsing

Large datasets (historical NAV values) are processed on `Dispatchers.Default`, ensuring smooth UI performance and preventing frame drops.

---

### 5. Enhanced UI/UX Experience

* **Material 3 Pull-to-Refresh** for seamless data updates
* **Dynamic Profit/Loss Indicators** with color-coded visuals (green/red)
* **Modern UI Components** built entirely with Jetpack Compose (cards, chips, rounded CTA buttons)

---

## Tech Stack & Libraries

* **UI:** Jetpack Compose (Material 3), Navigation Compose
* **Dependency Injection:** Dagger Hilt
* **Asynchronous Programming:** Kotlin Coroutines & Flow
* **Networking:** Retrofit, OkHttp, Gson
* **Local Database:** Room
* **Charting:** Vico (Compose-first charting library)
* **Lifecycle:** lifecycle-runtime-compose

---

## How to Run the App

1. Install the latest version of **Android Studio** (Koala or newer recommended)
2. Ensure **JDK 17 or higher** is configured
3. Clone the repository:

```bash
git clone https://github.com/Ad12-Ad/Groww-Assignment.git
```

4. Open the project in Android Studio
5. Allow Gradle to sync and download all dependencies
6. Run the app on:

   * an Android Emulator, or
   * a physical device running API 24 (Android 7.0) or higher

---

## Final Note

This project emphasizes **production-level Android development practices**, including:

* Clean and scalable architecture
* Efficient state management
* Performance optimization
* Reactive and modern UI design

---
