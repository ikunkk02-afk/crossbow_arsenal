package com.ikunkk02.crossbowarsenal.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public final class OverpoweredWarningRenderer {
	private static final int RED = 0xFFFF5555;
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BACKGROUND = 0xB0200000;

	private OverpoweredWarningRenderer() {
	}

	public static void initialize() {
		HudRenderCallback.EVENT.register(OverpoweredWarningRenderer::render);
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		OverpoweredWarningState.Mode mode = OverpoweredWarningController.getMode();
		if (client.player == null || client.world == null || mode == OverpoweredWarningState.Mode.NONE) {
			return;
		}

		Text title = Text.translatable("hud.crossbow_arsenal.overpowered_enabled");
		Text detail = mode == OverpoweredWarningState.Mode.FULL
				? Text.translatable("hud.crossbow_arsenal.overpowered_detail")
				: Text.translatable("hud.crossbow_arsenal.overpowered_short");
		int centerX = context.getScaledWindowWidth() / 2;
		int y = Math.max(18, context.getScaledWindowHeight() / 6);
		int width = Math.max(client.textRenderer.getWidth(title), client.textRenderer.getWidth(detail)) + 20;
		context.fill(centerX - width / 2, y - 6, centerX + width / 2, y + 24, BACKGROUND);
		context.drawBorder(centerX - width / 2, y - 6, width, 30, RED);
		context.drawTextWithShadow(client.textRenderer, title, centerX - client.textRenderer.getWidth(title) / 2, y, RED);
		context.drawTextWithShadow(client.textRenderer, detail, centerX - client.textRenderer.getWidth(detail) / 2, y + 12, WHITE);
	}
}
