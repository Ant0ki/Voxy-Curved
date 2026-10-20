package me.voxycurve.config;

import me.voxycurve.VoxyCurveMod;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.option.*;
import net.caffeinemc.mods.sodium.api.config.structure.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Adds a "Voxy Curve" tab to Sodium's video settings.
 * Written against the same Sodium config API calls Voxy's own menu uses.
 */
public class CurveConfigMenu implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder B) {
        var cfg = CurveConfig.CONFIG;
        var mod = B.registerModOptions(VoxyCurveMod.ID, "Voxy Curve", VoxyCurveMod.VERSION);

        var enabled = B.createBooleanOption(Identifier.parse("voxycurve:enabled"));
        enabled.setName(Component.literal("Curved horizon"));
        enabled.setTooltip(Component.literal("Bends the farthest Voxy terrain downward like a planet's curvature."));
        enabled.setBinding(v -> cfg.enabled = v, () -> cfg.enabled);
        enabled.setStorageHandler(cfg::save);
        enabled.setDefaultValue(true);

        var curved = B.createIntegerOption(Identifier.parse("voxycurve:curve_percent"));
        curved.setName(Component.literal("Curved distance"));
        curved.setTooltip(Component.literal("Percent of your Voxy render distance that curves, counted from the far edge inward. 10% bends only the farthest tenth, 100% starts bending right at you."));
        curved.setBinding(v -> cfg.curvePercent = v, () -> cfg.curvePercent);
        curved.setStorageHandler(cfg::save);
        curved.setDefaultValue(10);
        curved.setRange(new Range(1, 100, 1));
        curved.setValueFormatter(v -> Component.literal(v + "%"));

        var drop = B.createIntegerOption(Identifier.parse("voxycurve:drop_blocks"));
        drop.setName(Component.literal("Edge drop"));
        drop.setTooltip(Component.literal("How many blocks the very edge of the render distance is lowered. Higher = stronger curvature."));
        drop.setBinding(v -> cfg.dropBlocks = v, () -> cfg.dropBlocks);
        drop.setStorageHandler(cfg::save);
        drop.setDefaultValue(300);
        drop.setRange(new Range(0, 2000, 10));
        drop.setValueFormatter(v -> Component.literal(v + " blocks"));

        var group = B.createOptionGroup();
        group.addOption(enabled);
        group.addOption(curved);
        group.addOption(drop);

        var page = B.createOptionPage();
        page.setName(Component.literal("Curvature"));
        page.addOptionGroup(group);

        mod.addPage(page);
    }
}
