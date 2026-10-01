# Fetch App

## Overview
An Android app that retrieves data from an API and shows it as a grouped, sorted list in a `RecyclerView`. It uses the **MVVM** (Model-View-ViewModel) architecture to keep the UI and the data handling separate.

## Features
- **Data retrieval**: Fetches items from `https://fetch-hiring.s3.amazonaws.com/hiring.json`.
- **Filtering and sorting**: Items with a blank or missing name are dropped, then items are sorted by `listId` and by the number in the name.
- **Grouped list**: Items are grouped by `listId`, each group under a header.
- **Expandable sections**: Tap a header to expand or collapse its group. Open groups stay open after a rotation.
- **Sticky headers**: The current group's header stays pinned while you scroll.
- **Error handling**: A message is shown if the request fails or the server returns an error.

## Technology stack
- **Language**: Kotlin
- **Architecture**: MVVM
- **Libraries**: Retrofit and Gson for networking, LiveData and ViewModel for lifecycle-aware state, Kotlin coroutines, RecyclerView for the list.
- **Tests**: JUnit and Mockito unit tests for the ViewModel (`app/src/test`).

## Setup
### Prerequisites
- Android Studio (latest stable)
- Android SDK 24 or above (`minSdk` is 24)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/yearme27/FetchApp.git
   ```
2. Open the project in Android Studio and let Gradle sync.
3. Run the app on an emulator or a connected device.

## Project structure
```
app/src/main/java/com/example/fetchapp/
├── model/
│   ├── ApiService.kt              # Retrofit API interface
│   ├── Item.kt                    # Data model
│   ├── ItemRepository.kt          # Network calls
│   └── ItemViewModelFactory.kt    # Creates the ViewModel
├── view/
│   ├── MainActivity.kt            # Start screen with a button to the list
│   ├── ResultActivity.kt          # Shows the list
│   └── ItemAdapter.kt             # RecyclerView adapter (headers + expandable items)
├── viewmodel/
│   └── ItemViewModel.kt           # Loads, filters, sorts and groups the items
└── StickyItemDecoration.kt        # Draws the pinned header
```

## Usage
1. Launch the app.
2. Tap the button on the main screen to open the list.
3. The app fetches the data, groups it by `listId`, and shows it.
4. Tap a header to expand or collapse its items.
