package io.synthhex.highlander

import io.synthhex.highlander.recipe.ModRecipes
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object HighlanderClient : ClientModInitializer {
	val MOD_ID: String = "highlander";
	val logger: Logger = LoggerFactory.getLogger(MOD_ID);

	override fun onInitializeClient() {
		ModRecipes.registerRecipes();
		logger.info("Starting!");
	}
}