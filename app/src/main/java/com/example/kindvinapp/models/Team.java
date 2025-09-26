package com.example.kindvinapp.models;

import java.io.Serializable;

public class Team implements Serializable {
    private String name;
    private int position;
    private int colorResId;
    private int pawnIconResId; // ID ресурса иконки

    public Team(String name, int colorResId, int pawnIconResId) {
        this.name = name;
        this.position = 1;
        this.colorResId = colorResId;
        this.pawnIconResId = pawnIconResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getColorResId() {
        return colorResId;
    }

    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }

    public int getPawnIconResId() {
        return pawnIconResId;
    }

    public void setPawnIconResId(int pawnIconResId) {
        this.pawnIconResId = pawnIconResId;
    }
}

