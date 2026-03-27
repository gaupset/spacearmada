package com.invaders99.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Assigns sabotage targets so each player attacks exactly one other player and
 * is attacked by exactly one (a bijection). Uses a random cyclic permutation
 * (no self-targets) when there are at least two players.
 *
 * <p>When a player is eliminated, {@link #reassignAfterElimination} splices the ring so the
 * attacker who targeted them now targets that player’s former victim.
 */
public final class SabotageTargetAssignment {

    private SabotageTargetAssignment() {}

    /**
     * Updates targeting after {@code p_elim} is removed from the match.
     *
     * <p>Let {@code pred} be the unique player with {@code currentTargets.get(pred) = p_elim}, and
     * {@code succ = currentTargets.get(p_elim)}. The returned map is like {@code currentTargets} but
     * without {@code p_elim} as a key, and with {@code pred} mapping to {@code succ} instead of
     * {@code p_elim} (the chain skips the eliminated node).
     *
     * <p>If {@code pred} and {@code succ} are the same (only possible when two players remained
     * and the other was eliminated), the surviving player is dropped from the map so callers can
     * clear {@code sabotageTargetId} instead of assigning a self-target.
     *
     * @param currentTargets attacker session id → victim session id for players still in the ring
     * @param eliminatedPlayerId {@code p_elim}; must not be {@code null}
     * @return new map; never mutates {@code currentTargets}
     */
    public static Map<String, String> reassignAfterElimination(
            Map<String, String> currentTargets, String eliminatedPlayerId) {
        if (currentTargets == null || currentTargets.isEmpty() || eliminatedPlayerId == null) {
            return new HashMap<>();
        }
        String succ = currentTargets.get(eliminatedPlayerId);
        String pred = null;
        for (Map.Entry<String, String> e : currentTargets.entrySet()) {
            if (eliminatedPlayerId.equals(e.getValue())) {
                pred = e.getKey();
                break;
            }
        }
        HashMap<String, String> out = new HashMap<>(currentTargets);
        out.remove(eliminatedPlayerId);
        if (pred != null) {
            out.remove(pred);
            if (succ != null && !pred.equals(succ)) {
                out.put(pred, succ);
            }
        }
        return out;
    }

    /**
     * @return map from session player id to the session id of their sabotage victim.
     *         Empty when there are fewer than two players (solo lobby cannot satisfy the rules).
     */
    public static Map<String, String> assignTargets(List<String> playerIds, Random random) {
        ArrayList<String> ids = new ArrayList<>(playerIds);
        Collections.shuffle(ids, random);
        Map<String, String> out = new HashMap<>();
        int n = ids.size();
        if (n < 2) {
            return out;
        }
        for (int i = 0; i < n; i++) {
            String from = ids.get(i);
            String to = ids.get((i + 1) % n);
            out.put(from, to);
        }
        return out;
    }

    /** Body for PATCH {@code /lobbies/{lobbyId}.json} (merge). */
    public static String toStartGamePatchBody(Map<String, String> sabotageTargets) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"gameStarted\":true");
        if (!sabotageTargets.isEmpty()) {
            sb.append(",\"players\":{");
            boolean first = true;
            for (Map.Entry<String, String> e : sabotageTargets.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(escapeJson(e.getKey())).append("\":");
                sb.append("{\"sabotageTargetId\":\"").append(escapeJson(e.getValue())).append("\"}");
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
