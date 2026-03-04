package com.saxolab.openapi.model.root;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        String ClientKey,
        String UserId,
        String Name,
        String Culture,
        String Language,
        String TimeZoneId
) {}
