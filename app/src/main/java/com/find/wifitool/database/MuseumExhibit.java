package com.find.wifitool.database;

import java.util.ArrayList;

/**
 * Created by markkeller on 10/26/17.
 */

public class MuseumExhibit {
    private int titleResourceID, subtitleResourceID, descriptionResourceID, imageResourceID;

    private ArrayList<MuseumWork> worksList = new ArrayList<MuseumWork>();

    public MuseumExhibit(int titleResourceID, int subtitleResourceID, int descriptionResourceID, int imageResourceID) {
        this.titleResourceID = titleResourceID;
        this.subtitleResourceID = subtitleResourceID;
        this.descriptionResourceID = descriptionResourceID;
        this.imageResourceID = imageResourceID;
    }

    public int getTitleResourceID() {
        return titleResourceID;
    }

    public void setTitleResourceID(int titleResourceID) {
        this.titleResourceID = titleResourceID;
    }

    public int getSubtitleResourceID() {
        return subtitleResourceID;
    }

    public void setSubtitleResourceID(int subtitleResourceID) {
        this.subtitleResourceID = subtitleResourceID;
    }

    public int getDescriptionResourceID() {
        return descriptionResourceID;
    }

    public void setDescriptionResourceID(int descriptionResourceID) {
        this.descriptionResourceID = descriptionResourceID;
    }

    public int getImageResourceID() {
        return imageResourceID;
    }

    public void setImageResourceID(int imageResourceID) {
        this.imageResourceID = imageResourceID;
    }

    public ArrayList<MuseumWork> getWorksList() {
        return worksList;
    }

    public void setWorksList(ArrayList<MuseumWork> worksList) {
        this.worksList = worksList;
    }

    public void addWork(MuseumWork newWork) {
        this.worksList.add(newWork);
    }



}
