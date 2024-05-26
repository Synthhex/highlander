package io.synthhex.highlander.recipe

import io.synthhex.highlander.Highlander
import io.synthhex.highlander.HighlanderClient
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

class ModRecipes {
    companion object {
        fun registerRecipes() {
            println("Registering!");
            Registry.register(Registries.RECIPE_SERIALIZER, Identifier(HighlanderClient.MOD_ID, LodestoneCompassDuplicateRecipe.Companion.Serializer.ID), LodestoneCompassDuplicateRecipe.Companion.Serializer.INSTANCE);
            Registry.register(Registries.RECIPE_TYPE, Identifier(HighlanderClient.MOD_ID, LodestoneCompassDuplicateRecipe.Companion.Type.ID), LodestoneCompassDuplicateRecipe.Companion.Type.INSTANCE);
        }
    }
}