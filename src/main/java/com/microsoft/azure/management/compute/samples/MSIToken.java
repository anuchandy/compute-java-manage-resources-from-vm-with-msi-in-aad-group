package com.microsoft.azure.management.compute.samples;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

class MSIToken {
    private static DateTime epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC);

    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "expires_on")
    private String expiresOn;

    String accessToken() {
        return accessToken;
    }

    String tokenType() {
        return tokenType;
    }

    boolean isExpired() {
        return true;
        // DateTime now = DateTime.now(DateTimeZone.UTC);
        // DateTime expireOn = epoch.plusSeconds(Integer.parseInt(this.expiresOn));
        // return now.plusMinutes(5).isAfter(expireOn.getMillis());
    }
}