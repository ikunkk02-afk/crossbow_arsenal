package com.ikunkk02.crossbowarsenal.client.lockon;

import com.ikunkk02.crossbowarsenal.util.LockOnMath;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class LockOnScreenProjection {
	private static final double MIN_CLIP_W = 1.0E-5D;
	private static FrameState frameState;

	private LockOnScreenProjection() {
	}

	public static void initialize() {
		WorldRenderEvents.LAST.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || context.camera() == null) {
				frameState = null;
				return;
			}
			frameState = new FrameState(
					context.camera().getPos(),
					new Matrix4f(context.positionMatrix()),
					new Matrix4f(context.projectionMatrix())
			);
		});
	}

	public static ScreenRect projectEntity(MinecraftClient client, Entity entity, float tickDelta) {
		if (client.player == null || client.world == null) {
			return null;
		}

		FrameState matrices = frameState;
		if (matrices == null) {
			return null;
		}

		Window window = client.getWindow();
		Box box = getLerpedBox(entity, tickDelta);
		ScreenRect rect = null;
		for (Vec3d corner : corners(box)) {
			ScreenPoint point = projectPoint(corner, matrices, window.getScaledWidth(), window.getScaledHeight());
			if (point != null) {
				rect = rect == null ? new ScreenRect(point.x, point.y, point.x, point.y) : rect.include(point);
			}
		}
		return rect;
	}

	public static boolean intersectsScreen(ScreenRect rect, MinecraftClient client, int margin) {
		if (rect == null) {
			return false;
		}
		Window window = client.getWindow();
		return rect.maxX >= -margin
				&& rect.minX <= window.getScaledWidth() + margin
				&& rect.maxY >= -margin
				&& rect.minY <= window.getScaledHeight() + margin;
	}

	public static double squaredDistanceToScreenCenter(ScreenRect rect, MinecraftClient client) {
		double centerX = client.getWindow().getScaledWidth() * 0.5D;
		double centerY = client.getWindow().getScaledHeight() * 0.5D;
		double dx = rect.centerX() - centerX;
		double dy = rect.centerY() - centerY;
		return dx * dx + dy * dy;
	}

	private static Box getLerpedBox(Entity entity, float tickDelta) {
		Vec3d offset = entity.getLerpedPos(tickDelta).subtract(entity.getPos());
		return entity.getBoundingBox().offset(offset);
	}

	private static ScreenPoint projectPoint(Vec3d point, FrameState matrices, int width, int height) {
		Vec3d relative = point.subtract(matrices.cameraPos);
		Vector4f clip = new Vector4f((float) relative.x, (float) relative.y, (float) relative.z, 1.0F);
		matrices.positionMatrix.transform(clip);
		matrices.projectionMatrix.transform(clip);
		if (clip.w <= MIN_CLIP_W) {
			return null;
		}

		double ndcX = clip.x / clip.w;
		double ndcY = clip.y / clip.w;
		double screenX = LockOnMath.screenXFromNdc(ndcX, width);
		double screenY = LockOnMath.screenYFromNdc(ndcY, height);
		return new ScreenPoint(screenX, screenY);
	}

	private static Vec3d[] corners(Box box) {
		return new Vec3d[]{
				new Vec3d(box.minX, box.minY, box.minZ),
				new Vec3d(box.minX, box.minY, box.maxZ),
				new Vec3d(box.minX, box.maxY, box.minZ),
				new Vec3d(box.minX, box.maxY, box.maxZ),
				new Vec3d(box.maxX, box.minY, box.minZ),
				new Vec3d(box.maxX, box.minY, box.maxZ),
				new Vec3d(box.maxX, box.maxY, box.minZ),
				new Vec3d(box.maxX, box.maxY, box.maxZ)
		};
	}

	private record ScreenPoint(double x, double y) {
	}

	private record FrameState(Vec3d cameraPos, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
	}

	public record ScreenRect(double minX, double minY, double maxX, double maxY) {
		private ScreenRect include(ScreenPoint point) {
			return new ScreenRect(
					Math.min(minX, point.x),
					Math.min(minY, point.y),
					Math.max(maxX, point.x),
					Math.max(maxY, point.y)
			);
		}

		public double centerX() {
			return (minX + maxX) * 0.5D;
		}

		public double centerY() {
			return (minY + maxY) * 0.5D;
		}
	}
}
