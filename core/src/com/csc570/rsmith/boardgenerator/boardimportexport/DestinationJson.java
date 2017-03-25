package com.csc570.rsmith.boardgenerator.boardimportexport;

import java.util.ArrayList;

/**
 * Created by rsmith on 2/25/17.
 */
public class DestinationJson {

    private String name;
    private ArrayList<RouteJson> routes;
    private float xCord;
    private float yCord;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<RouteJson> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<RouteJson> routes) {
        this.routes = routes;
    }

    public void setxCord(float xCord) {
        this.xCord = xCord;
    }

    public void setyCord(float yCord) {
        this.yCord = yCord;
    }

    public float getXCord() {
        return xCord;
    }

    public float getYCord() {
        return yCord;
    }
}
