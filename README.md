<p align="center">
  <img src="branding/SkyFox_Logo.svg" width="160" alt="SkyFox logo">
</p>

<h1 align="center">SkyFox</h1>

<p align="center">A fast, privacy-friendly flight tracker for Android.</p>

<p align="center">
  <a href="https://github.com/taynotfound/skyfox/releases">Download the latest APK</a> ·
  <a href="https://github.com/taynotfound/skyfox/actions">Builds</a>
</p>

SkyFox uses [airplanes.live](https://airplanes.live) data to show nearby aircraft without requiring an account. It is an independent project and is not affiliated with or endorsed by airplanes.live.

## Features

- Live aircraft map with selectable map layers
- Search by HEX, callsign, registration, airframe, or SQUAWK
- Route information with origin and destination airports
- ICAO, callsign, SQUAWK, and location alerts
- Feeder monitoring with offline notifications
- Aircraft photos from Planespotters
- Watchlists, spotting statistics, and shareable summaries
- Start location and responsive aircraft details

## Download

**[Latest release →](https://github.com/taynotfound/skyfox/releases/latest)**

Versioned releases are published as signed FOSS release APKs. Install the `.apk` file — Android may ask you to allow installation from unknown sources.

**Nightly builds** (from every push to `main`) are available under the [`nightly` pre-release tag](https://github.com/taynotfound/skyfox/releases/tag/nightly). These are for testers and developers — they may be unstable.

## Build locally

```bash
./gradlew :app:assembleFossDebug
```

The APK is written to `app/build/outputs/apk/foss/debug/`.

## Credits

SkyFox is free and open source, licensed under GPL-3.0. See [LICENSE](LICENSE).

SkyFox is maintained by [Tay März](https://github.com/taynotfound).
