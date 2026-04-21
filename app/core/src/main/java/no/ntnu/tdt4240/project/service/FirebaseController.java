package no.ntnu.tdt4240.project.service;

public class FirebaseController {
    private final LobbyService lobbyService;

    public LobbyService lobbyHandler() {return lobbyService;}

    public FirebaseController() {
        this.lobbyService = new LobbyService();
    }

    public void createLobby(LobbyService.LobbyCallback callback) {
        lobbyService.createLobby(callback);
    }

    public void joinLobby(String code, LobbyService.LobbyCallback callback) {
        lobbyService.joinLobby(code, callback);
    }

    public void leaveLobby(LobbyService.LobbyCallback callback) {
        lobbyService.leaveLobby(callback);
    }

    public void getLobbyStatus(LobbyService.LobbyStatusCallback callback) {
        lobbyService.getLobbyStatus(callback);
    }

    public void startGame(LobbyService.LobbyCallback callback) {
        lobbyService.startGame(callback);
    }

    public void pingGameHandler() {
        lobbyService.pingGameHandler();
    }

    public String getLobbyID() {
        return lobbyService.getLobbyID();
    }
}
