package com.example.payara.hello;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(final ValidationException exception) {
        var errorResponse = new ErrorResponse();
        errorResponse.setCode(Response.Status.BAD_REQUEST.getStatusCode());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
