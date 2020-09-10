package com.applitools.eyes.visualgrid.model;

import java.util.Objects;

public class HashObject {
    private final String hashFormat;
    private final String hash;

    public HashObject(String hashFormat, String hash) {
        this.hashFormat = hashFormat;
        this.hash = hash;
    }

    public String getHashFormat() {
        return hashFormat;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HashObject that = (HashObject) o;
        return hashFormat.equals(that.hashFormat) &&
                hash.equals(that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashFormat, hash);
    }
}
