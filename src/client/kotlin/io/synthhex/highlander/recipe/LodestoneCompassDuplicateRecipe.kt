package io.synthhex.highlander.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.synthhex.highlander.HighlanderClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.inventory.Inventory
import net.minecraft.item.CompassItem
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.SmithingRecipe
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class LodestoneCompassDuplicateRecipe(
        val template: Ingredient,
        val base: Ingredient,
        val addition: Ingredient,
        var result: ItemStack
) : SmithingRecipe {
    override fun matches(inventory: Inventory, world: World): Boolean {
        HighlanderClient.logger.info("Test if matches");
        if (!world.isClient()) // if it runs on the server it will crash
            return false;

        if (!template.test(inventory.getStack(0)) // test if template is a compass
                || !base.test(inventory.getStack(1)) // test if base is a compass
                || !addition.test(inventory.getStack(2))) // test if addition is redstone dust
            return false;

        val lode: ItemStack = inventory.getStack(0);

        HighlanderClient.logger.info(lode.components.toString());

        if (!lode.contains(DataComponentTypes.LODESTONE_TRACKER)) // check if compass is a lodestone compass
            return false;

        if(!lode.components.get(DataComponentTypes.LODESTONE_TRACKER)?.tracked()!!) // check if it tracks something
            return false;

        return true;
    }

    override fun fits(width: Int, height: Int): Boolean {
        return true;
    }

    override fun getIngredients(): DefaultedList<Ingredient> {
        val list: DefaultedList<Ingredient> = DefaultedList.ofSize(3);
        list.addAll(listOf(template, base, addition));
        return list;
    }

    override fun craft(inventory: Inventory, lookup: RegistryWrapper.WrapperLookup): ItemStack {
        HighlanderClient.logger.info("Crafted");
        val lode: ItemStack = inventory.getStack(0);
        val itemStack = lode.copyComponentsToNewStack(lode.item, lode.count * 2);
        itemStack.applyUnvalidatedChanges(lode.componentChanges);
        inventory.setStack(1, itemStack.copy());
        return itemStack;
    }

    override fun getResult(registriesLookup: RegistryWrapper.WrapperLookup?): ItemStack {
        HighlanderClient.logger.info("Got result");
        return result;
    }

    override fun getType(): RecipeType<*> {
        return Type.INSTANCE;
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return Serializer.INSTANCE;
    }

    override fun testTemplate(stack: ItemStack): Boolean {
        HighlanderClient.logger.info("Testing template");
        return template.test(stack)
                && stack.contains(DataComponentTypes.LODESTONE_TRACKER) // check if compass is a lodestone compass
                && stack.components.get(DataComponentTypes.LODESTONE_TRACKER)?.tracked()!!; // check if it tracks something
    }

    override fun testBase(stack: ItemStack): Boolean {
        HighlanderClient.logger.info("Testing base");
        return base.test(stack)
                && !stack.contains(DataComponentTypes.LODESTONE_TRACKER); // make sure compass is blank
    }

    override fun testAddition(stack: ItemStack): Boolean {
        HighlanderClient.logger.info("Testing addition");
        return addition.test(stack);
    }

    companion object {
        class Type : RecipeType<LodestoneCompassDuplicateRecipe> {
            companion object {
                val INSTANCE: Type = Type();
                val ID: String = "lodestone_compass_duplication"
            }
        }

        class Serializer : RecipeSerializer<LodestoneCompassDuplicateRecipe> {
            val CODEC: MapCodec<LodestoneCompassDuplicateRecipe> = RecordCodecBuilder.mapCodec({ instance: RecordCodecBuilder.Instance<LodestoneCompassDuplicateRecipe> ->
                instance.group(
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter { recipe: LodestoneCompassDuplicateRecipe -> recipe.template },
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter { recipe: LodestoneCompassDuplicateRecipe -> recipe.base },
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter { recipe: LodestoneCompassDuplicateRecipe -> recipe.addition },
                        ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter { recipe: LodestoneCompassDuplicateRecipe -> recipe.result }
                ).apply(instance, ::LodestoneCompassDuplicateRecipe);
            });

            val PACKET_CODEC: PacketCodec<RegistryByteBuf, LodestoneCompassDuplicateRecipe> = PacketCodec.ofStatic(Serializer::write, Serializer::read);


            override fun codec(): MapCodec<LodestoneCompassDuplicateRecipe> {
                return CODEC;
            }

            override fun packetCodec(): PacketCodec<RegistryByteBuf, LodestoneCompassDuplicateRecipe> {
                return PACKET_CODEC;
            }

            companion object {
                val INSTANCE: Serializer = Serializer();
                val ID: String = "lodestone_duplicating";

                @JvmStatic
                fun read(buf: RegistryByteBuf): LodestoneCompassDuplicateRecipe {
                    val ingredient = Ingredient.PACKET_CODEC.decode(buf) as Ingredient;
                    val ingredient2 = Ingredient.PACKET_CODEC.decode(buf) as Ingredient;
                    val ingredient3 = Ingredient.PACKET_CODEC.decode(buf) as Ingredient;
                    val itemStack = ItemStack.PACKET_CODEC.decode(buf) as ItemStack;
                    return LodestoneCompassDuplicateRecipe(ingredient, ingredient2, ingredient3, itemStack);
                }

                @JvmStatic
                fun write(buf: RegistryByteBuf, recipe: LodestoneCompassDuplicateRecipe) {
                    Ingredient.PACKET_CODEC.encode(buf, recipe.template)
                    Ingredient.PACKET_CODEC.encode(buf, recipe.base)
                    Ingredient.PACKET_CODEC.encode(buf, recipe.addition)
                    ItemStack.PACKET_CODEC.encode(buf, recipe.result)
                }
            }
        }
    }
}