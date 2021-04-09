package edu.illinois.cs.cs125.spring2020.mp.logic;



import android.graphics.Color;

import androidx.annotation.VisibleForTesting;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;



/** AreaDivider.*/
public class AreaDivider {
    /**latitude of the north boundary.*/
    private double north;

    /**latitude of the south boundary.*/
    private double south;

    /**longitude of the west boundary.*/
    private double west;

    /**longitude of the east boundary.*/
    private double east;

    /**the requested side length of each cell, in meters.*/
    private int cellSize;

    /**
     *
     * @param setNorth north bound input.
     * @param setEast east bound input.
     * @param setSouth south bound input.
     * @param setWest west bound input.
     * @param setCellSize cell size in meters.
     */
    public AreaDivider(final double setNorth,
                       final double setEast,
                       final double setSouth,
                       final double setWest,
                       final int setCellSize) {
        north = setNorth;
        south = setSouth;
        west = setWest;
        east = setEast;
        cellSize = setCellSize;
    }


    /** Gets the boundaries of the specified cell as a Google Maps LatLngBounds object.
     * @param x the x value of the cell
     * @param y the y value of the cell
     * @return get boundaries of the cell as a LatLngBounds object*/
    public LatLngBounds getCellBounds(final int x, final int y) {
        double xChange = (east - west) / getXCells();
        double yChange = (north - south) / getYCells();
        double lngSouthWest = west + x * xChange;
        double latSouthWest = south + y * yChange;
        double lngNorthEast = west + (x + 1) * xChange;
        double latNorthEast = south + (y + 1) * yChange;
        return new LatLngBounds(new LatLng(latSouthWest, lngSouthWest),
                new LatLng(latNorthEast, lngNorthEast));

    }

    /** Gets the number of cells between the west and east boundaries.
     * @return int number of cells in x-direction*/
    public int getXCells() {
        double diffHorizontal = LatLngUtils.distance(south, west, south, east) / cellSize;
        return (int) Math.ceil(diffHorizontal);
    }

    /** Gets the X coordinate of the cell containing the specified location.
     * @param position position of a given point
     * @return x-index of the cell given a LatLng object*/
    public int getXIndex(final LatLng position) {
        if (position.longitude > east || position.longitude < west) {
            return -1;
        } else {
            double testDistance = LatLngUtils.distance(position.latitude, position.longitude,
                    position.latitude, west);
            double totalDistance = LatLngUtils.distance(south, west, south, east);

            return (int) Math.floor(testDistance / totalDistance * getXCells());

        }

    }

    /** Gets the number of cells between the south and north boundaries.
     * @return int number of cells in y-direction*/
    public int getYCells() {
        double diffVertical = LatLngUtils.distance(south, west, north, west) / cellSize;
        return (int) Math.ceil((diffVertical));
    }

    /** Gets the Y coordinate of the cell containing the specified location.
     * @param position position of a given point
     * @return y-index of the cell given a LatLng object*/
    public int getYIndex(final LatLng position) {
        if (position.latitude > north || position.latitude < south) {
            return -1;
        } else {
            double testDistance = LatLngUtils.distance(position.latitude, position.longitude,
                    south, position.longitude);
            double totalDistance = LatLngUtils.distance(south, west, north, west);

            return (int) Math.floor(testDistance / totalDistance * getYCells());

        }
    }

    /** Returns whether the configuration provided to the constructor is valid.
     * @return whether the config is acceptable*/
    public boolean isValid() {
        boolean condition = true;
        if (LatLngUtils.same(north, south) || LatLngUtils.same(west, east)) {
            condition = false;
        } else if (cellSize <= 0 || south > north || west > east) {
            condition = false;
        }
        return condition;
    }

    /**
     * Adds a colored line to the Google map.
     * @param startLat the latitude of one endpoint of the line
     * @param startLng the longitude of that endpoint
     * @param endLat the latitude of the other endpoint of the line
     * @param endLng the longitude of that other endpoint
     * @param color the color to fill the line with
     * @param map google map
     */
    @VisibleForTesting
    public void addLine(final double startLat, final double startLng,
                        final double endLat, final double endLng, final int color,
                        final GoogleMap map) {
        // Package the loose coordinates into LatLng objects usable by Google Maps
        LatLng start = new LatLng(startLat, startLng);
        LatLng end = new LatLng(endLat, endLng);

        // Configure and add a colored line
        final int lineThickness = 12;
        PolylineOptions fill = new PolylineOptions().add(start, end).color(color).width(lineThickness).zIndex(1);
        map.addPolyline(fill);

    }

    /**
     * Draws the grid to a map using solid black polylines.
     * @param map google map
     */
    public void renderGrid(final GoogleMap map) {
        double xChange = (east - west) / getXCells();
        double yChange = (north - south) / getYCells();
        addLine(south, west, north, west, Color.BLACK, map);
        addLine(south, west, south, east, Color.BLACK, map);
        addLine(north, west, north, east, Color.BLACK, map);
        addLine(south, east, north, east, Color.BLACK, map);

        for (int i = 1; i <= getXCells() - 1; i++) {
            addLine(south, west + i * xChange,
                    north, west + i * xChange,
                    Color.BLACK, map);
        }

        for (int i = 1; i <= getYCells() - 1; i++) {
            addLine(south + i * yChange, west,
                    south + i * yChange, east,
                    Color.BLACK, map);
        }

    }

}
