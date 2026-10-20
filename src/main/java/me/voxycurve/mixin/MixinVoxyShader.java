package me.voxycurve.mixin;

import me.voxycurve.VoxyBridge;
import me.voxycurve.config.CurveConfig;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Uploads the curve parameters every time a Voxy shader program is bound.
 * Programs without a "voxyCurve" uniform (everything except the terrain shaders) are skipped.
 * Because it reads the config on every bind, changes in the menu apply instantly.
 */
@Pseudo
@Mixin(targets = "me.cortex.voxy.client.core.gl.shader.Shader")
public abstract class MixinVoxyShader {
    @Shadow @Final private int id;

    @Inject(method = "bind()V", at = @At("TAIL"))
    private void voxycurve$upload(CallbackInfo ci) {
        int loc = GL20C.glGetUniformLocation(this.id, "voxyCurve");
        if (loc < 0) return;

        CurveConfig cfg = CurveConfig.CONFIG;
        float edge = VoxyBridge.renderDistanceBlocks();
        // The curved band is curvePercent% of the player's actual Voxy render distance, measured inward from the edge
        float span = Math.max(1.0f, edge * (cfg.curvePercent / 100.0f));
        float start = edge - span;
        float drop = cfg.enabled ? cfg.dropBlocks : 0.0f;

        GL20C.glUniform4f(loc, start, 1.0f / span, drop, 0.0f);
    }
}
