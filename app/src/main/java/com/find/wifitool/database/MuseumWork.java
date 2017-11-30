package com.find.wifitool.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;

/**
 * Created by markkeller on 11/9/17.
 */

public class MuseumWork {
    private int workID;

    private String phillips_id, title, maker, date_made, culture, materials, credit_line, item_name, movement, century, lifespan, continent, gender, year;

    private Bitmap bmp = null;


    public MuseumWork(int workID) {
        this.workID = workID;
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

    public Bitmap loadWorkImage() {

        if(this.bmp == null && this.phillips_id != null) {
            try {
                URL url = new URL(MuseumWork.generateImageURL(this.phillips_id));
                this.bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.bmp;
    }

    private static String generateImageURL(String phillips_id) {
        return "http://www.phillipscollection.org/willo/w/size3/" + phillips_id + "w.jpg";
    }
}
