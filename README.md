# Invaders 99
TDT4240

## Setup guide
1. `firebase login` - should not be needed. If it is, let me know, and i will invite to the Firebase project.
2. `cd firebase/functions && npm run build` to compile firebase functions (Only need to be done once, cane use `npm run build:watch` to autocompile when you start to edit the functions)
3. `cd firebase && firebase emulators:start` in a new terminal to start emulators
4. Open and start Android project as usual.
5. You can check Firebase connection status by navigating to the settings of the app

# Deploy:
`firebase deploy --only functions,db`