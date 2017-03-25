package com.csc570.rsmith.boardgenerator.boardimportexport;

import java.util.ArrayList;

/**
 * Created by rsmith on 2/25/17.
 */
public class RouteJson {

    private String start;
    private String end;
    private int length;
    private int tracksAvailable;
    private ArrayList<String> colorsAllowed;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getTracksAvailable() {
        return tracksAvailable;
    }

    public void setTracksAvailable(int tracksAvailable) {
        this.tracksAvailable = tracksAvailable;
    }

    public ArrayList<String> getColorsAllowed() {
        return colorsAllowed;
    }

    public void setColorsAllowed(ArrayList<String> colorsAllowed) {
        this.colorsAllowed = colorsAllowed;
    }
}
