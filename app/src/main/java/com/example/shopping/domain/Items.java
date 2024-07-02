package com.example.shopping.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Items implements Serializable {
    private int id;
    private String title;
    private String description;
    private String status;
    private ArrayList<String> picUrl;
    private String price;
    private int numberinCart;

    public Items() {
    }


    public Items(String title, String description, ArrayList<String> picUrl, String price, String status) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.price = price;
        this.status = status;
    }

    public Items(int id, String title, ArrayList<String> picUrl, String price, String status) {
        this.id = id;
        this.title = title;
        this.picUrl = picUrl;
        this.price = price;
        this.status = status;
    }

    public Items(String title, ArrayList<String> picUrl, String price, String status) {
        this.title = title;
        this.picUrl = picUrl;
        this.price = price;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getNumberinCart() {
        return numberinCart;
    }

    public void setNumberinCart(int numberinCart) {
        this.numberinCart = numberinCart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
