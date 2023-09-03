package com.taggernation.skiesutils;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CameraHelper {


    public void setVecDirection(@NotNull Vector startingPoint, @NotNull Vector endingPoint, CameraVector vecDirection) {
        vecDirection.x = endingPoint.getX() - startingPoint.getX();
        vecDirection.y = endingPoint.getY() - startingPoint.getY();
        vecDirection.z = endingPoint.getZ() - startingPoint.getZ();

        double magnitude = Math.sqrt(Math.pow(vecDirection.x, 2) + Math.pow(vecDirection.y, 2) + Math.pow(vecDirection.z, 2));

        vecDirection.x /= magnitude;
        vecDirection.y /= magnitude;
        vecDirection.z /= magnitude;
    }
    public static int getID(Entity entity) {
        return entity.getEntityId();
    }
    public static class CameraVector extends org.bukkit.util.Vector {
        public double x, y, z;
    }
}
