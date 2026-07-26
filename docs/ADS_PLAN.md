# TolongTukar — Ad Integration Plan

> Status: **Plan only — no implementation yet.**
> Created: July 2026

---

## 1. Ad Network: Google AdMob

**Why AdMob:**
- Industry standard for Android/iOS apps
- KMP-compatible via platform-specific implementations (Android: Google Mobile Ads SDK, iOS: Google Mobile Ads SDK)
- Supports banner, interstitial, rewarded, native ads
- Free to use, no upfront cost
- Pay-per-impression (CPM) + pay-per-click (CPC)

**Alternative considered:** Meta Audience Network (Facebook ads). Rejected — AdMob has better fill rates in Malaysia/SEA market and simpler KMP integration.

---

## 2. Ad Formats & Placement Strategy

### 2.1 Banner Ad — Home Screen
- **Format:** 320×50 dp banner (standard AdMob banner)
- **Placement:** Bottom of HomeScreen, below the category grid
- **Frequency:** Always visible on home screen
- **Revenue:** Lowest CPM (~$0.50-$1.00 per 1,000 impressions in SEA)
- **UX impact:** Minimal — doesn't block content

### 2.2 Interstitial Ad — Converter Exit
- **Format:** Full-screen ad
- **Trigger:** When user exits a converter screen (back press) after spending >30 seconds in that screen
- **Frequency cap:** Max 1 interstitial per 2 minutes, max 3 per session
- **Revenue:** Higher CPM (~$2-$5 per 1,000 impressions)
- **UX impact:** Moderate — must not appear on every back-press. Cooldown timer prevents annoyance.

### 2.3 Native Ad — Between Converter Units (currency only)
- **Format:** Native ad styled to match app theme (material design card)
- **Placement:** After every 10th unit in the currency converter list (30 currencies = 2 native ad slots)
- **Frequency:** Only in currency category (most-used, highest session duration)
- **Revenue:** Medium CPM (~$1.50-$3.00 per 1,000 impressions)
- **UX impact:** Low if styled well — appears as a card between units

### 2.4 Rewarded Ad — Remove Ads (optional future)
- **Format:** Full-screen video, user opts in
- **Trigger:** User taps "Remove ads for 24 hours" → watches ad → ads disabled for 24h
- **Revenue:** Highest CPM (~$10-$20 per 1,000 views)
- **UX impact:** Positive — user chooses to engage, gets reward
- **Implementation:** Store "ads_disabled_until" timestamp in SettingsStorage

---

## 3. KMP Architecture for Ads

```
┌─────────────────────────────────────┐
│           commonMain                │
│  ┌─────────────────────────────┐    │
│  │    AdManager (interface)    │    │  ← expect class
│  │  - showBanner()             │    │
│  │  - showInterstitial()       │    │
│  │  - isAdsDisabled(): Bool    │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │  AdState (ViewModel)        │    │  ← controls when/where to show
│  │  - lastInterstitialTime     │    │
│  │  - sessionAdCount           │    │
│  │  - cooldownMs = 120_000     │    │
│  └─────────────────────────────┘    │
└─────────────────────────────────────┘
          │                    │
          ▼                    ▼
┌──────────────┐     ┌──────────────┐
│  androidMain  │     │   iosMain    │
│              │     │              │
│ Google Mobile│     │ Google Mobile│
│  Ads SDK     │     │  Ads SDK     │
│ (com.google. │     │ (Pods)       │
│  android.gms │     │              │
│  .ads)       │     │              │
└──────────────┘     └──────────────┘
```

### Dependencies
- **Android:** `implementation 'com.google.android.gms:play-services-ads:23.3.0'`
- **iOS:** Google Mobile Ads via CocoaPods (pod 'Google-Mobile-Ads-SDK')
- **commonMain:** No dependency — only interface + state logic

