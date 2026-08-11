---
layout: plain
permalink: /privacy
title: "Privacy Policy"
---

# Privacy Policy

**Product:** SkyFox  
**Maintainer:** Tay März  
**Last updated:** August 2026

SkyFox is a free and open-source Android flight tracker. It is based on the airplanes.live Android app by d4rken, used under the GPL-3.0 license. SkyFox is an independent project and is **not** affiliated with or endorsed by airplanes.live.

---

## What SkyFox collects

SkyFox does not collect, share, or sell personal information. No user account is required or created. SkyFox does not operate a user database and contains no advertising or behavioural tracking.

Normal app usage involves network requests to external services (described below). Those services will naturally receive your IP address as part of standard HTTP communication.

---

## External services

### airplanes.live

Aircraft position and flight data is fetched from the [airplanes.live](https://airplanes.live) API. Your device sends requests to their servers to retrieve nearby aircraft data. Their privacy policy is at: https://airplanes.live/privacy/

### Planespotters.net

Aircraft photographs are loaded from [Planespotters.net](https://www.planespotters.net) when available. Their privacy policy is at: https://www.planespotters.net/legal/privacypolicy

### GitHub

Update checks and release information are fetched from the GitHub API (`api.github.com`). No personal data is sent.

---

## Android permissions

### INTERNET

Required to fetch aircraft data, aircraft photos, and release information from external servers. Without internet access, the app cannot retrieve live aircraft data.

### ACCESS_COARSE_LOCATION

Provides a rough location estimate (2–4 km accuracy). Used to show nearby aircraft and center the map on your position. **This permission is optional.** You can decline it and pan the map manually; distance-based features will be unavailable.

### ACCESS_FINE_LOCATION

Provides higher-accuracy location (GPS-level). Used for the same purposes as coarse location where greater precision is desired. **This permission is optional.**

### CAMERA

Used by the augmented-reality (AR) view feature to overlay aircraft on the camera feed. **This permission is optional** and is only requested when you open the AR view.

### POST_NOTIFICATIONS

Used to deliver aircraft alerts (e.g. a watched aircraft appearing nearby, emergency squawk alerts, feeder offline notifications). **This permission is optional.** Declining it disables alert notifications.

---

## Local data

All app data (watchlists, alerts, settings, cached aircraft data) is stored locally on your device. SkyFox does not upload this data to any server.

You can export or delete local data at any time via the app settings.

---

## Debug log

SkyFox includes an optional debug log feature for troubleshooting. It is manually triggered through the app settings and records verbose app activity. The log file stays on your device until you choose to share it.

**The log may contain sensitive information**, including file paths, network request details, and details about your installed applications. Only share debug logs with trusted parties (e.g. when reporting a bug).

---

## Analytics and advertising

SkyFox contains no analytics, no advertising SDK, and no behavioural tracking of any kind.

---

## Contact

For privacy-related questions, open an issue at:  
https://github.com/taynotfound/skyfox/issues

---

*View this policy on GitHub: [PRIVACY_POLICY.md](https://github.com/taynotfound/skyfox/blob/main/PRIVACY_POLICY.md)*
