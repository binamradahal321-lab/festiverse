package com.festiverse.app.model;

import java.io.Serializable;

/** A Nepali festival with bilingual descriptions and a bundled illustration. */
public class Festival implements Serializable {

    private final String name;
    private final String nepaliName;
    private final String date;          // ISO "yyyy-MM-dd" (2026 main day)
    private final String bsDate;
    private final String category;
    private final String descriptionEn;
    private final String descriptionNp;
    private final String location;
    private final int imageRes;         // bundled drawable resource
    private final int colorHex;

    public Festival(String name, String nepaliName, String date, String bsDate,
                    String category, String descriptionEn, String descriptionNp,
                    String location, int imageRes, int colorHex) {
        this.name = name;
        this.nepaliName = nepaliName;
        this.date = date;
        this.bsDate = bsDate;
        this.category = category;
        this.descriptionEn = descriptionEn;
        this.descriptionNp = descriptionNp;
        this.location = location;
        this.imageRes = imageRes;
        this.colorHex = colorHex;
    }

    public String getName() { return name; }
    public String getNepaliName() { return nepaliName; }
    public String getDate() { return date; }
    public String getBsDate() { return bsDate; }
    public String getCategory() { return category; }
    public String getDescriptionEn() { return descriptionEn; }
    public String getDescriptionNp() { return descriptionNp; }
    public String getLocation() { return location; }
    public int getImageRes() { return imageRes; }
    public int getColorHex() { return colorHex; }
}
