package com.find.wifitool.database;

/**
 * Created by markkeller on 10/26/17.
 */

public class MuseumExhibit {
    private int titleResourceID, subtitleResourceID, descriptionResourceID, imageResourceID;

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
}
