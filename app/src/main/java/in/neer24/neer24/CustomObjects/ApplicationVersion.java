package in.neer24.neer24.CustomObjects;

public class ApplicationVersion {

    double applicationVersion;
    int isCompulsory;

    public ApplicationVersion(double applicationVersion, int isCompulsory) {
        this.applicationVersion = applicationVersion;
        this.isCompulsory = isCompulsory;
    }

    public double getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(double applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public int getIsCompulsory() {
        return isCompulsory;
    }

    public void setIsCompulsory(int isCompulsory) {
        this.isCompulsory = isCompulsory;
    }
}
