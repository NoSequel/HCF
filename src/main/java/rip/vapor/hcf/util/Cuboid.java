package rip.vapor.hcf.util;

import com.google.gson.JsonObject;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Data
public class Cuboid {

    private final String worldName;
    private final int maxX, maxY, maxZ, minX, minY, minZ;

    /**
     * the main constructor for a Cuboid
     *
     * @param location1 the first location of the cuboid
     * @param location2 the second location of the cuboid
     */
    public Cuboid(Location location1, Location location2) {
        if (location1.getWorld() != location2.getWorld()) {
            throw new IllegalArgumentException("world of location1 does not equal location2's world");
        }

        this.maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        this.maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        this.maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

        this.minX = Math.min(location1.getBlockX(), location2.getBlockX());
        this.minY = Math.min(location1.getBlockY(), location2.getBlockY());
        this.minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());

        this.worldName = location1.getWorld().getName();
    }

    /**
     * Load a cuboid from a JsonObject
     *
     * @param object the {@link JsonObject}
     */
    public Cuboid(JsonObject object) {
        this(
                new Location(Bukkit.getWorld(object.get("worldName").getAsString()), object.get("x1").getAsInt(), object.get("y1").getAsInt(), object.get("z1").getAsInt()),
                new Location(Bukkit.getWorld(object.get("worldName").getAsString()), object.get("x2").getAsInt(), object.get("y2").getAsInt(), object.get("z2").getAsInt())
        );
    }

    /**
     * Get a JsonObject from a Cuboid JsonObject
     *
     * @return the {@link JsonObject}
     */
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("worldName", this.worldName)
                .addProperty("x1", this.minX)
                .addProperty("x2", this.maxX)
                .addProperty("y1", this.minY)
                .addProperty("y2", this.maxY)
                .addProperty("z1", this.minZ)
                .addProperty("z2", this.maxZ).get();
    }

    /**
     * checks if location is in cuboid
     *
     * @param location the location to be checked
     * @return whether the location is in the cuboid
     */
    public boolean isLocationInCuboid(Location location) {
        return (minX <= location.getBlockX() && minZ <= location.getBlockZ()) && (maxX >= location.getBlockX() && maxZ >= location.getBlockZ());
    }

    /**
     * Get all chunks in a cuboid
     *
     * @return the chunks
     */
    public int getChunks() {
        return (this.maxX - minX) + (this.maxZ - minZ);
    }

    /**
     * Get the center of the {@link Cuboid}
     *
     * @return the center
     */
    public String getMinXYZ() {
        return String.join(", ", new String[]{
                "(" + minX,
                String.valueOf(minY),
                minZ + ")"
        });
    }

    /**
     * Get the XYZ string of a {@link Cuboid}
     *
     * @return the XYZ string
     */
    public String toXYZ() {
        return String.join(", ", new String[] {
                "(" + minX,
                String.valueOf(minY),
                minZ + ")",

                "(" + maxX,
                String.valueOf(maxY),
                maxZ + ")",
        });
    }

    public String toString() {
        return new Location(Bukkit.getWorld(worldName), minX, minY, minZ).toString() + ", " + new Location(Bukkit.getWorld(worldName), maxX, maxY, maxZ).toString();
    }

}