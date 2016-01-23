package com.starwood.anglerslong;

import android.content.Context;

/**
 * Created by kennystreit on 3/7/15.
 */
public class SpeciesItem {

    private String name;
    private String scientificName;
    private String commonName;
    private String individualLimit;
    private String aggregateLimit;
    private String sizeLimit;
    private String season;
    private String records;

    private Context context;

    public SpeciesItem() {

    }

    public SpeciesItem(Context context) {
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getIndividualLimit() {
        return individualLimit;
    }

    public void setIndividualLimit(String individualLimit) {
        this.individualLimit = individualLimit;
    }

    public String getAggregateLimit() {
        return aggregateLimit;
    }

    public void setAggregateLimit(String aggregateLimit) {
        this.aggregateLimit = aggregateLimit;
    }

    public String getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(String sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getRecords() {
        return records;
    }

    public void setRecords(String records) {
        this.records = records;
    }
}
