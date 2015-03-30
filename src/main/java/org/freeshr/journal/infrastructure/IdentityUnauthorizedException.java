package org.freeshr.journal.infrastructure;

import java.io.IOException;

public class IdentityUnauthorizedException extends IOException {
    private String message;

    public IdentityUnauthorizedException(String message) {
        super(message);
    }
}
