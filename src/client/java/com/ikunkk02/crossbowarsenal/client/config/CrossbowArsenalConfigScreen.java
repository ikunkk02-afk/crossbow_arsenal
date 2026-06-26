package com.ikunkk02.crossbowarsenal.client.config;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class CrossbowArsenalConfigScreen {
	private CrossbowArsenalConfigScreen() {
	}

	public static Screen create(Screen parent) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable("config.crossbow_arsenal.title"))
				.setSavingRunnable(() -> {
					config.sanitize();
					CrossbowArsenalConfigManager.save();
				});
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory repeating = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.repeating"));
		ConfigCategory lockOn = builder.getOrCreateCategory(Text.translatable("config.crossbow_arsenal.category.lock_on"));

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
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.requireLineOfSight"), config.requireLineOfSight)
				.setDefaultValue(true)
				.setSaveConsumer(value -> config.requireLineOfSight = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.serverValidationFovDegrees"), config.serverValidationFovDegrees)
				.setDefaultValue(120.0D)
				.setMin(1.0D)
				.setMax(180.0D)
				.setSaveConsumer(value -> config.serverValidationFovDegrees = value)
				.build());
		lockOn.addEntry(entryBuilder.startIntSlider(Text.translatable("config.crossbow_arsenal.lockOnHomingTicks"), config.lockOnHomingTicks, 1, 200)
				.setDefaultValue(60)
				.setSaveConsumer(value -> config.lockOnHomingTicks = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.normalHomingStrength"), config.normalHomingStrength)
				.setDefaultValue(0.12D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.normalHomingStrength = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.undeadHomingStrength"), config.undeadHomingStrength)
				.setDefaultValue(0.16D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.undeadHomingStrength = value)
				.build());
		lockOn.addEntry(entryBuilder.startDoubleField(Text.translatable("config.crossbow_arsenal.bossHomingStrength"), config.bossHomingStrength)
				.setDefaultValue(0.08D)
				.setMin(0.0D)
				.setMax(1.0D)
				.setSaveConsumer(value -> config.bossHomingStrength = value)
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
		lockOn.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.crossbow_arsenal.showLockOnDebug"), config.showLockOnDebug)
				.setDefaultValue(false)
				.setSaveConsumer(value -> config.showLockOnDebug = value)
				.build());

		return builder.build();
	}
}
