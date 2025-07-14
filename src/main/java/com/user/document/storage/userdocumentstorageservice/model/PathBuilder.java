package com.user.document.storage.userdocumentstorageservice.model;

public class PathBuilder {
    private static final String SLASH = "/";
    private static StringBuilder path;

    PathBuilder() {
        this.path = new StringBuilder();
    }

    public static PathBuilder builder() {
        return new PathBuilder();
    }

    public PathBuilder join(String value) {
        if (path.length() != 0) {
            path.append(SLASH);
        }
        path.append(value);
        return this;
    }

    public String build() {
        return this.path.toString();

    }
}
