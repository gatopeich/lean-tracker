# Lean Tracker UI Workflow

## Visual Overview

This document describes the user interface and workflow of the Lean Tracker app.

## Screen Flow

```
┌─────────────────┐
│  Main Screen    │ ← App Launch / Home
│  (List)         │
└────────┬────────┘
         │
         ├─→ [Tap Item] ──────────┐
         │                        │
         ├─→ [Tap + FAB] ─────┐   │
         │                    │   │
         └─→ [Share from      │   │
              Browser]        │   │
                             │   │
                    ┌────────▼───▼────────┐
                    │  Add/Edit Screen    │
                    │  (Form)             │
                    └────────┬────────────┘
                             │
                    ┌────────▼────────────┐
                    │  Visual Select      │
                    │  (Optional)         │
                    └────────┬────────────┘
                             │
                    ┌────────▼────────────┐
                    │  Element Selector   │
                    │  (WebView)          │
                    └─────────────────────┘

            ┌────────────────────┐
            │  Detail Screen     │
            │  (View/Diff)       │
            └────────────────────┘
```

## Screen Details

### 1. Main Screen

**Purpose**: Display list of tracked items

**Layout**:
```
┌──────────────────────────────────┐
│ ☰  Tracked Items              ⋮ │
├──────────────────────────────────┤
│                                  │
│  ┌────────────────────────────┐ │
│  │ Article Title - News Site  │ │
│  │ https://example.com/news   │ │
│  │ Last checked: Dec 30, 12:00│ │
│  │ ⚠️  Content changed!        │ │
│  └────────────────────────────┘ │
│                                  │
│  ┌────────────────────────────┐ │
│  │ Product Price - Shop       │ │
│  │ https://shop.com/product   │ │
│  │ Last checked: Dec 30, 11:00│ │
│  └────────────────────────────┘ │
│                                  │
│  ┌────────────────────────────┐ │
│  │ Weather Forecast           │ │
│  │ https://weather.com        │ │
│  │ Never checked              │ │
│  └────────────────────────────┘ │
│                                  │
│                                  │
│                              [+] │
└──────────────────────────────────┘
```

**Elements**:
- Top bar: "Tracked Items" title
- Item cards with:
  - Name (truncated if long)
  - URL (truncated)
  - Last checked timestamp
  - Change indicator (highlighted if changed)
  - Delete button (🗑️)
- Floating Action Button (+) to add new item
- Empty state when no items:
  ```
  No tracked items yet
  Share a URL from your browser to start tracking
  ```

### 2. Add/Edit Screen

**Purpose**: Configure tracking for a URL

**Layout**:
```
┌──────────────────────────────────┐
│ ←  Track New URL              ✓ │
├──────────────────────────────────┤
│                                  │
│  URL                             │
│  ┌────────────────────────────┐ │
│  │ https://example.com/page   │ │
│  └────────────────────────────┘ │
│                                  │
│  Name                            │
│  ┌────────────────────────────┐ │
│  │ Article Title - Example    │ │
│  └────────────────────────────┘ │
│                                  │
│  Selector                        │
│  ┌──────────────────┬─────────┐ │
│  │ .article-title   │ Visual  │ │
│  │                  │ Select  │ │
│  └──────────────────┴─────────┘ │
│                                  │
│  Regex (optional)                │
│  ┌────────────────────────────┐ │
│  │                            │ │
│  └────────────────────────────┘ │
│                                  │
│  Check Interval (hours)          │
│  ┌────────────────────────────┐ │
│  │ 24                         │ │
│  └────────────────────────────┘ │
│                                  │
│  ┌────────────────────────────┐ │
│  │          SAVE              │ │
│  └────────────────────────────┘ │
│                                  │
└──────────────────────────────────┘
```

**Features**:
- URL field (pre-filled if shared from browser)
- Name field (auto-filled from page title)
- Selector field with "Visual Select" button
- Optional regex field
- Interval selector (numeric input)
- Save button (enabled when URL and selector are filled)

### 3. Element Selector Screen

**Purpose**: Visual point-and-click element selection

**Layout**:
```
┌──────────────────────────────────┐
│ ←  Select Element             ✓ │
├──────────────────────────────────┤
│ Selector: .article-title         │
│ Preview: Breaking News: New...   │
├──────────────────────────────────┤
│ [WebView with Page Content]     │
│                                  │
│  ╔══════════════════════════╗   │
│  ║ Tap on an element to     ║   │
│  ║ select it                ║   │
│  ╚══════════════════════════╝   │
│                                  │
│  Page Title Here                 │
│                                  │
│  ┏━━━━━━━━━━━━━━━━━━━━━━━━┓   │
│  ┃ Article Title (selected) ┃   │ ← Highlighted
│  ┗━━━━━━━━━━━━━━━━━━━━━━━━┛   │
│                                  │
│  Article body text goes here     │
│  and continues...                │
│                                  │
└──────────────────────────────────┘
```

**Features**:
- Info bar showing generated selector and preview
- WebView displaying the actual page
- Purple instruction banner at top
- Click anywhere to select element
- Selected element highlighted with purple outline
- Check mark button to confirm selection

**JavaScript Injection**:
- Intercepts all clicks
- Generates CSS selector for clicked element
- Highlights selected element
- Passes selector back to native code

### 4. Detail Screen

