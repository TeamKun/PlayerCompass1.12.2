package net.kunmc.lab.playercompass1_12_2.utils;

import org.bukkit.Location;

public class Utils {
    public static String convertWorldName(String worldName) {
        switch (worldName) {
            case "world":
                return "オーバーワールド";
            case "world_nether":
                return "ネザー";
            case "world_the_end":
                return "エンド";
            default:
                return worldName;
        }
    }

    public static double calcPlaneDistance(Location loc1, Location loc2) throws IllegalArgumentException {
        Location loc1c = loc1.clone();
        loc1c.setY(0);
        Location loc2c = loc2.clone();
        loc2c.setY(0);
        return loc1c.distance(loc2c);
    }
}
