# Spacearmada
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

## Project Structure

```
spacearmada/
├── app/                              LibGDX multi-module Gradle project (game client)
│   ├── core/                         Platform-agnostic game code (shared by both launchers)
│   │   └── src/main/java/no/ntnu/tdt4240/project/
│   │       ├── Main.java             ApplicationAdapter entry point
│   │       ├── Assets.java           Asset loading
│   │       ├── GameInputProcessor.java
│   │       ├── state/                State pattern: StateManager + MenuState, WaitingRoomState,
│   │       │                         GameState, PauseState, SabotageState, PowerupState, ...
│   │       ├── engine/               Ashley ECS — component/, entity/, system/, Mapper
│   │       ├── event/                In-game event bus (Event, EventListener, EventManager)
│   │       ├── service/              FirebaseService, LobbyService, AudioService, ScoreService
│   │       ├── ui/                   Scene2D HUD — view/, UiFactory, SpaceButton
│   │       ├── model/                Domain model (Sabotage, Powerup)
│   │       ├── data/                 DTOs mirroring the DB shape (LobbyPlayer)
│   │       ├── powerup/strategy/     Strategy pattern: powerup effect implementations
│   │       ├── sabotage/strategy/    Strategy pattern: sabotage effect implementations
│   │       ├── config/               Player config
│   │       └── util/                 AppConfig, FirebaseJson, helpers
│   ├── android/                      Android launcher
│   ├── lwjgl3/                       Desktop (LWJGL3) launcher — builds the runnable JAR
│   ├── assets/                       Shared assets (textures, audio, fonts)
│   └── build.gradle, settings.gradle, gradlew
├── firebase/                         Firebase project (backend)
│   ├── functions/src/
│   │   ├── index.ts                  HTTPS entry point
│   │   ├── gameRunner.ts             Lobby lifecycle, runner election, sabotage routing
│   │   ├── sabotageAssignment.ts     Ring-based target assignment
│   │   └── types.ts                  Shared TS interfaces (LobbyData, LobbyPlayer, RunnerState)
│   ├── database.rules.json           RTDB security rules
│   └── firebase.json
├── docs/                             Architecture diagrams and figures
└── README.md
```

## Deploy
`firebase deploy --only functions,db`