**Purpose**: View tracked item details and content diff

**Layout**:
```
┌──────────────────────────────────┐
│ ←  Article Title            Edit │
├──────────────────────────────────┤
│                                  │
│  ┌────────────────────────────┐ │
│  │ URL                        │ │
│  │ https://example.com/page   │ │
│  │                            │ │
│  │ Selector                   │ │
│  │ .article-title             │ │
│  │                            │ │
│  │ Regex                      │ │
│  │ (none)                     │ │
│  └────────────────────────────┘ │
│                                  │
│  Content Diff                    │
│  ┌────────────────────────────┐ │
│  │   Unchanged line           │ │
│  │ - Old content removed      │ │ ← Red
│  │ + New content added        │ │ ← Green
│  │   Another unchanged line   │ │
│  └────────────────────────────┘ │
│                                  │
└──────────────────────────────────┘
```

**Features**:
- Metadata card showing URL, selector, regex
- Diff display with color coding:
  - Lines starting with `-` in red (removed)
  - Lines starting with `+` in green (added)
  - Lines starting with space (unchanged)
- Edit button in top bar
- Monospace font for diff

### 5. Notification

**Appearance**:
```
┌────────────────────────────────┐
│ 🔔 Lean Tracker                │
│    Content changed!            │
│    Change detected in Article  │
│    Title - News Site           │
│                         [VIEW] │
└────────────────────────────────┘
```

**Behavior**:
- Appears when content changes detected
- Tapping opens Detail screen for that item
- Auto-dismisses after viewing

## User Flows

### Flow 1: Share URL from Browser

```
1. User browses to a page in Chrome/Firefox/etc.
   ↓
2. User taps Share → Lean Tracker
   ↓
3. Add/Edit screen opens with URL pre-filled
   ↓
4. App fetches page title automatically
   ↓
5. User taps "Visual Select" button
   ↓
6. Element Selector screen loads the page
   ↓
7. User taps on desired element (e.g., article title)
   ↓
8. Element highlights in purple, selector shown at top
   ↓
9. User taps ✓ to confirm
   ↓
10. Returns to Add/Edit with selector filled
   ↓
11. User taps SAVE
   ↓
12. Returns to Main screen, item added to list
```

### Flow 2: View Change Notification

```
1. Background worker detects content change
   ↓
2. Notification appears
   ↓
3. User taps notification
   ↓
4. App opens Detail screen for that item
   ↓
5. User sees color-coded diff
   ↓
6. Change flag cleared after viewing
```

### Flow 3: Manual Entry

```
1. User taps + button on Main screen
   ↓
2. Add/Edit screen opens empty
   ↓
3. User types URL manually
   ↓
4. User types selector manually (e.g., ".price")
   ↓
5. Optionally adds regex pattern
   ↓
6. Adjusts check interval
   ↓
7. User taps SAVE
   ↓
8. Returns to Main screen, item added
```

## Color Scheme

### Light Theme
- Primary: Purple (#6200EE)
- Secondary: Teal (#03DAC5)
- Background: White (#FFFBFE)
- Surface: White
- Error: Red

### Dark Theme
- Primary: Light Purple (#BB86FC)
- Secondary: Teal (#03DAC5)
- Background: Dark Gray (#1C1B1F)
- Surface: Dark Gray
- Error: Light Red

### Semantic Colors
- Added content: Green (#00AA00)
- Removed content: Red (#AA0000)
- Change highlight: Primary container
- Element selection: Purple outline

## Typography

- **Titles**: Medium weight, 20sp
- **Body**: Regular weight, 16sp
- **Labels**: Small, 12sp
- **Diff text**: Monospace, 14sp

## Icons

- **Add**: + (Floating Action Button)
- **Delete**: 🗑️ (Trash icon)
- **Back**: ← (Arrow)
- **Confirm**: ✓ (Check mark)
- **Edit**: Edit icon (pencil)
- **Notification**: 🔔 (Bell)

## Interactions

### Tap Gestures
- Item card → Open Detail screen
- Delete icon → Show delete confirmation
- + FAB → Open Add/Edit screen
- Visual Select → Open Element Selector
- Element in WebView → Select element
- Notification → Open Detail screen

### Long Press
- (Not implemented for minimal design)

### Swipe
- (Not implemented for minimal design)

## Empty States

### Main Screen (No Items)
```
┌──────────────────────────────────┐
│                                  │
│                                  │
│       No tracked items yet       │
│                                  │
│   Share a URL from your browser  │
│        to start tracking         │
│                                  │
│                                  │
│                              [+] │
└──────────────────────────────────┘
```

### Detail Screen (Never Checked)
```
Current Content
┌────────────────────────────┐
│ (No content checked yet)   │
└────────────────────────────┘
```

## Error States

### Load Error in Element Selector
```
Error loading content
Please check your internet connection
```

### Invalid Selector
```
No elements found matching selector
Please try a different selector
```

## Accessibility

- All interactive elements have content descriptions
- Sufficient color contrast for text
- Touch targets minimum 48dp
- Text scalable
- Dark theme support

## Performance

- Smooth 60 FPS scrolling on Main screen
- Instant navigation between screens
- WebView loads progressively
- Background work doesn't block UI

---

This workflow documentation provides a complete picture of the app's user interface without requiring screenshots or running the app.