### Ad Unit IDs (placeholder — register app in AdMob first)
| Slot | Android Ad Unit ID | iOS Ad Unit ID |
|------|-------------------|----------------|
| Home banner | `ca-app-pub-XXXXX/YYYYYY1` | `ca-app-pub-XXXXX/YYYYYY2` |
| Converter interstitial | `ca-app-pub-XXXXX/YYYYYY3` | `ca-app-pub-XXXXX/YYYYYY4` |
| Currency native | `ca-app-pub-XXXXX/YYYYYY5` | `ca-app-pub-XXXXX/YYYYYY6` |

---

## 4. Frequency Capping Rules

| Rule | Value | Rationale |
|------|-------|-----------|
| Interstitial cooldown | 2 minutes | Prevent ad fatigue |
| Max interstitials per session | 3 | Hard cap |
| Session timeout threshold | 30s in screen | Don't show on quick lookups |
| Native ad spacing | Every 10 units | Non-intrusive |
| Rewarded cooldown | 24 hours | Don't abuse reward system |

### Implementation in AdState:
```kotlin
class AdState {
    var lastInterstitialTime: Long = 0
    var sessionAdCount: Int = 0
    val cooldownMs = 120_000L  // 2 minutes
    val maxPerSession = 3

    fun shouldShowInterstitial(): Boolean {
        val now = System.currentTimeMillis()
        return sessionAdCount < maxPerSession &&
               (now - lastInterstitialTime) > cooldownMs
    }
}
```

---

## 5. Build Configuration

### Gradle: ads vs no-ads flavors
```kotlin
// build.gradle.kts
android {
    flavorDimensions += "ads"
    productFlavors {
        create("free") {
            dimension = "ads"
            // AdMob SDK included
        }
        create("pro") {
            dimension = "ads"
            // No AdMob SDK — paid version
        }
    }
}
```

- **Free flavor:** Includes AdMob, shows ads
- **Pro flavor:** No AdMob SDK, no ads, user pays (one-time purchase or subscription)
- This enables a freemium model: free with ads, or paid ad-free

---

## 6. Revenue Projections (Estimates)

| Metric | Value (Malaysia/SEA) |
|--------|---------------------|
| Daily active users (DAU) — target | 500-2,000 |
| Sessions per user/day | 3-5 |
| Banner impressions/day | 1,500-10,000 |
| Interstitial impressions/day | 500-3,000 |
| Estimated CPM (blended) | $0.80-$2.00 |
| **Daily revenue** | **$2-$15** |
| **Monthly revenue** | **$60-$450** |

> These are rough estimates. Actual revenue depends on fill rate, eCPM, user geography, and engagement. Malaysia SEA eCPM is lower than US/EU.

---

## 7. Compliance & Privacy

- **Privacy Policy:** Required by AdMob + Play Store. Must disclose data collection (advertising ID, device info).
- **App-Tracking Transparency (iOS):** iOS 14+ requires user permission to track. Show a pre-permission dialog explaining why.
- **GDPR/CCPA:** Show consent form (Google UMP SDK) on first launch for EU/CA users.
- **COPPA:** Mark app as "not directed at children" in AdMob console.
- **Play Store Data Safety form:** Declare ad data collection.

---

## 8. Implementation Steps (when ready)

1. Register TolongTukar in [AdMob Console](https://admob.google.com)
2. Create 6 ad units (banner, interstitial, native × Android/iOS)
3. Add AdMob SDK dependencies (Android + iOS)
4. Create `AdManager` expect/actual pattern
5. Implement `AdState` frequency capping
6. Add `AdView` composable to HomeScreen bottom
7. Add interstitial trigger on converter exit
8. Add native ad cards in currency list
9. Add Google UMP consent form
10. Test with test ad unit IDs during development
11. Submit app for AdMob review
12. Switch to production ad unit IDs after approval

---

## 9. What NOT to Do

- ❌ Don't show ads on every screen transition
- ❌ Don't use popup ads that block the converter
- ❌ Don't show interstitials on first launch
- ❌ Don't place ads over the keyboard area
- ❌ Don't use more than 1 banner per screen
- ❌ Don't implement without frequency capping
