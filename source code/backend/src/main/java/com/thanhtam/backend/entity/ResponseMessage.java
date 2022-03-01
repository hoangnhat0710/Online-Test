package com.thanhtam.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ResponseMessage {
    private String message;

    private Object data;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public ResponseMessage(Object data) {
        this.data = data;
    }

    

    

}
