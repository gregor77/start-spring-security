package com.rhyno.startsecurity.password;

public enum PasswordEncodeType {
    BCRYPT("bcrypt"), SHA256("SHA-256");

    private String type;

    PasswordEncodeType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
