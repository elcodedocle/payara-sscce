package com.example.payara.hello;

/**
 * The generic error response.
 */
public class ErrorResponse {

    // Status code of the HTTP response
    private Integer code;
    // A human-readable description of the error
    private String message;

    public ErrorResponse() {
        // NOOP
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
