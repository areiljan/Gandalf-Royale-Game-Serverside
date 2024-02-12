package ee.taltech.game.server.datamanagement;

public class LoginControl {
    private static final int MAX_PLAYERS = 10;
    private static final int[] idAvailability = new int[MAX_PLAYERS];
    LoginControl() {
        // Constructor.
    }
    public static LoginControl getInstance() {
        return new LoginControl();
    }

    public static int assignId(int id) {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (idAvailability[i] == 0) {
                idAvailability[i] = id;
                return id;
            }
        }
        return -1;
        // In this case, the lobby is full.
    }

    public static int disconnect(int idToDisconnect) {
        if (idAvailability[idToDisconnect] != 0) {
            idAvailability[idToDisconnect] = 0;
        }
        return idToDisconnect;
        // Removes a player from the list of players. Usable later.
    }
}