package com.saxolab.openapi.auth;

public interface SaxoTokenProvider {

  String getAccessToken();

  boolean isTokenValid();
}
