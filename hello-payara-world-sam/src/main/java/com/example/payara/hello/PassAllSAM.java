package com.example.payara.hello;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.security.auth.message.callback.CallerPrincipalCallback;
import jakarta.security.auth.message.callback.GroupPrincipalCallback;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Server Auth Module.
 */
public class PassAllSAM implements ServerAuthModule {

    final Logger logger = Logger.getLogger(PassAllSAM.class.getName());
    CallbackHandler handler;

    /**
     * Initialize the ServerAuthModule.
     *
     * @param requestPolicy  Request MessagePolicy.
     * @param responsePolicy Response MessagePolicy.
     * @param handler        CallbackHandler.
     * @param options        Options map.
     * @throws AuthException When initialisation fails.
     */
    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map<String, Object> options) throws AuthException {
        this.handler = handler;
    }

    /**
     * @return The supported message types for this ServerAuthModule.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getSupportedMessageTypes() {
        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
    }

    /**
     * Validate the request supplied to this ServerAuthModule which are all requests including logout requests.
     * <p>
     * This method wraps around 'processValidateRequest()' to always write HTTP 401 to the response when AuthStatus is FAILURE.
     *
     * @param messageInfo    MessageInfo.
     * @param clientSubject  Client subject.
     * @param serviceSubject Service subject.
     * @throws AuthException When authentication fails.
     * @return The AuthStatus.
     */
    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        logger.log(Level.INFO, () -> "Validating request");
        String username = "PASS_ALL_USER";
        String[] roles = new String[] { "ADMINISTRATOR", "UNPRIVILEGED_USER" };
        if (this.handler == null) {
            throw new AuthException("No CallbackHandler found");
        }
        CallerPrincipalCallback caller = new CallerPrincipalCallback(clientSubject, username);
        GroupPrincipalCallback groups = new GroupPrincipalCallback(clientSubject, roles);
        try {
            this.handler.handle(new Callback[] { caller, groups });
        } catch (IOException | UnsupportedCallbackException e) {
            throw new AuthException(e);
        }
        return AuthStatus.SUCCESS;
    }

    /**
     * Secure response for this ServerAuthModule.
     *
     * @param messageInfo    Message info.
     * @param serviceSubject Service subject.
     * @throws AuthException When authentication fails.
     * @return Auth Status.
     */
    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        logger.log(Level.INFO, () -> "Calling secureResponse");
        return AuthStatus.SEND_SUCCESS;
    }

    /**
     * Clean subject for this ServerAuthModule which is called in the logout process.
     *
     * @param messageInfo Message info.
     * @param subject     The subject to logout.
     * @throws AuthException When authentication fails.
     */
    @Override
    public void cleanSubject(final MessageInfo messageInfo, final Subject subject) throws AuthException {
        final HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        logger.log(Level.FINE, () -> "Cleaning subject '" + request.getMethod() + "': " + getRequestedURL(request));
        if (subject == null) {
            return;
        }
        subject.getPrincipals().clear();
    }

    protected String getRequestedURL(HttpServletRequest request) {
        var requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        }

        return requestURL.append('?').append(queryString).toString();
    }

}
