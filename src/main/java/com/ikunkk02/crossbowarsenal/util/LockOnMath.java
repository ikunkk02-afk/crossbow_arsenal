package com.ikunkk02.crossbowarsenal.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public final class LockOnMath {
	private static final double MAX_PREDICTION_TIME = 1.2D;

	private LockOnMath() {
	}

	public static boolean isWithinForwardCone(double normalizedDot, double fullFovDegrees) {
		double fov = Math.max(1.0D, Math.min(180.0D, fullFovDegrees));
		return normalizedDot + 1.0E-9D >= Math.cos(Math.toRadians(fov * 0.5D));
	}

	public static double clampHomingStrength(double strength) {
		return Math.max(0.0D, Math.min(1.0D, strength));
	}

	public static Vec3d steerVelocity(Vec3d currentVelocity, Vec3d desiredDirection, double strength) {
		double speed = currentVelocity.length();
		if (speed <= 1.0E-6D || desiredDirection.lengthSquared() <= 1.0E-6D) {
			return currentVelocity;
		}

		Vec3d currentDirection = currentVelocity.normalize();
		Vec3d adjustedDirection = currentDirection.lerp(desiredDirection.normalize(), clampHomingStrength(strength));
		if (adjustedDirection.lengthSquared() <= 1.0E-6D) {
			return currentVelocity;
		}
		return adjustedDirection.normalize().multiply(speed);
	}

	public static Vec3d steerTerminalVelocity(Vec3d currentVelocity, Vec3d desiredDirection, double strength, double minimumSpeed) {
		if (desiredDirection.lengthSquared() <= 1.0E-6D) {
			return currentVelocity;
		}

		double safeMinimumSpeed = Double.isFinite(minimumSpeed) ? Math.max(0.0D, minimumSpeed) : 0.0D;
		double speed = Math.max(currentVelocity.length(), safeMinimumSpeed);
		if (speed <= 1.0E-6D) {
			return currentVelocity;
		}
		if (currentVelocity.lengthSquared() <= 1.0E-6D) {
			return desiredDirection.normalize().multiply(speed);
		}

		Vec3d velocityAtTerminalSpeed = currentVelocity.normalize().multiply(speed);
		return steerVelocity(velocityAtTerminalSpeed, desiredDirection, strength);
	}

	public static double calculatePredictionTime(double distance, double arrowSpeed) {
		if (!Double.isFinite(distance) || !Double.isFinite(arrowSpeed) || distance <= 0.0D || arrowSpeed <= 1.0E-6D) {
			return 0.0D;
		}
		return Math.min(distance / arrowSpeed, MAX_PREDICTION_TIME);
	}

	public static Vec3d predictAimPoint(Vec3d arrowPosition, Vec3d aimPoint, Vec3d targetVelocity, double arrowSpeed, double gravityCompensation) {
		double distance = arrowPosition.distanceTo(aimPoint);
		double predictionTime = calculatePredictionTime(distance, arrowSpeed);
		double compensation = Double.isFinite(gravityCompensation) ? Math.max(0.0D, gravityCompensation) : 0.0D;
		return aimPoint.add(targetVelocity.multiply(predictionTime)).add(0.0D, distance * compensation, 0.0D);
	}

	public static Optional<Vec3d> getBoxIntersection(Box box, Vec3d start, Vec3d end) {
		if (box.contains(start)) {
			return Optional.of(start);
		}
		Optional<Vec3d> intersection = box.raycast(start, end);
		if (intersection.isPresent()) {
			return intersection;
		}
		return box.contains(end) ? Optional.of(end) : Optional.empty();
	}

	public static double screenXFromNdc(double ndcX, int screenWidth) {
		return (ndcX + 1.0D) * 0.5D * screenWidth;
	}

	public static double screenYFromNdc(double ndcY, int screenHeight) {
		return (1.0D - ndcY) * 0.5D * screenHeight;
	}
}
