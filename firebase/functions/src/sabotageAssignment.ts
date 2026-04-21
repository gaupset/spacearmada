import {LobbyPlayer} from "./types";

/**
 * Assigns sabotage targets so each player attacks exactly one other player
 * and is attacked by exactly one (a bijection). Uses a random cyclic
 * permutation (no self-targets) when there are at least two players.
 * @param {string[]} playerIds - List of player IDs to assign targets for.
 * @return {Record<string, string>} Map of attackerId to victimId.
 */
export function assignTargets(
  playerIds: string[]
): Record<string, string> {
  const ids = [...playerIds];
  // Fisher-Yates shuffle
  for (let i = ids.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [ids[i], ids[j]] = [ids[j], ids[i]];
  }
  const out: Record<string, string> = {};
  if (ids.length < 2) return out;
  for (let i = 0; i < ids.length; i++) {
    out[ids[i]] = ids[(i + 1) % ids.length];
  }
  return out;
}

/**
 * Walks the sabotage ring forward from `fromId`, returning the next
 * attackable player id: skips `selfId` and any player that has gameOver
 * or leftLobby set. Returns `fromId` if no rotation is possible.
 * @param {Record<string, LobbyPlayer>} players - Lobby players map.
 * @param {string} fromId - Starting cursor id (typically the current victim).
 * @param {string} selfId - Caller's id (never target self).
 * @return {string} The next target id.
 */
export function nextTargetInRing(
  players: Record<string, LobbyPlayer>,
  fromId: string,
  selfId: string
): string {
  if (!players || !fromId) return fromId;
  const maxSteps = Object.keys(players).length;
  let cursor = fromId;
  for (let i = 0; i < maxSteps; i++) {
    const link = players[cursor];
    if (!link || !link.sabotageTargetId) return fromId;
    const candidate = link.sabotageTargetId;
    const candidatePlayer = players[candidate];
    const inactive = !!candidatePlayer &&
      (candidatePlayer.gameOver || candidatePlayer.leftLobby);
    if (candidate !== selfId && !inactive) return candidate;
    cursor = candidate;
  }
  return fromId;
}

