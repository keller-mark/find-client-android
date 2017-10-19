package com.find.wifitool.database;

/**
 * Created by markkeller on 10/19/17.
 */

public class FloorLocation {

    private int _id;
    private String _locName;
    private int _floorIndex;
    private double _locX;
    private double _locY;
    private double _locRatio;

    // Empty constructor
    public FloorLocation(){

    }

    public FloorLocation(int id, String locName, int floorIndex, double locX, double locY, double locRatio){
        this._id = id;
        this._locName = locName;
        this._floorIndex = floorIndex;
        this._locX = locX;
        this._locY = locY;
        this._locRatio = locRatio;
    }

    public int getID() {
        return _id;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public String getLocName() {
        return _locName;
    }

    public void setLocName(String _locName) {
        this._locName = _locName;
    }

    public int getFloorIndex() {
        return _floorIndex;
    }

    public void setFloorIndex(int _floorIndex) {
        this._floorIndex = _floorIndex;
    }

    public double getLocX() {
        return _locX;
    }

    public void setLocX(double _locX) {
        this._locX = _locX;
    }

    public double getLocY() {
        return _locY;
    }

    public void setLocY(double _locY) {
        this._locY = _locY;
    }

    public double getLocRatio() {
        return _locRatio;
    }

    public void setLocRatio(double _locRatio) {
        this._locRatio = _locRatio;
    }
}
