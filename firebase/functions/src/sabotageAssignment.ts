/**
 * Assigns sabotage targets so each player attacks exactly one other player
 * and is attacked by exactly one (a bijection). Uses a random cyclic
 * permutation (no self-targets) when there are at least two players.
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
 * Splices the sabotage ring after a player is eliminated.
 * The predecessor now targets the eliminated player's former victim.
 */
export function reassignAfterElimination(
  currentTargets: Record<string, string>,
  eliminatedPlayerId: string
): Record<string, string> {
  if (!currentTargets || !eliminatedPlayerId) return {};
  const succ = currentTargets[eliminatedPlayerId];
  let pred: string | null = null;
  for (const [attacker, victim] of Object.entries(currentTargets)) {
    if (victim === eliminatedPlayerId) {
      pred = attacker;
      break;
    }
  }
  const out: Record<string, string> = {...currentTargets};
  delete out[eliminatedPlayerId];
  if (pred !== null) {
    delete out[pred];
    if (succ && pred !== succ) {
      out[pred] = succ;
    }
  }
  return out;
}
