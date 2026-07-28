package com.npg418.jadedvdrift.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.overlay.TooltipRenderer;

@Mixin(TooltipRenderer.class)
public class TooltipRendererMixin {
    @Shadow(remap = false)
    private Rect2i realRect;

    @Unique
    private static double jadeDVDrift$dvdX = -1;
    @Unique
    private static double jadeDVDrift$dvdY = -1;
    @Unique
    private static double jadeDVDrift$dvdVelX = 40.0;
    @Unique
    private static double jadeDVDrift$dvdVelY = 28.0;
    @Unique
    private static long jadeDVDrift$lastNanos = -1;

    @Inject(method = "recalculateRealRect", at = @At("RETURN"), remap = false)
    private void jade$dvdBounce(CallbackInfo ci) {
        if (realRect == null) return;

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

        jadeDVDrift$dvdX += jadeDVDrift$dvdVelX * dt;
        jadeDVDrift$dvdY += jadeDVDrift$dvdVelY * dt;

        if (jadeDVDrift$dvdX < 0) {
            jadeDVDrift$dvdX = 0;
            jadeDVDrift$dvdVelX = Math.abs(jadeDVDrift$dvdVelX);
        } else if (jadeDVDrift$dvdX + w > screenW) {
            jadeDVDrift$dvdX = screenW - w;
            jadeDVDrift$dvdVelX = -Math.abs(jadeDVDrift$dvdVelX);
        }

        if (jadeDVDrift$dvdY < 0) {
            jadeDVDrift$dvdY = 0;
            jadeDVDrift$dvdVelY = Math.abs(jadeDVDrift$dvdVelY);
        } else if (jadeDVDrift$dvdY + h > screenH) {
            jadeDVDrift$dvdY = screenH - h;
            jadeDVDrift$dvdVelY = -Math.abs(jadeDVDrift$dvdVelY);
        }

        realRect.setPosition((int) jadeDVDrift$dvdX, (int) jadeDVDrift$dvdY);
    }
}
