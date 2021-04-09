package edu.illinois.cs.cs125.spring2020.mp.logic;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cs125.spring2020.mp.R;

/**
 * Represents an area mode game. Keeps track of cells and the player's most recent capture.
 * <p>
 * All these functions are stubs that you need to implement.
 * Feel free to add any private helper functions that would be useful.
 * See {@link TargetGame} for an example of how multiplayer games are handled.
 */
public final class AreaGame extends Game {

    // You will probably want some instance variables to keep track of the game state
    // (similar to the area mode gameplay logic you previously wrote in GameActivity)
    /** The grid generated through AreaDivider.*/
    private AreaDivider grid;

    /** Map of the x-locations visited by the player.*/
    private Map<String, List<Integer>> playerPathX = new HashMap<>();

    /** Map of the y-locations visited by the plater.*/
    private Map<String, List<Integer>> playerPathY = new HashMap<>();

    /** The visited condition of the map.*/
    private boolean[][] mapCondition;

    /** The captured team condition of the map.*/
    private int[][] teamCondition;
    /**
     * Creates a game in area mode.
     * <p>
     * Loads the current game state from JSON into instance variables and populates the map
     * to show existing cell captures.
     * @param email the user's email
     * @param map the Google Maps control to render to
     * @param webSocket the websocket to send updates to
     * @param fullState the "full" update from the server
     * @param context the Android UI context
     */
    public AreaGame(final String email, final GoogleMap map, final WebSocket webSocket,
                    final JsonObject fullState, final Context context) {
        super(email, map, webSocket, fullState, context);
        int cellSize = fullState.get("cellSize").getAsInt();
        double areaNorth = fullState.get("areaNorth").getAsDouble();
        double areaSouth = fullState.get("areaSouth").getAsDouble();
        double areaEast = fullState.get("areaEast").getAsDouble();
        double areaWest = fullState.get("areaWest").getAsDouble();
        grid = new AreaDivider(areaNorth, areaEast, areaSouth, areaWest, cellSize);
        grid.renderGrid(map);
        mapCondition = new boolean[grid.getXCells()][grid.getYCells()];
        teamCondition = new int[grid.getXCells()][grid.getYCells()];
        JsonArray cells = fullState.get("cells").getAsJsonArray();
        for (JsonElement element : cells) {
            JsonObject cell = element.getAsJsonObject();
            int x = cell.get("x").getAsInt();
            int y = cell.get("y").getAsInt();
            LatLng northeast = grid.getCellBounds(x, y).northeast;
            LatLng southwest = grid.getCellBounds(x, y).southwest;
            LatLng northwest = new LatLng(
                    grid.getCellBounds(x, y).northeast.latitude,
                    grid.getCellBounds(x, y).southwest.longitude);
            LatLng southeast = new LatLng(
                    grid.getCellBounds(x, y).southwest.latitude,
                    grid.getCellBounds(x, y).northeast.longitude);
            Polygon target = map.addPolygon(new PolygonOptions().add(northeast, southeast,
                    southwest, northwest));
            int[] teamColorArray = getContext().getResources().getIntArray(R.array.team_colors);
            target.setFillColor(teamColorArray[cell.get("team").getAsInt()]);
            mapCondition[x][y] = true;
            teamCondition[x][y] = cell.get("team").getAsInt();

        }

        for (JsonElement p : fullState.get("players").getAsJsonArray()) {
            JsonObject player = p.getAsJsonObject();
            String playerEmail = player.get("email").getAsString();

            // Create a list to hold the IDs of targets visited by the player, in order
            List<Integer> path = new ArrayList<>();
            playerPathX.put(playerEmail, path);

            // Examine each target in the player entry's path
            for (JsonElement t : player.getAsJsonArray("path")) {
                JsonObject target = t.getAsJsonObject();
                int xValue = target.get("x").getAsInt();
                path.add(xValue);
            }
        }

        for (JsonElement p : fullState.get("players").getAsJsonArray()) {
            JsonObject player = p.getAsJsonObject();
            String playerEmail = player.get("email").getAsString();

            // Create a list to hold the IDs of targets visited by the player, in order
            List<Integer> path = new ArrayList<>();
            playerPathY.put(playerEmail, path);

            // Examine each target in the player entry's path
            for (JsonElement t : player.getAsJsonArray("path")) {
                JsonObject target = t.getAsJsonObject();
                int yValue = target.get("y").getAsInt();
                path.add(yValue);
            }
        }

    }

