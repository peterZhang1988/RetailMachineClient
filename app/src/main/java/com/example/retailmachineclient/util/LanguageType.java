package com.example.retailmachineclient.util;

public enum LanguageType  {
    CHINESE("ch"),
    ENGLISH("en"),
    FRENCH("fr_BE");

    private String language;

    LanguageType(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language == null ? "" : language;
    }
}
