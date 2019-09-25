package com.matchstick.brightlife.models;

import com.squareup.moshi.Json;

public class VerificationStatus {

    @Json(name = "message")
    private String message;
    @Json(name = "verified")
    private Boolean verified;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

}