package me.voxycurve.mixin;

import me.voxycurve.VoxyCurveMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Patches Voxy's LOD terrain vertex shader after its #imports are resolved.
 * We add a curve function and apply it to the world-space point right before the MVP multiply.
 */
@Pseudo
@Mixin(targets = "me.cortex.voxy.client.core.gl.shader.ShaderLoader")
public abstract class MixinVoxyShaderLoader {
    private static final String TARGET = "voxy:lod/gl46/quads3.vert";
    private static final String FN_ANCHOR = "vec4 getQuadCornerPos(in QuadData quad, uint cornerId) {";
    private static final String POS_ANCHOR = "vec4 pos = MVP * vec4(point, 1.0f);";

    // x = curve start (blocks), y = 1/(span in blocks), z = drop (blocks), w = unused
    private static final String CURVE_GLSL = """
            uniform vec4 voxyCurve;
            vec3 voxycurve_apply(vec3 p) {
                if (voxyCurve.z <= 0.0) return p;
                // p and cameraSubPos are both relative to the camera's base section
                float d = length(p.xz - cameraSubPos.xz);
                float t = clamp((d - voxyCurve.x) * voxyCurve.y, 0.0, 1.0);
                p.y -= voxyCurve.z * t * t; // ease-in so there is no visible kink where it starts
                return p;
            }
            """;

    @Inject(method = "parse(Ljava/lang/String;)Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private static void voxycurve$patch(String id, CallbackInfoReturnable<String> cir) {
        if (!TARGET.equals(id)) return;
        String src = cir.getReturnValue();
        if (src == null || src.contains("voxyCurve")) return;
        if (!src.contains(FN_ANCHOR) || !src.contains(POS_ANCHOR)) {
            VoxyCurveMod.LOGGER.warn("Voxy's terrain shader changed, curve not applied (anchors missing)");
            return;
        }
        src = src.replace(FN_ANCHOR, CURVE_GLSL + FN_ANCHOR);
        src = src.replace(POS_ANCHOR, "point = voxycurve_apply(point);\n    " + POS_ANCHOR);
        cir.setReturnValue(src);
    }
}
