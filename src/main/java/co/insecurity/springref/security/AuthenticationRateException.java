package co.insecurity.springref.security;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationRateException extends AuthenticationException {

    private static final long serialVersionUID = 4171115949562002092L;

    public AuthenticationRateException(String message) {
        super(message);
    }
}