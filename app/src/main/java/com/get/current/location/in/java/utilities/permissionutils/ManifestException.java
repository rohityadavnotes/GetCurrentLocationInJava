package com.get.current.location.in.java.utilities.permissionutils;

public class ManifestException extends RuntimeException {

    ManifestException() {
        super("No permissions are registered in the manifest file");
    }

    ManifestException(String permission) {
        super(permission + ": Permissions are not registered in the manifest file");
    }
}