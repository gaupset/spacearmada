% Tutorial ECS Implementation (Local Notes)

This document describes the tutorial flow implementation and how it integrates with the existing ECS in `app/core`.
It is intentionally local-only and ignored by Git.

## Goals

- Provide a multi-step tutorial without special-casing core gameplay systems.
- Keep ECS purity: tutorial “rules” live in components + systems, not ad-hoc flags in state classes.
- Reuse existing movement/shooting/collision systems where possible.
- Keep UI states (`State` classes) focused on screens, navigation, and HUD wiring.

## New tutorial-specific classes

### `no.ntnu.tdt4240.project.engine.component.TutorialScenarioComponent`

- **Purpose**: ECS data holder for tutorial rules and progression state.
- Key constants:
  - `MODE_POWERUP = 0`
  - `MODE_SABOTAGE = 1`
  - `FORCED_INVINCIBILITY_SECONDS = 99999f`
- Fields:
  - `int mode`: which tutorial “scenario” is currently active (powerup vs sabotage).
  - `int scoreThreshold`: score needed to trigger a pause (default `5`).
  - `float nextPromptDelaySeconds`: delay before the `NEXT` prompt appears after selecting a powerup.
  - `boolean pauseRequested`: set when the tutorial wants gameplay to pause at the threshold.
  - `boolean powerupChosen`: set when the player has selected a powerup.
  - `boolean sabotageChosen`: set when the player has selected a sabotage.
  - `boolean nextPromptVisible`: set when the “Press NEXT to continue” prompt should be visible.
  - `float postSelectionElapsedSeconds`: timer used to reveal the `NEXT` prompt after powerup selection.
- Attached to a single entity per tutorial gameplay state (`TutorialCombatState`, `TutorialSabotageState`).

### `no.ntnu.tdt4240.project.engine.system.TutorialScenarioSystem`

- **Purpose**: drive tutorial-specific rules using ECS, based on `TutorialScenarioComponent`.
- Family: all entities with `TutorialScenarioComponent`.
- Behavior:
  - **Forced invincibility**:
    - Every frame, iterates entities with `PlayerComponent` + `HealthComponent` and sets
      `health.invincibilityRemaining = FORCED_INVINCIBILITY_SECONDS`.
    - This makes the player effectively invincible for the entire tutorial gameplay segments,
      while still using the existing collision/health systems.
  - **Powerup scenario (`MODE_POWERUP`)**:
    - If `!powerupChosen` and `score >= scoreThreshold`, sets `pauseRequested = true`.
    - After `powerupChosen == true`, increments `postSelectionElapsedSeconds` and
      once it reaches `nextPromptDelaySeconds`, sets `nextPromptVisible = true`
      (this controls when the `NEXT` button/text appear).
  - **Sabotage scenario (`MODE_SABOTAGE`)**:
    - If `!sabotageChosen` and `score >= scoreThreshold`, sets `pauseRequested = true`.
  - Reads score from ECS:
    - Finds entities with `PlayerComponent` + `ScoreComponent` and uses their `score` field.

### `no.ntnu.tdt4240.project.engine.system.TutorialPlayerShootingSystem`

- **Purpose**: a stripped-down shooting system for tutorial gameplay where only the player shoots.
- Family: `ShooterComponent` + `PlayerComponent`, excluding `BulletComponent` (i.e., non-bullet player entities).
- Behavior:
  - Uses `ShooterComponent.baseInterval` and `ShooterComponent.timer` for timing.
  - Reads `PowerupEffectsComponent` (if present) and:
    - If `rapidFireRemaining > 0`, multiplies interval by `PowerupEffectsComponent.PLAYER_SHOOT_INTERVAL_MULTIPLIER` (0.5x interval → 2x fire rate).
  - Spawns bullets via `EntityAssembler.createPlayerBullet(...)`, using the existing bullet configs.
- This keeps the tutorial aligned with the core shooting model (ShooterComponent + ECS bullet spawning),
  without allowing enemy shooting.

### `no.ntnu.tdt4240.project.state.TutorialState`

- **Purpose**: entry point screen for the tutorial from the main menu.
- UI only, no ECS:
  - Shows:
    - `Welcome to Spacearmada`
    - `Click CONTINUE to begin the tutorial`
  - On first `CONTINUE`:
    - Changes text to: `Use the touchscreen to move sideways`.
  - On second `CONTINUE`:
    - Transitions to `TutorialGameState` (movement-only gameplay).

### `no.ntnu.tdt4240.project.state.TutorialGameState`

- **Purpose**: tutorial phase 1 – movement-only, no enemies, no buttons except `NEXT`.
- ECS setup:
  - Creates an `Engine`, `GameLayout`, player entity via `EntityAssembler`.
  - Adds systems:
    - `InputSystem`
    - `MovementSystem`
    - `BounceSystem`
    - `BoundSystem`
    - `CollisionSystem`
    - `EventSystem`
    - `RemovalSystem`
    - `RenderSystem`
  - **No** spawn/shooting systems added, so there are no enemies or bullets.
