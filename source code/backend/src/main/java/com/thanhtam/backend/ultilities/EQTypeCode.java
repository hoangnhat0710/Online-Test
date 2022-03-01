package com.thanhtam.backend.ultilities;

public enum EQTypeCode {
    /**
     * TF: true/false
     * MC: Multiple choice
     
     */

    MC("MC");
    private final String type;

    private EQTypeCode(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }


}
