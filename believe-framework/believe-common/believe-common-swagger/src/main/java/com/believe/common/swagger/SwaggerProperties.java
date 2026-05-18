package com.believe.common.swagger;

import lombok.Data;

@Data
public class SwaggerProperties {

    private String title = "Believe API";
    private String description = "Believe Framework REST API";
    private String version = "1.0.0";
    private String contactName = "Believe Team";
    private String contactUrl = "https://believe.com";
    private String contactEmail = "team@believe.com";
    private String externalUrl = "https://believe.com/docs";
    private String externalDescription = "Believe Documentation";
}
