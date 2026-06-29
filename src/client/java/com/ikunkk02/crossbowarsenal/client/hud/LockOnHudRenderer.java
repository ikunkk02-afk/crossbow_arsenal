package com.ikunkk02.crossbowarsenal.client.hud;

import com.ikunkk02.crossbowarsenal.client.lockon.LockOnClientEvents;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnScreenProjection;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnStartupHudController;
import com.ikunkk02.crossbowarsenal.client.lockon.ClientTargetingPolicyState;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.util.LockOnTargeting;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public final class LockOnHudRenderer {
	private static final int NORMAL_COLOR = 0xFFE8F0FF;
	private static final int UNDEAD_COLOR = 0xFF9AB6FF;
	private static final int BOSS_COLOR = 0xFFFF4E4E;
	private static final int THROUGH_WALL_COLOR = 0xFFFF3030;
	private static final int DEBUG_COLOR = 0xFFE8F0FF;

	private LockOnHudRenderer() {
	}

	public static void initialize() {
		HudRenderCallback.EVENT.register(LockOnHudRenderer::render);
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.world == null) {
			return;
		}

		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		LivingEntity target = LockOnClientEvents.getCurrentTarget();
		float tickDelta = tickCounter.getTickDelta(false);
		if (config.showLockOnHud && target != null && !LockOnStartupHudController.isActive()) {
			renderLockBox(context, client, target, tickDelta);
		}
		if (config.showLockOnDebug) {
			renderDebug(context, client, target);
		}
	}

	private static void renderLockBox(DrawContext context, MinecraftClient client, LivingEntity target, float tickDelta) {
		LockOnScreenProjection.ScreenRect rect = LockOnScreenProjection.projectEntity(client, target, tickDelta);
		if (!LockOnScreenProjection.intersectsScreen(rect, client, 0)) {
			return;
		}

		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();
		int x1 = clamp((int) Math.floor(rect.minX()), 0, width);
		int y1 = clamp((int) Math.floor(rect.minY()), 0, height);
		int x2 = clamp((int) Math.ceil(rect.maxX()), 0, width);
		int y2 = clamp((int) Math.ceil(rect.maxY()), 0, height);
		if (x2 - x1 < 12) {
			int center = (x1 + x2) / 2;
			x1 = clamp(center - 6, 0, width);
			x2 = clamp(center + 6, 0, width);
		}
		if (y2 - y1 < 12) {
			int center = (y1 + y2) / 2;
			y1 = clamp(center - 6, 0, height);
			y2 = clamp(center + 6, 0, height);
		}

		boolean throughWall = LockOnTargeting.isThroughWall(client.player, target);
		int color = throughWall ? THROUGH_WALL_COLOR : getColor(target);
		int corner = Math.max(5, Math.min(18, Math.min(x2 - x1, y2 - y1) / 3));
		if (throughWall) {
			drawDashedBorder(context, x1, y1, x2, y2, color);
			Text label = Text.translatable("hud.crossbow_arsenal.through_wall");
			context.drawTextWithShadow(client.textRenderer, label, (x1 + x2 - client.textRenderer.getWidth(label)) / 2, Math.max(0, y1 - 11), color);
		} else {
			context.drawHorizontalLine(x1, x1 + corner, y1, color);
			context.drawHorizontalLine(x2 - corner, x2, y1, color);
			context.drawHorizontalLine(x1, x1 + corner, y2, color);
			context.drawHorizontalLine(x2 - corner, x2, y2, color);
			context.drawVerticalLine(x1, y1, y1 + corner, color);
			context.drawVerticalLine(x2, y1, y1 + corner, color);
			context.drawVerticalLine(x1, y2 - corner, y2, color);
			context.drawVerticalLine(x2, y2 - corner, y2, color);
		}
	}

	private static void renderDebug(DrawContext context, MinecraftClient client, LivingEntity target) {
		String name = target == null ? "none" : target.getName().getString();
		int id = target == null ? -1 : target.getId();
		boolean synced = LockOnClientEvents.isCurrentTargetSynced();
		context.drawTextWithShadow(client.textRenderer, "Holding lock-on crossbow: " + LockOnClientEvents.isHoldingSightCrossbow(), 6, 6, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Client target: " + name, 6, 16, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Client target id: " + id, 6, 26, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Target screen x/y: " + formatScreenCoordinate(LockOnClientEvents.getCurrentTargetScreenX()) + " / " + formatScreenCoordinate(LockOnClientEvents.getCurrentTargetScreenY()), 6, 36, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "LockTargetPacket sent: " + LockOnClientEvents.hasSentLockTargetPacket(), 6, 46, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Synced id: " + synced + " (" + LockOnClientEvents.getLastSentTargetId() + ")", 6, 56, DEBUG_COLOR);
		var policy = ClientTargetingPolicyState.getEffectivePolicy();
		context.drawTextWithShadow(client.textRenderer, "Overpowered targeting enabled: " + policy.enabled(), 6, 66, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Allow target players: " + policy.allowTargetPlayers(), 6, 76, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Allow target through walls: " + policy.allowTargetThroughWalls(), 6, 86, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Allow homing through walls: " + policy.allowHomingThroughWalls(), 6, 96, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Server accepted through-wall target: " + ClientTargetingPolicyState.wasLastAcceptedTargetThroughWall(), 6, 106, DEBUG_COLOR);
		context.drawTextWithShadow(client.textRenderer, "Server rejected reason: " + ClientTargetingPolicyState.getLastRejectedReason(), 6, 116, DEBUG_COLOR);
	}

	private static void drawDashedBorder(DrawContext context, int x1, int y1, int x2, int y2, int color) {
		for (int x = x1; x <= x2; x += 6) {
			context.drawHorizontalLine(x, Math.min(x + 3, x2), y1, color);
			context.drawHorizontalLine(x, Math.min(x + 3, x2), y2, color);
		}
		for (int y = y1; y <= y2; y += 6) {
			context.drawVerticalLine(x1, y, Math.min(y + 3, y2), color);
			context.drawVerticalLine(x2, y, Math.min(y + 3, y2), color);
		}
	}

	private static int getColor(LivingEntity target) {
		if (LockOnTargeting.isBoss(target)) {
			return BOSS_COLOR;
		}
		if (LockOnTargeting.isUndead(target)) {
			return UNDEAD_COLOR;
		}
		return NORMAL_COLOR;
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private static String formatScreenCoordinate(double value) {
		return Double.isNaN(value) ? "none" : Integer.toString((int) Math.round(value));
	}
}
