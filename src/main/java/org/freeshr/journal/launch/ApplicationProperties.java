package org.freeshr.journal.launch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static java.lang.System.getenv;

@Component
public class ApplicationProperties {

    public String getIdentityServerUrl(StringBuffer requestURL) {
        return getenv().get("IDENTITY_SERVER_URL") + "?redirectTo=" + requestURL;
    }
}
