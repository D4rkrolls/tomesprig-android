# Quillfold

Quillfold is an Android reading prototype for AI research and technology news. Its foldable layout presents a two-page book spread on wider screens and a single page on narrower screens. Swipe left or right across the reading area to turn pages. Select a story to open its publisher page in a browser.

## What works in this build

- Rustic paper, ink, and leather reading interface
- Responsive one-page and two-page layouts
- Swipe and arrow page navigation
- Sample story links that open publisher pages
- Feed settings screen with a curated source list

Stories and source entries are demonstration content. This version does not fetch RSS feeds or persist custom feed URLs. No analytics, advertising, account, or crash-reporting SDKs are included.

## Try the Android build

Install the included debug APK on an Android device, or build from source with JDK 17+, Android SDK Platform 35, and Gradle 8.9 or newer:

```sh
gradle --no-daemon assembleDebug
```

Gradle writes the APK to `app/build/outputs/apk/debug/app-debug.apk`. Install a locally built APK with:

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The debug APK is intended for testing and is not a signed production release.

## Privacy

The app requests internet access for opening linked articles and future feed support. It does not request access to local files, contacts, location, camera, or microphone. Android backup is disabled. The prototype includes no analytics or tracking SDKs. Opening a story sends a normal request to its publisher through the device browser.

## License

Quillfold source code is released under the Apache License 2.0. See [LICENSE](LICENSE). Third-party libraries and publisher content retain their own licenses and terms. Sample article descriptions are illustrative and are not presented as live feed entries.
