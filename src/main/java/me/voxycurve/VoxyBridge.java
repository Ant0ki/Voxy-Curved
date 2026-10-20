package me.voxycurve;

import java.lang.reflect.Field;

/** Reads Voxy's render distance by reflection so we never link against Voxy's code. */
public final class VoxyBridge {
    // Voxy: sectionRenderDistance * 16 * 32 = render distance in blocks (see its occlusion traverser / fog code)
    private static final float BLOCKS_PER_UNIT = 16f * 32f;
    private static final float FALLBACK_BLOCKS = 16f * BLOCKS_PER_UNIT; // 512 chunk setting

    private static Field configField;
    private static Field distanceField;
    private static boolean failed;

    private VoxyBridge() {}

    public static float renderDistanceBlocks() {
        if (failed) return FALLBACK_BLOCKS;
        try {
            if (distanceField == null) {
                Class<?> cls = Class.forName("me.cortex.voxy.client.config.VoxyConfig");
                configField = cls.getField("CONFIG");
                distanceField = cls.getField("sectionRenderDistance");
            }
            return distanceField.getFloat(configField.get(null)) * BLOCKS_PER_UNIT;
        } catch (Throwable t) {
            failed = true;
            VoxyCurveMod.LOGGER.warn("Could not read Voxy's render distance, assuming 512 chunks", t);
            return FALLBACK_BLOCKS;
        }
    }
}
