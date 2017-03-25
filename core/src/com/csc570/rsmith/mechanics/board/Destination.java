package com.csc570.rsmith.mechanics.board;

import java.util.*;

/**
 * Created by rsmith on 2/22/17.
 */
public class Destination {

    public static float TOTAL_WIDTH = 950;
    public static float TOTAL_HEIGHT = 600;

    private String locationName;

    private Set<Route> routes;
    private List<Destination> neighbors = new ArrayList<>();

    private float xCord;
    private float yCord;

    public Destination(String locationName) {
        this.locationName = locationName;
        routes = new HashSet<>();
        neighbors = new ArrayList<>();
    }

    public String getLocationName() {
        return locationName;
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    public List<Destination> getNeighbors() {
        return new ArrayList<>(neighbors);
    }

    public float getxCord() {
        return xCord;
    }

    public float getyCord() {
        return yCord;
    }

    // FUTHER INITIALIZATION

    /**
     * This method is to set the coordinates of the destination relative to
     * the 950 x 600 map
     * @param xCord x coordinate
     * @param yCord y coordinate
     */
    public void setCords(float xCord, float yCord) {
        this.xCord = xCord;
        this.yCord = yCord;
    }

    /**
     * This method can be used to add routes and neighbors to this
     * destination. It is necessary because it would be circular to initialize
     * Route and Destination objects without it.
     * @param route The route to add
     */
    public void addRoute(Route route) {
        routes.add(route);
        neighbors.add(route.getOther(this));
    }

    /**
     * This method can be used to add routes and neighbors to this
     * destination. It is necessary because it would be circular to initialize
     * Route and Destination objects without it.
     * @param routes Any routes to be added
     */
    public void addRoutes(Collection<Route> routes) {
        this.routes = new HashSet<>(routes);
        for (Route route : routes) {
            neighbors.add(route.getOther(this));
        }
    }

    /**
     * This method is for if there is a point where we need to remove
     * routes coming from a Destination
     * @param route The route to be removed
     */
    public void removeRoute(Route route) {
        routes.remove(route);
        neighbors.remove(route.getOther(this));
    }

    /**
     * This method is for if there is a point where we need to remove
     * routes coming from a Destination
     * @param routes The routes to be removed
     */
    public void removeRoutes(Collection<Route> routes) {
        this.routes.removeAll(routes);
        for (Route route : routes) {
            neighbors.remove(route.getOther(this));
        }
    }

    /**
     * This method checks if a specific route is in this destination's
     * set of routes.
     * @param route The route to check
     * @return Whether the route is present or not
     */
    public boolean hasRoute(Route route) {
        return routes.contains(route);
    }



    @Override
    public boolean equals(Object other) {
        return other != null &&
                (other instanceof Destination) &&
                ((Destination) other).locationName.equals(locationName);
    }

    @Override
    public int hashCode() {
        return locationName.hashCode();
    }
}
