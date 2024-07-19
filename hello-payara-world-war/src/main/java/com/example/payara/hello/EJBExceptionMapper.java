package com.example.payara.hello;

import jakarta.ejb.EJBException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

/**
 * Maps {@link EJBException} thrown by e.g. EJB services and tries to unwrap and rethrow the exception.
 */
@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

    @Context
    private Providers providers;

    @Override
    public Response toResponse(final EJBException exception) {

        Throwable handledException = exception;

        try {
            unwrapEJBException(exception);
        } catch (Throwable e) {
            // Exception is unwrapped into something not EJBException related
            // Try to get the mapper for it
            handledException = e;
        }

        ExceptionMapper<Throwable> mapper = providers.getExceptionMapper((Class<Throwable>) handledException.getClass());
        return mapper.toResponse(handledException);
    }

    /**
     * Throws the first nested exception which isn't an {@link EJBException}.
     *
     * @param wrappingException the EJBException
     */
    public static void unwrapEJBException(final EJBException wrappingException) throws Throwable {
        Throwable pE = null;
        Throwable cE = wrappingException;
        while (cE != null && cE.getCause() != pE) {
            if (!(cE instanceof EJBException)) {
                throw cE;
            }
            pE = cE;
            cE = cE.getCause();
        }
        if (cE != null) {
            throw cE;
        }
    }
}
