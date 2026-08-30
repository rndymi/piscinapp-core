package com.rndymi.es.piscinapp.core.identity.application;

import java.util.Locale;

public final class UsernameNormalizer {

    private UsernameNormalizer() {
    }

    public static String normalize(
            String username
    ) {

        if (username == null) {
            return null;
        }

        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
