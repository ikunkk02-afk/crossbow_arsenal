package com.ikunkk02.crossbowarsenal.client.hud;

import com.ikunkk02.crossbowarsenal.client.lockon.LockOnStartupHudController;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public final class StartupHudRenderer {
	private static final int CYAN_RGB = 0x38DDF5;

	private StartupHudRenderer() {
	}

	public static void initialize() {
		HudRenderCallback.EVENT.register(StartupHudRenderer::render);
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.world == null || !LockOnStartupHudController.isActive()) {
			return;
		}

		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		float renderTicks = LockOnStartupHudController.getTicks() + tickCounter.getTickDelta(false);
		StartupHudAnimation.Phase phase = StartupHudAnimation.phase((int) renderTicks, config.startupHudDurationTicks);
		float phaseProgress = StartupHudAnimation.phaseProgress(renderTicks, config.startupHudDurationTicks, phase);
		float flicker = 0.94F + 0.06F * (float) Math.sin(renderTicks * 1.9F);
		float fade = phase == StartupHudAnimation.Phase.READY ? 1.0F - phaseProgress * 0.78F : 1.0F;
		float alpha = (float) config.startupHudOpacity * flicker * fade;
		int color = withAlpha(CYAN_RGB, alpha);
		int faintColor = withAlpha(CYAN_RGB, alpha * 0.34F);

		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();
		int centerX = width / 2;
		int centerY = height / 2;

		switch (phase) {
			case BOOTING -> renderBooting(context, client, centerX, centerY, phaseProgress, color, faintColor);
			case ONLINE -> renderOnline(context, client, width, height, centerX, centerY, phaseProgress, color, faintColor);
			case READY -> renderReady(context, client, width, height, centerX, centerY, phaseProgress, color, faintColor);
		}
	}

	private static void renderBooting(DrawContext context, MinecraftClient client, int centerX, int centerY, float progress, int color, int faintColor) {
		int radius = 17 + Math.round(progress * 7.0F);
		drawRing(context, centerX, centerY, radius, faintColor, 0, 360);
		int sweep = Math.round(progress * 360.0F);
		drawRing(context, centerX, centerY, radius, color, sweep - 72, sweep);
		drawCenteredText(context, client, Text.translatable("hud.crossbow_arsenal.system_booting"), centerX, centerY + radius + 9, color);
	}

	private static void renderOnline(DrawContext context, MinecraftClient client, int width, int height, int centerX, int centerY, float progress, int color, int faintColor) {
		drawCornerFrames(context, width, height, color);
		int scanX = Math.round((width + 140.0F) * progress) - 70;
		for (int offset : new int[]{-38, 0, 38}) {
			context.fill(scanX - 52, centerY + offset, scanX + 52, centerY + offset + 1, faintColor);
			context.fill(scanX - 18, centerY + offset - 1, scanX + 18, centerY + offset, color);
		}
		drawRing(context, centerX, centerY, 24, faintColor, 0, 360);
		drawCenteredText(context, client, Text.translatable("hud.crossbow_arsenal.lock_on_sight_online"), centerX, centerY + 36, color);
	}

	private static void renderReady(DrawContext context, MinecraftClient client, int width, int height, int centerX, int centerY, float progress, int color, int faintColor) {
		drawCornerFrames(context, width, height, faintColor);
		int radius = 5 + Math.round(progress * 13.0F);
		drawRing(context, centerX, centerY, radius, color, 0, 360);
		int gap = radius + 3;
		int arm = 9 + Math.round(progress * 8.0F);
		context.drawHorizontalLine(centerX - gap - arm, centerX - gap, centerY, color);
		context.drawHorizontalLine(centerX + gap, centerX + gap + arm, centerY, color);
		context.drawVerticalLine(centerX, centerY - gap - arm, centerY - gap, color);
		context.drawVerticalLine(centerX, centerY + gap, centerY + gap + arm, color);
		context.fill(centerX, centerY, centerX + 1, centerY + 1, color);
		drawCenteredText(context, client, Text.translatable("hud.crossbow_arsenal.targeting_system_ready"), centerX, centerY + radius + 13, color);
	}

	private static void drawCornerFrames(DrawContext context, int width, int height, int color) {
		int marginX = Math.max(18, width / 18);
		int marginY = Math.max(16, height / 14);
		int horizontal = Math.max(28, width / 10);
		int vertical = Math.max(20, height / 10);
		context.drawHorizontalLine(marginX, marginX + horizontal, marginY, color);
		context.drawVerticalLine(marginX, marginY, marginY + vertical, color);
		context.drawHorizontalLine(width - marginX - horizontal, width - marginX, marginY, color);
		context.drawVerticalLine(width - marginX, marginY, marginY + vertical, color);
		context.drawHorizontalLine(marginX, marginX + horizontal, height - marginY, color);
		context.drawVerticalLine(marginX, height - marginY - vertical, height - marginY, color);
		context.drawHorizontalLine(width - marginX - horizontal, width - marginX, height - marginY, color);
		context.drawVerticalLine(width - marginX, height - marginY - vertical, height - marginY, color);
	}

	private static void drawRing(DrawContext context, int centerX, int centerY, int radius, int color, int startDegrees, int endDegrees) {
		for (int degrees = startDegrees; degrees <= endDegrees; degrees += 4) {
			double radians = Math.toRadians(degrees);
			int x = centerX + (int) Math.round(Math.cos(radians) * radius);
			int y = centerY + (int) Math.round(Math.sin(radians) * radius);
			context.fill(x, y, x + 1, y + 1, color);
		}
	}

	private static void drawCenteredText(DrawContext context, MinecraftClient client, Text text, int centerX, int y, int color) {
		context.drawTextWithShadow(client.textRenderer, text, centerX - client.textRenderer.getWidth(text) / 2, y, color);
	}

	private static int withAlpha(int rgb, float alpha) {
		int alphaByte = Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
		return alphaByte << 24 | rgb;
	}
}