- HUD:
  - Uses tutorial-mode `GameHud` with `NEXT` only; no pause, menu, sabotage, or powerup buttons.
  - After 5 seconds of gameplay, displays prompt:
    - `Press NEXT to progress to the next step`
  - `NEXT` transitions to `TutorialCombatIntroState`.

### `no.ntnu.tdt4240.project.state.TutorialCombatIntroState`

- **Purpose**: tutorial phase 2 – explain scoring and powerups before combat.
- UI only:
  - Shows:
    - `Shoot enemies to gain points`
    - `Gain 5 points to earn a power up`
  - `CONTINUE` starts `TutorialCombatState`.

### `no.ntnu.tdt4240.project.state.TutorialCombatState`

- **Purpose**: tutorial phase 3 – combat-focused tutorial to teach earning powerups.
- ECS setup:
  - Creates player + singletons:
    - `SabotageEffectsComponent` entity
    - `PowerupEffectsComponent` entity
    - `WaveComponent` entity
    - `TutorialScenarioComponent` entity with:
      - `mode = MODE_POWERUP`
      - `scoreThreshold = 5`
      - `nextPromptDelaySeconds = 5`
  - Adds systems:
    - `InputSystem`
    - `MovementSystem`
    - `BounceSystem`
    - `BoundSystem`
    - `CollisionSystem`
    - `EventSystem`
    - `WaveSystem`
    - `SpawnSystem` (enemies spawn, but only player shoots)
    - `TutorialPlayerShootingSystem` (player only)
    - `TutorialScenarioSystem`
    - `PowerupEffectSystem`
    - `RemovalSystem`
    - `RenderSystem`
- HUD:
  - Tutorial-mode `GameHud` configured with:
    - `NEXT` listener:
      - If tutorial scenario has `nextPromptVisible == true`, transitions to `TutorialSabotageIntroState`.
    - `POWERUP` listener:
      - If current tutorial scenario allows powerup (pause requested & not yet chosen), pushes `TutorialPowerupState`.
- Behavior through ECS:
  - `TutorialScenarioSystem`:
    - When score ≥ 5 and no powerup chosen → sets `pauseRequested = true`.
  - `TutorialCombatState.update()`:
    - If `shouldPauseTutorial()` (reads `pauseRequested` from ECS) → calls `setTutorialSystemsPaused(true)`:
      - Pauses all non-render systems; the world “freezes” visually but HUD still updates.
  - `canOpenPowerup()`:
    - True when `pauseRequested && !powerupChosen`, used to decide if POWERUP button is actionable.
  - Rendering:
    - When `canOpenPowerup()` is true:
      - Shows text: `Click POWERUP` and makes the tutorial POWERUP button visible.
    - After powerup is chosen:
      - `TutorialScenarioSystem` runs the post-selection timer and then sets `nextPromptVisible = true`.
      - HUD shows: `Press NEXT to continue the tutorial` with `NEXT` visible.

### `no.ntnu.tdt4240.project.state.TutorialPowerupState`

- **Purpose**: overlay to let the player choose one powerup during the tutorial.
- UI behavior:
  - Shows `Choose a powerup` with 3 `SpaceButton`s:
    - `Shield (invincibility)` (TYPE_SHIELD)
    - `2x fire rate` (TYPE_RAPID_FIRE)
    - `0.5x enemy speed` (TYPE_SLOW_ENEMIES)
  - On selection:
    - Calls `TutorialCombatState.applyPowerup(type)`:
      - Applies the corresponding `PowerupStrategy` to ECS `PowerupEffectsComponent`.
      - Marks `tutorial.powerupChosen = true`, `pauseRequested = false`, and resets the post-selection timer.
      - Unpauses gameplay via `setTutorialSystemsPaused(false)`.
    - Pops itself off the state stack (back to `TutorialCombatState`).
- Note: No unpause timer here – gameplay resumes immediately after powerup selection.

### `no.ntnu.tdt4240.project.state.TutorialSabotageIntroState`

- **Purpose**: tutorial phase 4 – explain sabotage earning.
- UI only:
  - Shows:
    - `Earn 5 points to sabotage another player`
  - `CONTINUE` transitions to `TutorialSabotageState`.

### `no.ntnu.tdt4240.project.state.TutorialSabotageState`

- **Purpose**: tutorial phase 5 – sabotage-focused tutorial.
- ECS setup:
  - Similar to `TutorialCombatState` but for sabotage:
    - Player, effects, wave, and a `TutorialScenarioComponent` with:
      - `mode = MODE_SABOTAGE`
      - `scoreThreshold = 5`
  - Systems:
    - `InputSystem`
    - `MovementSystem`
    - `BounceSystem`
    - `BoundSystem`
    - `CollisionSystem`
    - `EventSystem`
    - `WaveSystem`
    - `SpawnSystem`
    - `TutorialPlayerShootingSystem`
    - `TutorialScenarioSystem`
    - `PowerupEffectSystem`
    - `RemovalSystem`
    - `RenderSystem`
