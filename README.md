# Lean Tracker

A minimal Android app that tracks changes on website content with intelligent cookie management and visual content selection.

## Features

### 🎯 Content Tracking
- Accept URLs shared from browsers via Android intents
- Periodic polling with configurable intervals (default 24 hours)
- Automatic change detection on tracked websites
- Visual diff notifications when changes occur
- Auto-naming: `[selector] - [page title]`

### 🎨 Visual Element Selection
- Point-and-click element selection in WebView
- CSS selector and XPath support
- Select all similar elements with one tap
- Optional regex filtering after element selection

### 🔐 Security & Authentication
- Encrypted credential storage using EncryptedSharedPreferences
- WebView for secure login flows
- Smart cookie management:
  - Keeps authentication cookies for faster checks
  - Discards tracking cookies after each session
  - Automatic cookie consent rejection when possible

### 📱 User Experience
- Material Design 3 UI with Jetpack Compose
- Clear visual diffs showing exactly what changed
- Simple, intuitive interface
- Low battery and data usage

## Architecture

### Tech Stack
- **UI**: Jetpack Compose (Material 3)
- **Database**: Room
- **Background Work**: WorkManager
- **Security**: EncryptedSharedPreferences
- **Networking**: OkHttp + JSoup
- **WebView**: AndroidX WebKit

### Project Structure
```
app/src/main/java/dev/gatopeich/leantracker/
├── data/                   # Database entities and DAOs
│   ├── TrackedItem.kt
│   ├── AuthCookie.kt
│   ├── TrackedItemDao.kt
│   ├── AuthCookieDao.kt
│   └── AppDatabase.kt
├── domain/                 # Business logic
│   ├── TrackedItemRepository.kt
│   └── ContentFetcher.kt
├── ui/                     # Compose UI screens
│   ├── MainScreen.kt
│   ├── AddEditScreen.kt
│   ├── DetailScreen.kt
│   ├── ElementSelectorScreen.kt
│   ├── MainViewModel.kt
│   ├── AddEditViewModel.kt
│   ├── DetailViewModel.kt
│   └── theme/
├── worker/                 # Background tasks
│   └── ContentCheckWorker.kt
├── util/                   # Utilities
│   ├── SecureStorage.kt
│   ├── CookieManager.kt
│   └── ContentDiffer.kt
├── MainActivity.kt
└── LeanTrackerApp.kt
```

### Key Components

**Data Layer**
- Room database for persistent storage
- Encrypted SharedPreferences for credentials
- Separate tables for tracked items and auth cookies

**Domain Layer**
- Repository pattern for data access
- ContentFetcher for HTTP requests and HTML parsing
- Cookie management utilities

**UI Layer**
- Compose-based screens with Material 3
- ViewModels for state management
- Navigation between screens

**Background Work**
- WorkManager for reliable periodic checks
- Notification system for change alerts
- Network-aware scheduling

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or newer
- Android SDK API 34

### Building the App

1. Clone the repository:
```bash
git clone https://github.com/gatopeich/lean-tracker.git
cd lean-tracker
```

2. Open in Android Studio:
   - File → Open → Select the project directory

3. Sync Gradle:
   - Wait for Gradle sync to complete
   - Resolve any SDK/dependency issues if prompted

4. Build the APK:
```bash
./gradlew assembleRelease
```
The APK will be in `app/build/outputs/apk/release/`

### Running the App

**On emulator/device:**
```bash
./gradlew installDebug
```

**Or in Android Studio:**
- Select your device/emulator
- Click Run (▶️)

## Usage Guide

### Tracking a New Website

1. **From a Browser**:
   - Navigate to the page you want to track
   - Share → Select "Lean Tracker"
   - The URL will be pre-filled

2. **Manual Entry**:
   - Open Lean Tracker
   - Tap the ➕ button
   - Enter the URL

### Selecting Content

1. **Visual Selection**:
   - Tap "Visual Select"
   - The page loads in a WebView
   - Tap on the element you want to track
   - The CSS selector is auto-generated
   - Tap ✓ to confirm

2. **Manual Selector**:
   - Enter a CSS selector (e.g., `.article-title`)
   - Or use XPath (e.g., `//div[@class='content']`)

3. **Optional Regex**:
   - Add a regex pattern to filter the extracted text
   - Example: `\d+` to extract only numbers

### Managing Items

- **View Details**: Tap on any tracked item
- **View Diff**: Changes are highlighted in green/red
- **Edit**: Tap "Edit" from the detail screen
- **Delete**: Tap the 🗑️ icon on any item

### Authentication

For sites requiring login:
- The app opens a WebView for you to log in
- Authentication cookies are saved securely
- You won't need to log in again for future checks
- Tracking cookies are automatically discarded

## Configuration

### Polling Interval
- Default: 24 hours
- Configurable per tracked item
- Minimum: 1 hour (recommended)

### Notifications
- Enable in Android Settings → Apps → Lean Tracker → Notifications
- Shows when content changes are detected
- Tap to view the diff

## APK Size

Target: <12 MB
- Achieved through ProGuard optimization
- Minimal dependencies
- Resource shrinking enabled

## Requirements

See [REQUIREMENTS.md](REQUIREMENTS.md) for detailed RFC 2119 requirements specification.

### Summary (MUST/SHALL/MAY)

**MUST**:
- Accept URLs via intents
- Periodic polling (default 24h)
- Detect and notify changes
- Visual element selection
- Encrypted credential storage
- Discard tracking cookies

**SHALL**:
- Accept cookies when rejection is complex

**MAY**:
- Configure polling intervals
- Keep auth cookies between checks
- Allow editing item names

## License

Apache License 2.0 - See [LICENSE](LICENSE) file for details.

## Contributing

This is a minimal app by design. Contributions should focus on:
- Bug fixes
- Security improvements
- Code size reduction
- Performance optimization

Keep changes minimal and well-documented.

## Privacy

- No data collection or tracking
- No third-party analytics
- All data stored locally
- User has full control over all data
- No internet access except for user-specified URLs

## Support

For issues or questions:
- Open an issue on GitHub
- Include Android version and device model
- Provide steps to reproduce any bugs

## Roadmap

Future considerations (keeping minimal philosophy):
- Import/export tracked items
- More sophisticated diff algorithms
- Better cookie consent detection
- Widget support
