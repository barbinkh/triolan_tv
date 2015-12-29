package com.forwork.triolan.model;

/**
 * This is just a simple class for holding data that is used to render our
 * custom view
 */
public class CustomData {
    private String web;
    private String picture;
    private String stream;
    private int id;


    public CustomData(int id, String web, String picture, String stream) {
        this.picture = picture;
        this.web = web;
        this.stream = stream;
        this.id = id;
    }


    public String getWeb() {
        return web;
    }


    public void setWeb(String web) {
        this.web = web;
    }


    public String getPicture() {
        return picture;
    }


    public void setPicture(String picture) {
        this.picture = picture;
    }


    public String getStream() {
        return stream;
    }


    public void setStream(String stream) {
        this.stream = stream;
    }


    public int getID() {
        return id;
    }


    public void setID(int id) {
        this.id = id;
    }
}
