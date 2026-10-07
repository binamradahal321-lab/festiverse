# FestiVerse 3.1 — "Celebrate Our Heritage"

A modern Android app (Java + XML, no Kotlin) for Nepali festival reminders.
Fully OFFLINE — all 47 festival illustrations ship inside the APK.

## How to run
1. Unzip, then Android Studio > File > Open > select THIS `FestiVerseApp` folder.
2. Wait for Gradle sync, then Run.

## Features
- **47 festivals for 2026 (BS 2082–2083)** — Dashain (Ghatasthapana, Fulpati,
  Ashtami, Nawami, Dashami, Kojagrat), all Tihar days, both Holis, Teej + Rishi
  Panchami, Chhath, Jitiya, Janai Purnima, Nag Panchami, Gathemangal, Indra
  Jatra, jatras, all three Lhosars, Buddha Jayanti, Mani Rimdu, Tiji, Ubhauli +
  Udhauli, Mother's & Father's Day, Saraswati Puja, Guru Purnima, Saune &
  Maghe Sankranti, Christmas and more — each with a unique illustration and
  bilingual (English / नेपाली) description, sorted chronologically.
- **Bottom navigation** — Home · Calendar · Favorites.
- **Favorites** — tap the ♥ on any card or detail page; saved festivals appear
  in the Favorites tab, persisted across sessions.
- **Notify Me** — on any detail page, schedules a reminder notification for
  7:00 AM on the festival day (next year's occurrence if already passed).
  Reminders survive reboot (BootReceiver) and tapping the notification opens
  that festival. On Android 13+ the app asks for notification permission.
- **Nepali Calendar (नेपाली पात्रो)** — a full Bikram Sambat calendar like
  Hamro Patro: Nepali month + year header (e.g. "असोज २०८३") with the AD range
  below it, Devanagari weekday row with Saturday in red, days in Nepali
  numerals with the English date small underneath, today ring, selected-day
  highlight, festival dots, and a tap-to-see list of that day's festivals.
  Month navigation works across BS 2070-2099 with a verified calendar table.
- **Nepali-year "Passed" logic** — a festival shows PASSED only when its date
  has gone by in the CURRENT Bikram Sambat year, and countdowns roll to the
  same BS date in the NEXT BS year (labels show the BS year, e.g. "next in
  213d (२०८४)").
- **Login / Signup** (on-device demo auth), **dark/light mode**, dedicated
  **search page**, live hero countdown, PASSED badges with next-year timers.

## New in 3.1
- 60 festivals and national days (added Swasthani, Prithvi Jayanti, Martyrs' Day,
  Democracy Day, Women's Day, both Eids, Labour Day, Pancha Dan, Gaura, Constitution
  Day, Gai Tihar, Bala Chaturdashi) — each with its own illustration.
- Per-festival living animations on the hero and detail pages: flying kites for
  Dashain days, fireworks for Tihar and Indra Jatra, rising diyos for Shivaratri and
  Chhath, Holi color bursts, monsoon rain for Machindranath and Saune, Himalayan snow
  for the Lhosars and Christmas, drifting petals for Teej, starlight otherwise.
- Calendar bookmarks now fire a 7:00 AM reminder notification on the day (recurring
  every Nepali year, restored after reboot); types are Festival / Birthday.
- Smoother calendar: card-style grid, slide-fade month transitions.
- Feedback button moved to the top bar (small icon, always visible).
- Greeting uses a clean bold font.

## New in 3.0
- Festivals sorted by how soon they occur; the nearest one is always first (hero + list).
- Nepali (BS) date is now the bold, primary date on every card and detail page; the
  English date sits small beneath it.
- Live date sync: pull-to-refresh (and a silent check at launch) fetches updated
  festival dates from a JSON you host (see RemoteSync.REMOTE_URL). Hamro Patro has no
  public API, so host your own verified dates (GitHub raw works) and the app updates
  without a new APK. Fully offline-safe: any failure keeps bundled dates.
- Dynamic animation: Ken Burns zoom on hero and detail art, staggered card entrances.
- Emojis replaced with vector icons; new typography (condensed headers, script greeting).
- Calendar bookmarks: the + button on any selected day saves an Event or Birthday
  (recurs every Nepali year), shown with cake/star icons, gold dot on the grid,
  deletable inline.
- Feedback: a quiet "Send feedback" link at the bottom of Home opens an email draft.

## Notes
- Festival dates are lunar and shift each year — update the dataset annually.
- Demo auth only; use Firebase Auth or a backend for production.
- Emulators: use the emulator's date controls or extended controls to test
  notifications quickly.
