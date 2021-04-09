package edu.illinois.cs.cs125.spring2020.mp.logic;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** information about the game.
 *
 */
public class GameSummary {
    /**Creates a game summary from JSON from the server.*/
    private JsonObject info;

    /** analyzing the JsonObject information from server.
     *
     * @param infoFromServer retrieved from cs125 server
     */
    public GameSummary(final JsonObject infoFromServer) {
        info = infoFromServer;
    }

    /** Gets the unique, server-assigned ID of this game.
     *
     * @return the game ID
     */
    public String getId() {

        return info.get("id").getAsString();
    }

    /** Gets the mode of this game, either area or target.
     *
     * @return the game mode
     */
    public String getMode() {
        return info.get("mode").getAsString();
    }

    /** Gets the owner/creator of this game.
     *
     * @return the email of the game's owner
     */
    public String getOwner() {
        return info.get("owner").getAsString();
    }

    /** Gets the name of the user's team/role.
     *
     * @param userEmail the logged-in user's email
     * @param context an Android context (for access to resources)
     * @return the human-readable team/role name of the user in this game
     */
    public String getPlayerRole(final String userEmail, final Context context) {
        JsonArray players = info.get("players").getAsJsonArray();
        for (JsonElement d : players) {
            JsonObject playerInArray = d.getAsJsonObject();
            if (playerInArray.get("email").getAsString().equals(userEmail)) {
                int teamNum = playerInArray.get("team").getAsInt();
                if (TeamID.TEAM_RED == teamNum) {
                    return "Red";
                } else if (TeamID.TEAM_YELLOW == teamNum) {
                    return "Yellow";
                } else if (TeamID.TEAM_GREEN == teamNum) {
                    return "Green";
                } else if (TeamID.TEAM_BLUE == teamNum) {
                    return "Blue";
                } else if (TeamID.OBSERVER == teamNum) {
                    return "Observer";
                }
            }
        }
        return "";
    }

    /** Determines whether this game is an invitation to the user.
     *
     * @param userEmail the logged-in user's email
     * @return whether the user is invited to this game
     */
    public boolean isInvitation(final String userEmail) {
        JsonArray players = info.get("players").getAsJsonArray();
        int gameState = info.get("state").getAsInt();
        if (gameState != GameStateID.ENDED) {
            for (JsonElement d : players) {
                JsonObject playerInArray = d.getAsJsonObject();
                if (playerInArray.get("email").getAsString().equals(userEmail)
                        && playerInArray.get("state").getAsInt() == PlayerStateID.INVITED) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Determines whether the user is currently involved in this game.
        For a game to be ongoing, it must not be over and the user must have accepted their invitation to it.
     * @param userEmail the logged0in user's email
     * @return whether this game is ongoing for the user
     */
    public boolean isOngoing(final String userEmail) {
        int gameState = info.get("state").getAsInt();
        JsonArray players = info.get("players").getAsJsonArray();
        for (JsonElement d : players) {
            JsonObject playerInArray = d.getAsJsonObject();
            if (playerInArray.get("email").getAsString().equals(userEmail)) {
                int playerState = playerInArray.get("state").getAsInt();
                if (gameState != GameStateID.ENDED
                        && (playerState == PlayerStateID.ACCEPTED
                        || playerState == PlayerStateID.PLAYING)) {
                    return true;
                }
            }
        }
        return false;
    }



}
