package com.example.echo.hospital.model;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by echo on 2017/10/16.
 */

public class Google {

    public static final String credentialWord = "credential";

    public Google(GoogleAccountCredential credential){
        setCredential(credential);
    }

    private static GoogleAccountCredential credential;

    public static GoogleAccountCredential getCredential() {
        return credential;
    }

    public static void setCredential(GoogleAccountCredential credential) {
        Google.credential = credential;
    }
}
