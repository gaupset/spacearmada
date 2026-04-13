import {getDatabase, ServerValue} from "firebase-admin/database";
import * as logger from "firebase-functions/logger";
import {LobbyData, LobbyPlayer, RunnerState} from "./types";
import {assignTargets} from "./sabotageAssignment";

const RUNNER_STALE_MS = 2000;
const INACTIVE_PLAYER_MS = 10000;

interface GameHandlerResult {
  status: string;
  isRunner: boolean;
  message?: string;
}

/**
 * Handles an incoming game request for a lobby.
 * @param {string} lobbyId - The lobby identifier.
 * @param {string} lobbyUserId - The user identifier.
 * @param {string} action - Optional action to perform.
 * @return {Promise<GameHandlerResult>} The result of the request.
 */
export async function handleGameRequest(
  lobbyId: string,
  lobbyUserId: string,
  action?: string
): Promise<GameHandlerResult> {
  logger.info("handleGameRequest", {lobbyId, lobbyUserId, action});

  const db = getDatabase();
  const lobbyRef = db.ref(`lobbies/${lobbyId}`);

  const lobbySnap = await lobbyRef.get();
  logger.info("lobbySnap", {exists: lobbySnap.exists()});
  if (!lobbySnap.exists()) {
    return {status: "error", isRunner: false, message: "Lobby not found"};
  }
  const lobby = lobbySnap.val() as LobbyData;
  const playerCount = lobby.players ? Object.keys(lobby.players).length : 0;
  logger.info("lobby data", {gameStarted: lobby.gameStarted, playerCount});

  // Runner election via transaction
  const isRunner = await tryClaimRunner(lobbyId, lobbyUserId);

  // Handle explicit actions regardless of runner status
  if (action === "startGame") {
    return await handleStartGame(lobbyId, lobby);
  }

  // Only the runner performs management tasks
  if (isRunner) {
    await runGameManagement(lobbyId, lobby);
  }

  return {status: "ok", isRunner};
}

/**
 * Attempts to claim the runner role for a lobby via transaction.
 * @param {string} lobbyId - The lobby identifier.
 * @param {string} lobbyUserId - The user attempting to claim runner.
 * @return {Promise<boolean>} True if this user is now the runner.
 */
async function tryClaimRunner(
  lobbyId: string,
  lobbyUserId: string
): Promise<boolean> {
  const db = getDatabase();
  const runnerRef = db.ref(`lobbies/${lobbyId}/runner`);

  const result = await runnerRef.transaction(
    (current: RunnerState | null) => {
      const now = Date.now();
      if (!current || !current.time || (now - current.time > RUNNER_STALE_MS)) {
        return {id: lobbyUserId, time: now};
      }
      if (current.id === lobbyUserId) {
        return {id: lobbyUserId, time: now};
      }
      return undefined; // abort — someone else is active runner
    }
  );

  if (!result.committed) return false;
  const val = result.snapshot.val() as RunnerState | null;
  return val?.id === lobbyUserId;
}

/**
 * Handles the startGame action for a lobby.
 * @param {string} lobbyId - The lobby identifier.
 * @param {LobbyData} lobby - The current lobby data.
 * @return {Promise<GameHandlerResult>} The result of starting the game.
 */
async function handleStartGame(
  lobbyId: string,
  lobby: LobbyData
): Promise<GameHandlerResult> {
  if (lobby.gameStarted) {
    return {status: "error", isRunner: false, message: "Game already started"};
  }
  if (!lobby.players) {
    return {
      status: "error",
      isRunner: false,
      message: "No players in lobby",
    };
  }

  const eligibleIds = Object.entries(lobby.players)
    .filter(([, p]) => !p.leftLobby)
    .map(([id]) => id);

  if (eligibleIds.length < 2) {
    return {
      status: "error",
      isRunner: false,
      message: "Need at least 2 players to start",
    };
  }

  const targets = assignTargets(eligibleIds);

  const updates: Record<string, unknown> = {
    [`lobbies/${lobbyId}/gameStarted`]: true,
  };
  for (const [attackerId, victimId] of Object.entries(targets)) {
    updates[`lobbies/${lobbyId}/players/${attackerId}/sabotageTargetId`] =
      victimId;
  }

  const db = getDatabase();
  await db.ref().update(updates);

  logger.info(`Game started in lobby ${lobbyId}`, {
    playerCount: eligibleIds.length,
  });
  return {status: "ok", isRunner: false, message: "Game started"};
}

/**
 * Performs game management tasks for the runner: kicking inactive players
 * and ending or deleting the lobby when appropriate.
 * @param {string} lobbyId - The lobby identifier.
 * @param {LobbyData} lobby - The current lobby data.
 * @return {Promise<void>}
 */
async function runGameManagement(
  lobbyId: string,
  lobby: LobbyData
): Promise<void> {
  if (!lobby.players) return;

  const now = Date.now();
  const db = getDatabase();
  const updates: Record<string, unknown> = {};

  if (lobby.gameStarted && !lobby.gameEnded) {
    // Kick inactive players
    for (const [id, player] of Object.entries(lobby.players)) {
      if (player.gameOver || player.leftLobby) continue;
      if (
        player.lastTimeOnline > 0 &&
        now - player.lastTimeOnline > INACTIVE_PLAYER_MS
      ) {
        logger.info(`Kicking inactive player ${id} in lobby ${lobbyId}`);
        updates[`lobbies/${lobbyId}/players/${id}/gameOver`] = true;
        updates[`lobbies/${lobbyId}/players/${id}/leftLobby`] = true;
      }
    }

    // Apply kick updates before checking game end
    if (Object.keys(updates).length > 0) {
      await db.ref().update(updates);
    }

    // Re-read lobby to get current state after kicks
    const freshSnap = await db.ref(`lobbies/${lobbyId}`).get();
    if (!freshSnap.exists()) return;
    const freshLobby = freshSnap.val() as LobbyData;

    // Check if game should end
    if (shouldGameEnd(freshLobby.players)) {
      logger.info(`Ending game in lobby ${lobbyId}`);
      await db.ref(`lobbies/${lobbyId}`).update({
        gameEnded: true,
        gameEndedAt: ServerValue.TIMESTAMP,
      });
    }
  } else if (lobby.gameStarted && lobby.gameEnded) {
    // Check if lobby should be deleted
    if (shouldDeleteLobby(lobby.players, now)) {
      logger.info(`Deleting lobby ${lobbyId}`);
      await db.ref(`lobbies/${lobbyId}`).remove();
    }
  }
}

/**
 * Returns true if the game should end (one or fewer active players).
 * @param {Record<string, LobbyPlayer>} players - The players map.
 * @return {boolean} Whether the game should end.
 */
function shouldGameEnd(players: Record<string, LobbyPlayer>): boolean {
  let activeCount = 0;
  for (const player of Object.values(players)) {
    if (!player.gameOver && !player.leftLobby) {
      activeCount++;
    }
  }
  return activeCount <= 1;
}

/**
 * Returns true if the lobby should be deleted (all players offline).
 * @param {Record<string, LobbyPlayer>} players - The players map.
 * @param {number} now - Current timestamp in ms.
 * @return {boolean} Whether the lobby should be deleted.
 */
function shouldDeleteLobby(
  players: Record<string, LobbyPlayer>,
  now: number
): boolean {
  for (const player of Object.values(players)) {
    if (player.leftLobby) continue;
    const offline =
      player.lastTimeOnline > 0 &&
      now - player.lastTimeOnline > INACTIVE_PLAYER_MS;
    if (!offline) return false;
  }
  return true;
}
