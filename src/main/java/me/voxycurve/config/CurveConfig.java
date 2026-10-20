package me.voxycurve.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.voxycurve.VoxyCurveMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CurveConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final CurveConfig CONFIG = load();

    public boolean enabled = true;
    /**
     * Share of Voxy's render distance that curves, counted from the far edge inward.
     * 10 = only the farthest 10% curves, 100 = the curve starts right at the player.
     */
    public int curvePercent = 10;
    /** How many blocks the very edge of the render distance is dropped. */
    public int dropBlocks = 300;

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("voxycurve.json");
    }

    private static CurveConfig load() {
        Path p = path();
        if (Files.exists(p)) {
            try {
                CurveConfig c = GSON.fromJson(Files.readString(p), CurveConfig.class);
                if (c != null) return c.sanitized();
            } catch (Exception e) {
                VoxyCurveMod.LOGGER.error("Bad voxycurve.json, using defaults", e);
            }
        }
        return new CurveConfig();
    }

    private CurveConfig sanitized() {
        this.curvePercent = Math.max(1, Math.min(100, this.curvePercent));
        this.dropBlocks = Math.max(0, Math.min(2000, this.dropBlocks));
        return this;
    }

    public void save() {
        try {
            Files.writeString(path(), GSON.toJson(this.sanitized()));
        } catch (IOException e) {
            VoxyCurveMod.LOGGER.error("Failed to save voxycurve.json", e);
        }
    }
}
