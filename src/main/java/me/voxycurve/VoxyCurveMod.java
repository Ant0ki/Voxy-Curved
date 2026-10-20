package me.voxycurve;

import me.voxycurve.config.CurveConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoxyCurveMod implements ClientModInitializer {
    public static final String ID = "voxycurve";
    public static final Logger LOGGER = LoggerFactory.getLogger("VoxyCurve");
    public static final String VERSION = FabricLoader.getInstance()
            .getModContainer(ID).map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("dev");

    @Override
    public void onInitializeClient() {
        CurveConfig.CONFIG.save(); // make sure the file exists with defaults
        LOGGER.info("Voxy Curve loaded (enabled={}, curved distance={}%, drop={} blocks)",
                CurveConfig.CONFIG.enabled, CurveConfig.CONFIG.curvePercent, CurveConfig.CONFIG.dropBlocks);
    }
}
