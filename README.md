# Spaceinvaders
TDT4240

## Simple Setup Guide
1. By default, app is configured to use production server. Simply open in a emulator or compile to JAR file amd run in environment that has internet access.

## Advanced Setup Guide
1. `firebase login` - should not be needed. If it is, let me know, and i will invite to the Firebase project.
2. `cd firebase/functions && npm run build` to compile firebase functions (Only need to be done once, cane use `npm run build:watch` to autocompile when you start to edit the functions)
3. `cd firebase && firebase emulators:start` in a new terminal to start emulators
3. Change `DEV_MODE` to **true** to switch to local endpoints. Local endpoints configured for android emulators only.
4. Open and start Android project as usual.
5. You can check Firebase connection status by navigating to the settings of the app

## Deploy
`firebase deploy --only functions,db`