package com.npg418.jadedvdrift.mixin;

import com.npg418.jadedvdrift.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.theme.Theme;
import snownee.jade.overlay.TooltipRenderer;

@Mixin(TooltipRenderer.class)
public class TooltipRendererMixin {

    @Shadow(remap = false)
    private Rect2i realRect;

    @Unique
    private static final double BASE_VEL_X = 40.0;
    @Unique
    private static final double BASE_VEL_Y = 28.0;

    @Unique
    private static double jadeDVDrift$dvdX = -1;
    @Unique
    private static double jadeDVDrift$dvdY = -1;
    @Unique
    private static double jadeDVDrift$dirX = 1.0;
    @Unique
    private static double jadeDVDrift$dirY = 1.0;
    @Unique
    private static long jadeDVDrift$lastNanos = -1;

    /* color-cycling state */
    @Unique
    private static float jadeDVDrift$hue = 0.0f;

    @Inject(method = "recalculateRealRect", at = @At("RETURN"), remap = false)
    private void jade$dvdBounce(CallbackInfo ci) {
        if (realRect == null || !Config.ENABLE.get()) return;

        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        int w = realRect.getWidth();
        int h = realRect.getHeight();

        if (screenW <= w || screenH <= h) return;

        long now = System.nanoTime();
        if (jadeDVDrift$lastNanos < 0) {
            jadeDVDrift$dvdX = realRect.getX();
            jadeDVDrift$dvdY = realRect.getY();
            jadeDVDrift$lastNanos = now;
        }
        double dt = (now - jadeDVDrift$lastNanos) / 1_000_000_000.0;
        dt = Math.min(dt, 0.1);
        jadeDVDrift$lastNanos = now;

        double amp = Config.SPEED_AMPLIFIER.get();
        double velX = BASE_VEL_X * amp;
        double velY = BASE_VEL_Y * amp;

        jadeDVDrift$dvdX += velX * jadeDVDrift$dirX * dt;
        jadeDVDrift$dvdY += velY * jadeDVDrift$dirY * dt;

        if (jadeDVDrift$dvdX < 0) {
            jadeDVDrift$dvdX = 0;
            jadeDVDrift$dirX = 1.0;
            jadeDVDrift$applyBounceColor();
        } else if (jadeDVDrift$dvdX + w > screenW) {
            jadeDVDrift$dvdX = screenW - w;
            jadeDVDrift$dirX = -1.0;
            jadeDVDrift$applyBounceColor();
        }

        if (jadeDVDrift$dvdY < 0) {
            jadeDVDrift$dvdY = 0;
            jadeDVDrift$dirY = 1.0;
            jadeDVDrift$applyBounceColor();
        } else if (jadeDVDrift$dvdY + h > screenH) {
            jadeDVDrift$dvdY = screenH - h;
            jadeDVDrift$dirY = -1.0;
            jadeDVDrift$applyBounceColor();
        }

        realRect.setPosition((int) jadeDVDrift$dvdX, (int) jadeDVDrift$dvdY);
    }

    @Unique
    private static void jadeDVDrift$applyBounceColor() {
        if (!Config.COLOR_ENABLE.get()) return;
        double step = Config.HUE_STEP.get();
        if (step == 0.0) return;
        jadeDVDrift$hue = (jadeDVDrift$hue + (float) step) % 1.0f;
        int color = java.awt.Color.HSBtoRGB(jadeDVDrift$hue, 0.85f, 1.0f) | 0xFF000000;
        float bgHue = (jadeDVDrift$hue + 0.5f) % 1.0f;
        int bgColor = java.awt.Color.HSBtoRGB(bgHue, 0.85f, 1.0f) | 0xFF000000;
        Theme theme = IThemeHelper.get().theme();
        theme.titleColor = color;
        theme.normalColor = color;
        theme.infoColor = color;
        theme.successColor = color;
        theme.warningColor = color;
        theme.dangerColor = color;
        theme.failureColor = color;
        theme.backgroundColor = bgColor;
        theme.borderColor[0] = color;
        theme.borderColor[1] = color;
        theme.borderColor[2] = color;
        theme.borderColor[3] = color;
        theme.boxBorderColor = color;
    }
}
