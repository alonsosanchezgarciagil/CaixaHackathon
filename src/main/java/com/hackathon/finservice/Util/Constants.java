package com.hackathon.finservice.Util;

public class Constants {

    private Constants() {
    }

    public static final String ACCOUNT_TYPE_BY_DEFAULT = "Main";

    public static final String[] WHITELIST_NOT_AUTH_ENDPOINTS = {
            "/api/users/register",
            "/api/users/login",
            "/health"
    };

    public static final String UNAUTHORIZED_RESPONSE = "Bad credentials";
}
