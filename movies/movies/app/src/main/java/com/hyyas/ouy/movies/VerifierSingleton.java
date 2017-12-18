package com.hyyas.ouy.movies;

import ABPassNew.Verifier.Verifier;

/**
 * Created by 41861 on 2017/6/28.
 */

public class VerifierSingleton {
    private static Verifier verifier;

    public static Verifier getVerifier() {
        return verifier;
    }

    public static void setVerifier(Verifier verifier) {
        VerifierSingleton.verifier = verifier;
    }


}