    /**
     * Called when the user's location changes.
     * <p>
     * Area mode games detect whether the player is in an uncaptured cell. Capture is possible if
     * the player has no captures yet or if the cell shares a side with the previous cell captured by
     * the player. If capture occurs, a polygon with the team color is added to the cell on the map
     * and a cellCapture update is sent to the server.
     * @param location the player's most recently known location
     */
    @Override
    public void locationUpdated(final LatLng location) {
        super.locationUpdated(location);
        int x = grid.getXIndex(location);
        int y = grid.getYIndex(location);
        List<Integer> locationsX = playerPathX.get(getEmail());
        List<Integer> locationsY = playerPathY.get(getEmail());


        if (x != -1 && y != -1) {

            if (mapCondition[x][y]) {
                return;
            }

            if (locationsX.size() > 0) {
                int previousX = locationsX.get(locationsX.size() - 1);
                int previousY = locationsY.get(locationsY.size() - 1);
                int xDiff = Math.abs(x - previousX);
                int yDiff = Math.abs(y - previousY);
                if (((xDiff == 1) && (previousY == y)) || ((yDiff == 1) && (previousX == x))) {
                    LatLng northeast = grid.getCellBounds(x, y).northeast;
                    LatLng southwest = grid.getCellBounds(x, y).southwest;
                    LatLng northwest = new LatLng(
                            grid.getCellBounds(x, y).northeast.latitude,
                            grid.getCellBounds(x, y).southwest.longitude);
                    LatLng southeast = new LatLng(
                            grid.getCellBounds(x, y).southwest.latitude,
                            grid.getCellBounds(x, y).northeast.longitude);
                    Polygon target = getMap().addPolygon(new PolygonOptions().add(northeast, southeast,
                            southwest, northwest));
                    int[] teamColorArray = getContext().getResources().getIntArray(R.array.team_colors);
                    target.setFillColor(teamColorArray[getMyTeam()]);
                    mapCondition[x][y] = true;
                    teamCondition[x][y] = getMyTeam();
                    for (Map.Entry<String, List<Integer>> entry : playerPathX.entrySet()) {
                        if (entry.getKey().equals(getEmail())) {
                            List<Integer> xLocations = entry.getValue();
                            xLocations.add(x);
                            playerPathX.put(getEmail(), xLocations);
                        }
                    }

                    for (Map.Entry<String, List<Integer>> entry : playerPathY.entrySet()) {
                        if (entry.getKey().equals(getEmail())) {
                            List<Integer> yLocations = entry.getValue();
                            yLocations.add(y);
                            playerPathY.put(getEmail(), yLocations);
                        }
                    }
                    JsonObject cellCapture = new JsonObject();
                    cellCapture.addProperty("type", "cellCapture");
                    cellCapture.addProperty("x", x);
                    cellCapture.addProperty("y", y);
                    sendMessage(cellCapture);

                }


            } else {
                LatLng northeast = grid.getCellBounds(x, y).northeast;
                LatLng southwest = grid.getCellBounds(x, y).southwest;
                LatLng northwest = new LatLng(
                        grid.getCellBounds(x, y).northeast.latitude,
                        grid.getCellBounds(x, y).southwest.longitude);
                LatLng southeast = new LatLng(
                        grid.getCellBounds(x, y).southwest.latitude,
                        grid.getCellBounds(x, y).northeast.longitude);
                Polygon target = getMap().addPolygon(new PolygonOptions().add(northeast, southeast,
                        southwest, northwest));
                int[] teamColorArray = getContext().getResources().getIntArray(R.array.team_colors);
                target.setFillColor(teamColorArray[getMyTeam()]);
                mapCondition[x][y] = true;
                teamCondition[x][y] = getMyTeam();
                for (Map.Entry<String, List<Integer>> entry : playerPathX.entrySet()) {
                    if (entry.getKey().equals(getEmail())) {
                        List<Integer> xLocations = entry.getValue();
                        xLocations.add(x);
                        playerPathX.put(getEmail(), xLocations);
                    }
                }

                for (Map.Entry<String, List<Integer>> entry : playerPathY.entrySet()) {
                    if (entry.getKey().equals(getEmail())) {
                        List<Integer> yLocations = entry.getValue();
                        yLocations.add(y);
                        playerPathY.put(getEmail(), yLocations);
                    }
                }
                JsonObject cellCapture = new JsonObject();
                cellCapture.addProperty("type", "cellCapture");
                cellCapture.addProperty("x", x);
                cellCapture.addProperty("y", y);
                sendMessage(cellCapture);
            }
        }

    }

