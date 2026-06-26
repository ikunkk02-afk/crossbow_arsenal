package com.ikunkk02.crossbowarsenal.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class CrossbowArsenalModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return CrossbowArsenalConfigScreen::create;
	}
}
