package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Account(
        String AccountId,
        String AccountKey,
        String ClientKey,
        String AccountType,
        String Currency,
        boolean Active,
        String DisplayName
) {}