    /**
     * Processes an update from the server.
     * <p>
     * Since playerCellCapture events are specific to area mode games, this function handles those
     * by placing a polygon of the capturing player's team color on the newly captured cell and
     * recording the cell's new owning team.
     * All other message types are delegated to the superclass.
     * @param message JSON from the server (the "type" property indicates the update type)
     * @return whether the message type was recognized
     */
    @Override
    public boolean handleMessage(final JsonObject message) {
        if (super.handleMessage(message)) {
            return true;
        }

        if (message.get("type").getAsString().equals("playerCellCapture")) {
            String playerEmail = message.get("email").getAsString();
            int team = message.get("team").getAsInt();
            int x = message.get("x").getAsInt();
            int y = message.get("y").getAsInt();
            mapCondition[x][y] = true;
            teamCondition[x][y] = team;
            for (Map.Entry<String, List<Integer>> entry : playerPathX.entrySet()) {
                if (entry.getKey().equals(playerEmail)) {
                    List<Integer> xLocations = entry.getValue();
                    xLocations.add(x);
                    playerPathX.put(playerEmail, xLocations);
                }
            }

            for (Map.Entry<String, List<Integer>> entry : playerPathY.entrySet()) {
                if (entry.getKey().equals(playerEmail)) {
                    List<Integer> yLocations = entry.getValue();
                    yLocations.add(y);
                    playerPathY.put(playerEmail, yLocations);
                }
            }

            LatLng northeast = grid.getCellBounds(x, y).northeast;
            LatLng southwest = grid.getCellBounds(x, y).southwest;
            LatLng northwest = new LatLng(
                    grid.getCellBounds(x, y).northeast.latitude,
                    grid.getCellBounds(x, y).southwest.longitude);
            LatLng southeast = new LatLng(
                    grid.getCellBounds(x, y).southwest.latitude,
                    grid.getCellBounds(x, y).northeast.longitude);
            Polygon target = getMap().addPolygon(new PolygonOptions().add(northeast, southeast,
                    southwest, northwest));
            int[] teamColorArray = getContext().getResources().getIntArray(R.array.team_colors);
            target.setFillColor(teamColorArray[team]);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets a team's score in this area mode game.
     * @param teamId the team ID
     * @return the number of cells owned by the team
     */
    @Override
    public int getTeamScore(final int teamId) {
        int count = 0;
        for (int i = 0; i < teamCondition.length; i++) {
            for (int j = 0; j < teamCondition[i].length; j++) {
                if (teamCondition[i][j] == teamId) {
                    count++;
                }
            }
        }
        return count;

    }

}
