package com.example.planlekcji.ckziu_elektryk.client.replacements;

public enum ReplacementType {

    CLASSES("classes"),
    TEACHERS("teachers");

    private final String mode;

    ReplacementType(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
