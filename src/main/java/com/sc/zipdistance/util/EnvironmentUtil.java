package com.sc.zipdistance.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtil {

    @Autowired
    private Environment env;

    public boolean isDevEnv() {
        for (String profile : env.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("dev")) {
                return true;
            }
        }
        return false;
    }

    public boolean isProdEnv() {
        for (String profile : env.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("prod")) {
                return true;
            }
        }
        return false;
    }
}
