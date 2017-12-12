package com.find.wifitool.database;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by markkeller on 11/9/17.
 */

public class MuseumWork implements Comparable<MuseumWork> {
    private int workID;

    private String phillips_id, title, maker, date_made, culture, materials, credit_line, item_name, movement, century, lifespan, continent, gender, year;

    private Bitmap bmp = null;

    private double distance = 0.00;

    private ImageView iv = null;

    private double theta = 0.0;


    public MuseumWork(int workID) {
        this.workID = workID;
        Random rand = new Random();
        this.theta = rand.nextDouble() * 2*Math.PI;
    }

    public double getTheta() {
        return this.theta;
    }

    public int getWorkID() {
        return workID;
    }

    public void setWorkID(int workID) {
        this.workID = workID;
    }

    public void setPhillipsID(String phillips_id) {
        this.phillips_id = phillips_id;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setImageView(ImageView iv) {
        this.iv = iv;
    }

    public ImageView getImageView() {
        return this.iv;
    }



    public String getImageURL() {
        return "http://www.phillipscollection.org/willo/w/size3/" + this.phillips_id + "w.jpg";
    }

    @Override
    public int compareTo(@NonNull MuseumWork another) {
        if(another.getDistance() == 0.0) {
            return 1;
        } else {
            return (this.getDistance() - another.getDistance() > 0.0 ? 1 : -1);
        }
    }
}
