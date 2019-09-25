package com.matchstick.brightlife.models;

import com.squareup.moshi.Json;

public class Branch {
    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
