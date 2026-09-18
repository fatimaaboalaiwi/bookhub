package com.company.bookhub.integration.numverify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NumverifyResponse(

        Boolean valid,

        String number,

        @JsonProperty("local_format")
        String localFormat,

        @JsonProperty("international_format")
        String internationalFormat,

        @JsonProperty("country_prefix")
        String countryPrefix,

        @JsonProperty("country_code")
        String countryCode,

        @JsonProperty("country_name")
        String countryName,

        String location,

        String carrier,

        @JsonProperty("line_type")
        String lineType

) {
}