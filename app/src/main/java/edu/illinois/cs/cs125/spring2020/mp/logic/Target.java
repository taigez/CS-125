package edu.illinois.cs.cs125.spring2020.mp.logic;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents a target in an ongoing target-mode game and manages the marker displaying it.
 */
public class Target {

    /** Create a map to render to.*/
    private GoogleMap map;

    /** The position of the target.*/
    private LatLng position;

    /** The team code of the team currently owning the target.*/
    private int team;

    /** The map descriptor.*/
    private BitmapDescriptor icon;

    /** The marker options.*/
    private  MarkerOptions options;

    /** Marker.*/
    private Marker marker;
    /**
     * Creates a target in a target-mode game by placing an appropriately colored marker on the map.
     * @param setMap the map to render to
     * @param setPosition the position of the target
     * @param setTeam the TeamID code of the team currently owning the target
     */
    public Target(final GoogleMap setMap, final LatLng setPosition, final int setTeam) {
        map = setMap;
        position = setPosition;
        team = setTeam;
        //BitmapDescriptor icon;
        options = new MarkerOptions().position(position);
        marker = map.addMarker(options);
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        marker.setIcon(icon);

        if (team == TeamID.TEAM_RED) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        } else if (team == TeamID.TEAM_YELLOW) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else if (team == TeamID.TEAM_GREEN) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else if (team == TeamID.TEAM_BLUE) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        }

        marker.setIcon(icon);
    }

    /**
     * Gets the position of the target.
     * @return the coordinates of the target.
     */
    public LatLng getPosition() {
        return position;
    }

    /**
     * Gets the ID of the team currently owning this target.
     * @return the owning team ID or OBSERVER if unclaimed.
     */
    public int getTeam() {
        return team;
    }

    /**
     * Updates the owning team of this target and updates the hue of the marker to match.
     * @param newTeam the ID of the team that captured the target.
     */
    public void setTeam(final int newTeam) {
        team = newTeam;
        if (team == TeamID.TEAM_RED) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        } else if (team == TeamID.TEAM_YELLOW) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else if (team == TeamID.TEAM_GREEN) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else if (team == TeamID.TEAM_BLUE) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        }

        marker.setIcon(icon);
    }
}
