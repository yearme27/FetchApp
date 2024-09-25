# Fecth APP

## Overview
This Android application retrieves data from an API and displays it in a grouped and sorted list format using `RecyclerView`. The app uses the **MVVM** (Model-View-ViewModel) architecture to manage data efficiently and ensure a clear separation between the UI and business logic.

## Features
- **Data Retrieval**: Fetches data from a remote API (`https://fetch-hiring.s3.amazonaws.com/hiring.json`).
- **MVVM Architecture**: Implements the Model-View-ViewModel design pattern for a clean separation of concerns.
- **RecyclerView**: Displays a list of items grouped by `listId` with headers.
- **Expandable Sections**: Headers are clickable, allowing the user to expand or collapse sections.
- **Sticky Headers**: Headers stay in place while scrolling for a better user experience.
- **Error Handling**: Displays error messages if data fetching fails.

## Technology Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Libraries**:
  - **Retrofit**: For API calls and network communication.
  - **Gson**: For JSON serialization and deserialization.
  - **LiveData & ViewModel**: For lifecycle-aware data handling.
  - **RecyclerView**: For efficient and flexible list display.
  
## Installation and Setup
### Prerequisites
- **Android Studio**: Install the latest version from [here](https://developer.android.com/studio).
- **Android SDK**: Make sure you have Android SDK 21 or above.

### Steps
1. **Clone the Repository**:
    ```bash
    git clone https://github.com/yourusername/your-repo-name.git
    ```
2. **Open the Project**:
   - Open Android Studio.
   - Select **File > Open** and navigate to the cloned project directory.

3. **Sync the Project**:
   - Android Studio will automatically sync the project. If it doesn’t, click on **Sync Project with Gradle Files**.

4. **Build the Project**:
   - Click on the **Build** menu and select **Make Project**.

5. **Run the Application**:
   - Connect an Android device or start an emulator.
   - Click the **Run** button in Android Studio.
  
Project Structure
app/
app/
├── manifests/
│   └── AndroidManifest.xml
├── kotlin+java/
│   └── com.example.fetchapp/
│       ├── model/
│       │   ├── ApiService.kt              # Retrofit API interface
│       │   ├── Item.kt                   # Data model class
│       │   ├── ItemRepository.kt         # Repository for API calls
│       │   ├── ItemViewModelFactory.kt   # Factory for ViewModel creation
│       ├── ui.theme/                     # Theme-related files
│       ├── view/
│       │   ├── ItemAdapter.kt            # Adapter for RecyclerView
│       │   ├── MainActivity.kt           # Main screen of the app
│       │   ├── ResultActivity.kt         # Activity for displaying the list
│       ├── viewmodel/
│       │   ├── ItemViewModel.kt          # ViewModel handling UI data
│       │   ├── StickyHeaderClickListener.kt # Listener for sticky header clicks
│       │   ├── StickyItemDecoration.kt   # Custom ItemDecoration for sticky headers
├── res/
│   ├── drawable/
│   ├── layout/
│   │   ├── activity_main.xml             # Layout for MainActivity
│   │   ├── activity_result.xml           # Layout for ResultActivity
│   │   ├── item_layout.xml               # Layout for RecyclerView items
│   │   ├── list_header_layout.xml        # Layout for RecyclerView headers
└── com.example.fetchapp/
    ├── androidTest/
    ├── test/



## How the MVVM Architecture is Implemented
- **Model**:
  - `Item.kt`: Represents the data model for items.
  - `ItemRepository.kt`: Responsible for making network requests using `Retrofit` and returning data to the ViewModel.

- **View**:
  - `MainActivity.kt`: Contains a button to navigate to `ResultActivity`.
  - `ResultActivity.kt`: Sets up the `RecyclerView`, observes data from the ViewModel, and updates the UI using `ItemAdapter`.
  - `ItemAdapter.kt`: Handles data binding to the `RecyclerView`.

- **ViewModel**:
  - `ItemViewModel.kt`: Retrieves data from the `ItemRepository` and provides it to the View via `LiveData`.

## Usage
1. Launch the app.
2. Tap on the button in the main screen to navigate to the list screen (`ResultActivity`).
3. The app will fetch data from the API, group it by `listId`, and display it in a `RecyclerView`.
4. You can click on headers to expand or collapse the list items.
