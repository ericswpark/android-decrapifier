package com.ericswpark.java.android.decrapifier;

public class AndroidPackage {
    private final String packageIdentifier;
    private final boolean forceDelete;
    private final String description;

    public AndroidPackage(String packageIdentifier, boolean forceDelete, String description) {
        this.packageIdentifier = packageIdentifier;
        this.forceDelete = forceDelete;
        this.description = description;
    }

    public String getPackageIdentifier() {
        return packageIdentifier;
    }

    public boolean isForceDelete() {
        return forceDelete;
    }

    public String getDescription() {
        return description;
    }
}
