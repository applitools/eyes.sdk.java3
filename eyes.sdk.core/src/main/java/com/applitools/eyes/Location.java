package com.applitools.eyes;

/**
 * A location in a two-dimensional plane.
 */
public final class Location implements Cloneable {
    private final int x;
    private final int y;

    public static final Location ZERO = new Location(0, 0);

    /**
     * Creates a Location instance.
     * @param x The X coordinate of this location.
     * @param y The Y coordinate of this location.
     */
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Location)) {
            return false;
        }

        Location other = (Location) obj;
        return (this.getX() == other.getX()) && (this.getY() == other.getY());
    }

    @Override
    public int hashCode() {
        return this.getX() + this.getY();
    }

    /**
     * Creates a location from another location instance.
     * @param other A location instance from which to create the location.
     */
    public Location(Location other) {
        if (other != null) {
            x = other.getX();
            y = other.getY();
        } else {
            x = 0;
            y = 0;
        }
    }

    /**
     * Get a location translated by the specified amount.
     * @param dx The amount to offset the x-coordinate.
     * @param dy The amount to offset the y-coordinate.
     * @return A location translated by the specified amount.
     */
    public Location offset(int dx, int dy) {
        return new Location(x + dx, y + dy);
    }

    /**
     * Get a location translated by the specified amount.
     * @param amount The amount to offset.
     * @return A location translated by the specified amount.
     */
    public Location offset(Location amount) {
        return offset(amount.getX(), amount.getY());
    }

    /**
     * Get a scaled location.
     * @param scaleRatio The ratio by which to scale the results.
     * @return A scaled copy of the current location.
     */
    public Location scale(double scaleRatio) {
        return new Location((int) Math.ceil(x * scaleRatio), (int) Math.ceil(y * scaleRatio));
    }

    /**
     * @return The X coordinate of this location.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The Y coordinate of this location.
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public String toStringForFilename() {
        return x + "_" + y;
    }
}
