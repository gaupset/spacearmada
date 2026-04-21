export interface LobbyPlayer {
  actualName: string;
  personalHighScore: number;
  inGameOverScreen: boolean;
  gameOver: boolean;
  leftLobby: boolean;
  lastTimeOnline: number;
  score: number;
  sabotage?: {type: string; duration: number} | null;
  sabotageTargetId?: string | null;
}

export interface RunnerState {
  id: string;
  time: number;
}

export interface LobbyData {
  gameStarted: boolean;
  gameEnded: boolean;
  lobbyCreatedAt?: number;
  gameEndedAt?: number | null;
  winnerId?: string | null;
  runner?: RunnerState | null;
  players: Record<string, LobbyPlayer>;
}
