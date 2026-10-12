# ABBX Keyboard

A beginner Android Input Method Service prototype with English keys, a basic Bengali character layout, and an emoji panel.

## Build on GitHub (phone-friendly)
1. Create a GitHub repository named `ABBXKeyboard`.
2. Upload all files and folders in this project, preserving their paths, to the `main` branch.
3. Open the **Actions** tab and select **Build ABBX Keyboard APK**.
4. Run the workflow. After it succeeds, download the `ABBX-Keyboard-APK` artifact ZIP.
5. Extract it and install `app-debug.apk` on your Android phone.
6. Open ABBX Keyboard, enable it in Android keyboard settings, then select it from the input method picker.

## Notes
- This is a prototype, not a fully featured replacement for Gboard.
- Bengali input is direct character input, not phonetic transliteration.
- The `?123` key is a placeholder and does not yet open a numeric layout.
- The app does not request internet permission and includes no code to transmit typed text.
- The APK has not been built or tested on a device in this environment. Review the GitHub Actions build log if it fails.
