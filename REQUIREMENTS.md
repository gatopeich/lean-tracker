# Lean Tracker Requirements Specification

This document specifies the requirements for the Lean Tracker Android application using RFC 2119 keywords (MUST, MUST NOT, SHALL, SHALL NOT, SHOULD, SHOULD NOT, MAY).

## Functional Requirements

### Content Tracking

**MUST** requirements:
- The app MUST accept URLs shared from browsers via Android intents
- The app MUST perform periodic polling with a default interval of 24 hours
- The app MUST detect content changes on tracked websites
- The app MUST notify users when changes are detected with visual diff
- Each tracked item MUST be named by default as: `[regex/selector] - [page title]`

**MAY** requirements:
- The app MAY allow users to configure custom polling intervals

### Content Selection

**MUST** requirements:
- The app MUST initially offer visual point-and-click selection in WebView
- The app MUST record selections using CSS selectors or XPath
- The app MUST allow users to gather all similar elements of the selected types
- The app MUST convert selected elements to text before applying regex

**MAY** requirements:
- The app MAY provide regex filtering after element selection

### Authentication & Security

**MUST** requirements:
- The app MUST manage user login credentials safely using encrypted storage
- The app MUST provide WebView for users to enter login details without cookies initially
- The app MUST discard tracking/non-auth cookies after each session

**MAY** requirements:
- The app MAY keep authentication cookies to accelerate subsequent checks

### Cookie Management

**MUST** requirements:
- The app MUST automatically reject cookies when rejection is easily detectable
- The app MUST NOT persist tracking cookies between sessions

**SHALL** requirements:
- The app SHALL accept cookies when rejection is not straightforward

**MAY** requirements:
- The app MAY keep authentication cookies between checks

### Architecture & Code

**MUST** requirements:
- The code MUST be reduced as much as possible without obscuring functionality
- The architecture MUST be minimal for the task in size and version
- The app MUST use WebView or equivalent for authentication flows

**MAY** requirements:
- The APK size MAY be up to 12 MB

### User Experience

**SHOULD** requirements:
- The app SHOULD provide clear visual diffs when changes occur
- The app SHOULD make content selection user-friendly

**MAY** requirements:
- The app MAY allow editing of tracked item names

## Technical Requirements

### Platform
- **Target SDK**: API 34 (Android 14)
- **Minimum SDK**: API 26 (Android 8.0)
- **Language**: Kotlin

### Dependencies
- Jetpack Compose for UI
- Room for database
- WorkManager for background tasks
- EncryptedSharedPreferences for credential storage
- OkHttp for HTTP networking
- WebKit for WebView functionality
- JSoup for HTML parsing

### Permissions
- INTERNET - Required for fetching web content
- POST_NOTIFICATIONS - Required for change notifications
- WAKE_LOCK - Required for reliable background work
- RECEIVE_BOOT_COMPLETED - Required for scheduling after reboot

## Security Requirements

**MUST** requirements:
- Credentials MUST be stored using EncryptedSharedPreferences
- Authentication cookies MUST be separated from tracking cookies
- Tracking cookies MUST be discarded after each session
- Database backups MUST exclude sensitive data

## Performance Requirements

**SHOULD** requirements:
- The app SHOULD minimize battery usage by efficient polling
- The app SHOULD use minimal memory footprint
- Background checks SHOULD only run when network is available

## Data Requirements

### Database Schema

**TrackedItem**:
- id (Primary Key)
- url (String)
- name (String)
- selector (String - CSS or XPath)
- regex (String, optional)
- intervalHours (Integer, default 24)
- lastContent (String, optional)
- lastCheckedAt (Timestamp, optional)
- createdAt (Timestamp)
- hasChange (Boolean)

**AuthCookie**:
- domain (Primary Key)
- cookies (String - JSON serialized)
- updatedAt (Timestamp)

## Compliance

The application complies with:
- Android app security best practices
- Material Design 3 guidelines
- Google Play Store policies
- GDPR requirements (no tracking, user data under user control)
