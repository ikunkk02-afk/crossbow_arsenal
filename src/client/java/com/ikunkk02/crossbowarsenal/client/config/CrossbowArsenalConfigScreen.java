package com.ikunkk02.crossbowarsenal.client.config;

import com.ikunkk02.crossbowarsenal.client.network.ClientTargetingNetworking;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;

public final class CrossbowArsenalConfigScreen {
	private CrossbowArsenalConfigScreen() {
	}

	public static Screen create(Screen parent) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		boolean initiallyEnabled = config.enableOverpoweredTargeting;
		boolean[] pendingOverpoweredEnabled = {initiallyEnabled};
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable("config.crossbow_arsenal.title"))
				.setSavingRunnable(() -> saveConfig(parent, config, initiallyEnabled, pendingOverpoweredEnabled[0]));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory repeating = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.repeating"));
		ConfigCategory lockOn = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.lock_on"));
		ConfigCategory overpowered = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.overpowered"));
		ConfigCategory penetration = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.penetration"));
		ConfigCategory specialArrows = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.special_arrows"));
		ConfigCategory explosion = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.explosion"));

		repeating.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableRepeatingCrossbow"), config.enableRepeatingCrossbow)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableRepeatingCrossbow = value)
				.build());
		repeating.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.repeatingDelayTicks"), config.repeatingDelayTicks, 1, 40)
				.setDefaultValue(4)
				.setSaveConsumer(value -> config.repeatingDelayTicks = value)
				.build());
		repeating.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.level1Shots"), config.level1Shots, 1, 20)
				.setDefaultValue(3)
				.setSaveConsumer(value -> config.level1Shots = value)
				.build());
		repeating.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.level2Shots"), config.level2Shots, 1, 20)
				.setDefaultValue(5)
				.setSaveConsumer(value -> config.level2Shots = value)
				.build());
		repeating.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.level3Shots"), config.level3Shots, 1, 20)
				.setDefaultValue(7)
				.setSaveConsumer(value -> config.level3Shots = value)
				.build());
		repeating.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.repeatingDamageMultiplier"), config.repeatingDamageMultiplier)
				.setDefaultValue(0.75D)
				.setMin(0.0D)
				.setMax(10.0D)
				.setSaveConsumer(value -> config.repeatingDamageMultiplier = value)
				.build());
		repeating.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.repeatingSpreadMultiplier"), config.repeatingSpreadMultiplier)
				.setDefaultValue(1.5D)
				.setMin(0.0D)
				.setMax(10.0D)
				.setSaveConsumer(value -> config.repeatingSpreadMultiplier = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableLockOnSight"), config.enableLockOnSight)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableLockOnSight = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableFullScreenLockOn"), config.enableFullScreenLockOn)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableFullScreenLockOn = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.lockOnMaxDistance"), config.lockOnMaxDistance)
				.setDefaultValue(32.0D)
				.setMin(1.0D)
				.setMax(128.0D)
				.setSaveConsumer(value -> config.lockOnMaxDistance = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.lockOnAngleDegrees"), config.lockOnAngleDegrees)
				.setDefaultValue(8.0D)
				.setMin(1.0D)
				.setMax(90.0D)
				.setSaveConsumer(value -> config.lockOnAngleDegrees = value)
				.build());
		lockOn.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.lockOnScreenMargin"), config.lockOnScreenMargin, 0, 200)
				.setDefaultValue(20)
				.setSaveConsumer(value -> config.lockOnScreenMargin = value)
				.build());
		lockOn.addEntry(entryBuilder.startTextDescription(Text.translatable("config.crossbow_arsenal.requireLineOfSight.legacy"))
				.setColor(0xFFAAAAAA)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.serverValidationFovDegrees"), config.serverValidationFovDegrees)
				.setDefaultValue(120.0D)
				.setMin(1.0D)
				.setMax(180.0D)
				.setSaveConsumer(value -> config.serverValidationFovDegrees = value)
				.build());
		lockOn.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.lockOnHomingTicks"), config.lockOnHomingTicks, 1, 200)
				.setDefaultValue(120)
				.setSaveConsumer(value -> config.lockOnHomingTicks = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.normalHomingStrength"), config.normalHomingStrength)
				.setDefaultValue(0.55D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.normalHomingStrength = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.undeadHomingStrength"), config.undeadHomingStrength)
				.setDefaultValue(0.70D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.undeadHomingStrength = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.bossHomingStrength"), config.bossHomingStrength)
				.setDefaultValue(0.28D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.bossHomingStrength = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.homingGravityCompensation"), config.homingGravityCompensation)
				.setDefaultValue(0.015D)
				.setMin(0.0D)
				.setMax(0.1D)
				.setSaveConsumer(value -> config.homingGravityCompensation = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableGuaranteedHomingHit"), config.enableGuaranteedHomingHit)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableGuaranteedHomingHit = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.terminalHomingRadius"), config.terminalHomingRadius)
				.setDefaultValue(4.0D)
				.setMin(0.1D)
				.setMax(128.0D)
				.setSaveConsumer(value -> config.terminalHomingRadius = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.terminalHomingStrength"), config.terminalHomingStrength)
				.setDefaultValue(0.9D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.terminalHomingStrength = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.homingHitboxExpansion"), config.homingHitboxExpansion)
				.setDefaultValue(0.75D)
				.setMin(0.0D)
				.setMax(4.0D)
				.setSaveConsumer(value -> config.homingHitboxExpansion = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.requireClearPathForGuaranteedHit"), config.requireClearPathForGuaranteedHit)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.requireClearPathForGuaranteedHit = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.repeatingHomingMultiplier"), config.repeatingHomingMultiplier)
				.setDefaultValue(0.8D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.repeatingHomingMultiplier = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.showLockOnHud"), config.showLockOnHud)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.showLockOnHud = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableStartupHud"), config.enableStartupHud)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableStartupHud = value)
				.build());
		lockOn.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.startupHudDurationTicks"), config.startupHudDurationTicks, 1, 200)
				.setDefaultValue(30)
				.setSaveConsumer(value -> config.startupHudDurationTicks = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.startupHudOpacity"), config.startupHudOpacity)
				.setDefaultValue(0.65D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.startupHudOpacity = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableStartupSound"), config.enableStartupSound)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableStartupSound = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.startupHudReplayOnSwitch"), config.startupHudReplayOnSwitch)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.startupHudReplayOnSwitch = value)
				.build());
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.showLockOnDebug"), config.showLockOnDebug)
				.setDefaultValue(false)
				.setSaveConsumer(value -> config.showLockOnDebug = value)
				.build());

		overpowered.addEntry(entryBuilder.startTextDescription(Text.translatable("config.crossbow_arsenal.overpowered.warning").formatted(Formatting.RED))
				.setColor(0xFFFF5555)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableOverpoweredTargeting"), config.enableOverpoweredTargeting)
				.setDefaultValue(false)
				.setTooltip(Text.translatable("config.crossbow_arsenal.overpowered.warning").formatted(Formatting.RED))
				.setSaveConsumer(value -> pendingOverpoweredEnabled[0] = value)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.allowTargetPlayers"), config.allowTargetPlayers)
				.setDefaultValue(false)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.allowTargetPlayers = value)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.allowTargetThroughWalls"), config.allowTargetThroughWalls)
				.setDefaultValue(false)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.allowTargetThroughWalls = value)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.allowHomingThroughWalls"), config.allowHomingThroughWalls)
				.setDefaultValue(false)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.allowHomingThroughWalls = value)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.allowTargetInvisibleEntities"), config.allowTargetInvisibleEntities)
				.setDefaultValue(false)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.allowTargetInvisibleEntities = value)
				.build());
		overpowered.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.overpoweredTargetMaxDistance"), config.overpoweredTargetMaxDistance)
				.setDefaultValue(64.0D)
				.setMin(1.0D)
				.setMax(128.0D)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.overpoweredTargetMaxDistance = value)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.overpoweredPenetrationBreaksStone"), config.overpoweredPenetrationBreaksStone)
				.setDefaultValue(true)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.overpoweredPenetrationBreaksStone = value)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.overpoweredPenetrationBreaksWood"), config.overpoweredPenetrationBreaksWood)
				.setDefaultValue(true)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.overpoweredPenetrationBreaksWood = value)
				.build());
		overpowered.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.maxOverpoweredBlocksPenetrated"), config.maxOverpoweredBlocksPenetrated, 0, 64)
				.setDefaultValue(8)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.maxOverpoweredBlocksPenetrated = value)
				.build());
		overpowered.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.overpoweredHardBlockSpeedMultiplier"), config.overpoweredHardBlockSpeedMultiplier)
				.setDefaultValue(0.75D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.overpoweredHardBlockSpeedMultiplier = value)
				.build());
		overpowered.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.overpoweredHardBlockDamageMultiplier"), config.overpoweredHardBlockDamageMultiplier)
				.setDefaultValue(0.75D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setTooltip(overpoweredTooltip())
				.setSaveConsumer(value -> config.overpoweredHardBlockDamageMultiplier = value)
				.build());
		overpowered.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.showOverpoweredWarning"), config.showOverpoweredWarning)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("config.crossbow_arsenal.showOverpoweredWarning.tooltip"))
				.setSaveConsumer(value -> config.showOverpoweredWarning = value)
				.build());
		penetration.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableGlassPenetration"), config.enableGlassPenetration)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableGlassPenetration = value)
				.build());
		penetration.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.glassPenetrationDamageMultiplier"), config.glassPenetrationDamageMultiplier)
				.setDefaultValue(0.9D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.glassPenetrationDamageMultiplier = value)
				.build());
		penetration.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.glassPenetrationSpeedMultiplier"), config.glassPenetrationSpeedMultiplier)
				.setDefaultValue(0.92D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.glassPenetrationSpeedMultiplier = value)
				.build());
		penetration.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.glassPenetrationConsumesDurability"), config.glassPenetrationConsumesDurability)
				.setDefaultValue(false)
				.setSaveConsumer(value -> config.glassPenetrationConsumesDurability = value)
				.build());
		penetration.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.fragileBlockPenetrationEnabled"), config.fragileBlockPenetrationEnabled)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.fragileBlockPenetrationEnabled = value)
				.build());
		penetration.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.fragileBlockDamageMultiplier"), config.fragileBlockDamageMultiplier)
				.setDefaultValue(0.8D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.fragileBlockDamageMultiplier = value)
				.build());
		penetration.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.fragileBlockSpeedMultiplier"), config.fragileBlockSpeedMultiplier)
				.setDefaultValue(0.85D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.fragileBlockSpeedMultiplier = value)
				.build());
		penetration.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.maxFragileBlocksPenetrated"), config.maxFragileBlocksPenetrated, 0, 64)
				.setDefaultValue(3)
				.setSaveConsumer(value -> config.maxFragileBlocksPenetrated = value)
				.build());
		penetration.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.entityPenetrationEnabled"), config.entityPenetrationEnabled)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.entityPenetrationEnabled = value)
				.build());
		penetration.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.entityPenetrationDamageDecay"), config.entityPenetrationDamageDecay)
				.setDefaultValue(0.8D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.entityPenetrationDamageDecay = value)
				.build());
		penetration.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.maxEntityPenetrations"), config.maxEntityPenetrations, 1, 64)
				.setDefaultValue(3)
				.setSaveConsumer(value -> config.maxEntityPenetrations = value)
				.build());
		penetration.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.lockOnArrowCanPenetrateGlass"), config.lockOnArrowCanPenetrateGlass)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.lockOnArrowCanPenetrateGlass = value)
				.build());
		penetration.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.lockOnArrowCanPenetrateFragileBlocks"), config.lockOnArrowCanPenetrateFragileBlocks)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.lockOnArrowCanPenetrateFragileBlocks = value)
				.build());
		penetration.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.showPenetrationDebug"), config.showPenetrationDebug)
				.setDefaultValue(false)
				.setSaveConsumer(value -> config.showPenetrationDebug = value)
				.build());
		specialArrows.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enablePenetratingArrow"), config.enablePenetratingArrow)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enablePenetratingArrow = value)
				.build());
		specialArrows.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.enableExplosiveArrow"), config.enableExplosiveArrow)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.enableExplosiveArrow = value)
				.build());
		specialArrows.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.penetratingArrowCanPenetrateFragileBlocks"), config.penetratingArrowCanPenetrateFragileBlocks)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.penetratingArrowCanPenetrateFragileBlocks = value)
				.build());
		specialArrows.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.penetratingArrowSoftBlockRequiresPiercingLevel"), config.penetratingArrowSoftBlockRequiresPiercingLevel, 0, 10)
				.setDefaultValue(2)
				.setSaveConsumer(value -> config.penetratingArrowSoftBlockRequiresPiercingLevel = value)
				.build());
		specialArrows.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.penetratingArrowWoodBlockRequiresPiercingLevel"), config.penetratingArrowWoodBlockRequiresPiercingLevel, 0, 10)
				.setDefaultValue(3)
				.setSaveConsumer(value -> config.penetratingArrowWoodBlockRequiresPiercingLevel = value)
				.build());
		specialArrows.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.penetratingArrowDamageMultiplierPerBlock"), config.penetratingArrowDamageMultiplierPerBlock)
				.setDefaultValue(0.85D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.penetratingArrowDamageMultiplierPerBlock = value)
				.build());
		specialArrows.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.penetratingArrowSpeedMultiplierPerBlock"), config.penetratingArrowSpeedMultiplierPerBlock)
				.setDefaultValue(0.9D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.penetratingArrowSpeedMultiplierPerBlock = value)
				.build());
		explosion.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.explosiveArrowBasePower"), config.explosiveArrowBasePower)
				.setDefaultValue(2.0D)
				.setMin(0.0D)
				.setMax(16.0D)
				.setSaveConsumer(value -> config.explosiveArrowBasePower = value)
				.build());
		explosion.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.explosiveEnchantLevel1Power"), config.explosiveEnchantLevel1Power)
				.setDefaultValue(1.5D)
				.setMin(0.0D)
				.setMax(16.0D)
				.setSaveConsumer(value -> config.explosiveEnchantLevel1Power = value)
				.build());
		explosion.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.explosiveEnchantLevel2Power"), config.explosiveEnchantLevel2Power)
				.setDefaultValue(2.2D)
				.setMin(0.0D)
				.setMax(16.0D)
				.setSaveConsumer(value -> config.explosiveEnchantLevel2Power = value)
				.build());
		explosion.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.explosiveEnchantLevel3Power"), config.explosiveEnchantLevel3Power)
				.setDefaultValue(3.0D)
				.setMin(0.0D)
				.setMax(16.0D)
				.setSaveConsumer(value -> config.explosiveEnchantLevel3Power = value)
				.build());
		explosion.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.maxExplosionPower"), config.maxExplosionPower)
				.setDefaultValue(4.0D)
				.setMin(0.0D)
				.setMax(16.0D)
				.setSaveConsumer(value -> config.maxExplosionPower = value)
				.build());
		explosion.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.explosiveArrowBreakBlocks"), config.explosiveArrowBreakBlocks)
				.setDefaultValue(false)
				.setSaveConsumer(value -> config.explosiveArrowBreakBlocks = value)
				.build());
		explosion.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.explosiveEnchantBreakBlocks"), config.explosiveEnchantBreakBlocks)
				.setDefaultValue(false)
				.setSaveConsumer(value -> config.explosiveEnchantBreakBlocks = value)
				.build());
		explosion.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.explosiveArrowFire"), config.explosiveArrowFire)
				.setDefaultValue(false)
				.setSaveConsumer(value -> config.explosiveArrowFire = value)
				.build());
		explosion.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.explosiveArrowKnockbackMultiplier"), config.explosiveArrowKnockbackMultiplier)
				.setDefaultValue(1.0D)
				.setMin(0.0D)
				.setMax(10.0D)
				.setSaveConsumer(value -> config.explosiveArrowKnockbackMultiplier = value)
				.build());
		explosion.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.explosiveArrowSelfDamageMultiplier"), config.explosiveArrowSelfDamageMultiplier)
				.setDefaultValue(0.35D)
				.setMin(0.0D)
				.setMax(10.0D)
				.setSaveConsumer(value -> config.explosiveArrowSelfDamageMultiplier = value)
				.build());
		explosion.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.explosiveStopsPenetration"), config.explosiveStopsPenetration)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.explosiveStopsPenetration = value)
				.build());

		return builder.build();
	}

	private static Text[] overpoweredTooltip() {
		return new Text[]{
				Text.translatable("config.crossbow_arsenal.overpowered.warning").formatted(Formatting.RED),
				Text.translatable("config.crossbow_arsenal.overpowered.requires_master")
		};
	}

	private static void saveConfig(Screen parent, CrossbowArsenalConfig config, boolean initiallyEnabled, boolean pendingEnabled) {
		if (!initiallyEnabled && pendingEnabled) {
			config.enableOverpoweredTargeting = false;
			config.sanitize();
			CrossbowArsenalConfigManager.save();
			MinecraftClient client = MinecraftClient.getInstance();
			client.send(() -> client.setScreen(new ConfirmScreen(
					confirmed -> {
						if (confirmed) {
							config.enableOverpoweredTargeting = true;
							config.sanitize();
							CrossbowArsenalConfigManager.save();
							ClientTargetingNetworking.requestPolicyRefresh();
						}
						client.setScreen(parent);
					},
					Text.translatable("config.crossbow_arsenal.overpowered.confirm.title"),
					Text.translatable("config.crossbow_arsenal.overpowered.confirm"),
					Text.translatable("config.crossbow_arsenal.overpowered.confirm_enable"),
					Text.translatable("gui.cancel")
			)));
			return;
		}

		config.enableOverpoweredTargeting = pendingEnabled;
		config.sanitize();
		CrossbowArsenalConfigManager.save();
		ClientTargetingNetworking.requestPolicyRefresh();
	}
}