- HUD:
  - Tutorial-mode `GameHud` with only a SABOTAGE button (no POWERUP).
  - SABOTAGE listener:
    - If `canOpenSabotage()` is true, opens `TutorialSabotageChoiceState`.
- Behavior:
  - `TutorialScenarioSystem`:
    - When score ≥ 5 and no sabotage chosen → `pauseRequested = true`.
  - `TutorialSabotageState.update()`:
    - On `pauseRequested == true`, pauses all non-render systems.
  - Rendering:
    - When `canOpenSabotage()` (pause requested & not chosen) → shows text `Click SABOTAGE` and shows SABOTAGE button.

### `no.ntnu.tdt4240.project.state.TutorialSabotageChoiceState`

- **Purpose**: overlay for sabotage selection in the tutorial.
- UI:
  - Title: `Choose a sabotage`.
  - Three sabotage options with labels mirroring main game:
    - `2x enemy speed`
    - `0.5x player bullets`
    - `2x number of aliens`
  - On click:
    - Calls `TutorialSabotageState.useSabotage()`:
      - Marks ECS `TutorialScenarioComponent.sabotageChosen = true` and clears `pauseRequested`.
      - Unpauses gameplay.
    - Immediately sets the state to `TutorialEndingState` (no further gameplay; next is tutorial wrap-up UI).

### `no.ntnu.tdt4240.project.state.TutorialEndingState`

- **Purpose**: tutorial wrap-up screens and exit back to main menu.
- Flow:
  1. After sabotage selection:
     - Shows: `The selected sabotage is now applied to another player`
  2. `CONTINUE`:
     - Shows:
       - `When you are hit by an enemy or enemy bullet, you lose a life`
       - `You also lose a life when an enemy hits the bottom of the screen`
  3. Second `CONTINUE`:
     - Shows:
       - `Congratulations! You have finished the tutorial`
       - `Click EXIT TUTORIAL to return to the main menu`
     - Changes button label from `CONTINUE` to `EXIT TUTORIAL`.
  4. `EXIT TUTORIAL`:
     - `sm.set(new MenuState(sm, batch, assets))`.

## Changes to existing classes

### `no.ntnu.tdt4240.project.ui.view.GameHud`

- **New tutorial mode constructor**:
  - `public GameHud(Runnable nextListener, Runnable powerupListener, Runnable sabotageListener)`
  - Creates a HUD variant with:
    - `NEXT` button in the top-right.
    - Optional `POWERUP` button (tutorial-only, hidden by default).
    - Optional `SABOTAGE` button (tutorial-only, hidden by default).
- **New method**:
  - `actTutorial(float delta, int score, int health, int wave, String promptText, boolean isPowerupVisible, boolean isSabotageVisible, boolean isNextVisible)`
  - Updates:
    - Score/health/wave labels.
    - A bottom-center `tutorialLabel` with arbitrary prompt text.
    - Visibility of tutorial `POWERUP`, `SABOTAGE`, and `NEXT` buttons independently.
- **Important**:
  - The default constructor and main-game HUD behavior remain unchanged.
  - Tutorial mode is enabled only via the new constructor and is used exclusively in tutorial states.

### `no.ntnu.tdt4240.project.engine.Mapper`

- Added mapper entry:
  - `public static final ComponentMapper<TutorialScenarioComponent> tutorialScenario = ComponentMapper.getFor(TutorialScenarioComponent.class);`
- This allows all tutorial-aware systems/states to fetch `TutorialScenarioComponent` efficiently.

### `no.ntnu.tdt4240.project.engine.system.MovementSystem`, `ShootingSystem`, `PowerupEffectSystem`, `CollisionSystem`, `SpawnSystem`

- These systems are **reused as-is** in the tutorial:
  - **MovementSystem**:
    - Continues to handle `Slow Enemies` powerup via `PowerupEffectsComponent`.
  - **ShootingSystem**:
    - Not used in tutorial; replaced by `TutorialPlayerShootingSystem` to disable enemy fire while keeping the same component model.
  - **PowerupEffectSystem**:
    - Continues to tick `PowerupEffectsComponent` timers down each frame.
  - **CollisionSystem**:
    - Handles scoring on enemy hits, and life loss – but tutorial’s forced invincibility prevents actual life loss.
  - **SpawnSystem**:
    - Spawns enemies and uses wave config for pacing.

### `no.ntnu.tdt4240.project.state.GameState` and other core states

- No functional changes were required for main-game states:
  - The tutorial lives entirely in its own state classes and ECS configuration.
  - The only shared pieces are ECS components, systems, and `GameHud` (via the new tutorial constructor).

## Summary

- The tutorial system is now:
  - **ECS-aligned**: rules stored in `TutorialScenarioComponent`, driven by `TutorialScenarioSystem`, reusing existing movement/collision/powerup systems.
  - **UI decoupled**: states (`Tutorial*State`) orchestrate screens and navigation, while ECS handles gameplay behavior.
  - **Safe**: main gameplay (`GameState`, multiplayer, sabotage/powerups) is unaffected by tutorial-specific logic.

