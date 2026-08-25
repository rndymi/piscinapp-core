package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "piscinapp.security.jwt")
public class JwtKeyProperties {

    private String keyStoreBase64;
    private String keyStorePassword;
    private String keyPassword;
    private String keyAlias;
    private String keyId;

    public String getKeyStoreBase64() {
        return keyStoreBase64;
    }

    public void setKeyStoreBase64(String keyStoreBase64) {
        this.keyStoreBase64 = keyStoreBase64;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
