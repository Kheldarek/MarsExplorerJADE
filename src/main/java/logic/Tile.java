package logic;

import agents.Position;

public class Tile {

    private int gradient = 999;
    private boolean hasSamples;
    private boolean hasCrumbs;
    private int sampleCount;
    private int crumbCount;
    private boolean isObstacle = false;
    private boolean hasRover = false;
    private boolean hasProbe = false;
    private Position position;

    public boolean isObstacle() {
        return isObstacle;
    }

    public void setObstacle(boolean obstacle) {
        isObstacle = obstacle;
    }

    public boolean hasRover() {
        return hasRover;
    }

    public void setHasRover(boolean hasRover) {
        this.hasRover = hasRover;
    }

    public boolean hasProbe() {
        return hasProbe;
    }

    public void setHasProbe(boolean hasProbe) {
        this.hasProbe = hasProbe;
    }

    public int getGradient() {
        return gradient;
    }

    public void setGradient(int gradient) {
        this.gradient = gradient;
    }

    public boolean hasSamples() {
        return hasSamples;
    }

    public void setHasSamples(boolean hasSamples) {
        this.hasSamples = hasSamples;
    }

    public boolean hasCrumbs() {
        return hasCrumbs;
    }

    public void setHasCrumbs(boolean hasCrumbs) {
        this.hasCrumbs = hasCrumbs;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public int getCrumbCount() {
        return crumbCount;
    }

    public void setCrumbCount(int crumbCount) {
        this.crumbCount = crumbCount;
    }

    public boolean excavate() {
        if (sampleCount <= 0) {
            hasSamples = false;
        }
        if (hasSamples) {
            sampleCount--;
            return true;
        }
        return false;
    }

    public void dropCrumbs() {
        crumbCount += 2;
        if (crumbCount > 0) {
            hasCrumbs = true;
        }
    }

    public void pickUpCrumb() {
        crumbCount--;
        if (crumbCount <= 0) {
            hasCrumbs = false;
        }
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "gradient=" + gradient +
                ", hasSamples=" + hasSamples +
                ", hasCrumbs=" + hasCrumbs +
                ", sampleCount=" + sampleCount +
                ", crumbCount=" + crumbCount +
                ", isObstacle=" + isObstacle +
                ", hasRover=" + hasRover +
                ", hasProbe=" + hasProbe +
                ", position=" + position +
                '}';
    }
}
