package edu.illinois.cs.cs125.spring2020.mp.logic;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Provides static methods to convert game information to JSON payloads that
 * can be POSTed to the server's /games/create endpoint to create a multiplayer game.
 */
public class GameSetup {

    /**
     * Empty constructor.
     */
    public GameSetup() {

    }

    /**
     * Creates a JSON object representing the configuration of a multi player area mode game.
     * @param invitees all players involved in the game (never null)
     * @param area the area boundaries
     * @param cellSize the desired cell size in meters
     * @return a JSON object usable by the /games/create endpoint or null if the configuration is invalid
     */
    public static JsonObject areaMode(final List<Invitee> invitees,
                               final LatLngBounds area,
                               final int cellSize) {
        if (invitees.size() == 0 || cellSize <= 0) {
            return null;
        } else {


            JsonObject info = new JsonObject();
            info.addProperty("mode", "area");
            JsonArray people = new JsonArray();
            for (Invitee invitee : invitees) {
                JsonObject unique = new JsonObject();
                unique.addProperty("email", invitee.getEmail());
                unique.addProperty("team", invitee.getTeamId());
                people.add(unique);
            }
            info.add("invitees", people);
            info.addProperty("cellSize", cellSize);
            info.addProperty("areaNorth", area.northeast.latitude);
            info.addProperty("areaEast", area.northeast.longitude);
            info.addProperty("areaSouth", area.southwest.latitude);
            info.addProperty("areaWest", area.southwest.longitude);

            return info;
        }
    }

    /**
     * Creates a JSON object representing the configuration of a multi player target mode game.
     * @param invitees all players involved in the game (never null)
     * @param targets the positions of all targets (never null)
     * @param proximityThreshold the proximity threshold in meters
     * @return a JSON object usable by the /games/create endpoint or null if the configuration is invalid
     */
    public static  JsonObject targetMode(final List<Invitee> invitees,
                                         final List<LatLng> targets,
                                         final int proximityThreshold) {
        if (invitees.size() == 0 || proximityThreshold <= 0 || targets.size() == 0) {
            return null;
        } else {
            JsonObject info = new JsonObject();
            info.addProperty("mode", "target");
            JsonArray people = new JsonArray();
            for (Invitee invitee : invitees) {
                JsonObject unique = new JsonObject();
                unique.addProperty("email", invitee.getEmail());
                unique.addProperty("team", invitee.getTeamId());
                people.add(unique);
            }
            info.add("invitees", people);
            info.addProperty("proximityThreshold", proximityThreshold);
            JsonArray position = new JsonArray();
            for (LatLng value : targets) {
                JsonObject location = new JsonObject();
                location.addProperty("latitude", value.latitude);
                location.addProperty("longitude", value.longitude);
                position.add(location);
            }
            info.add("targets", position);
            return info;
        }

    }

}
