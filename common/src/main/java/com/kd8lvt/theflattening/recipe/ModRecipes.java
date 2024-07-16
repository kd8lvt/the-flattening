package com.kd8lvt.theflattening.recipe;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;

import static com.kd8lvt.theflattening.TheFlattening.*;

public class ModRecipes {
    public static BlockFlatteningRecipe.Type BLOCK_FLATTENING = BlockFlatteningRecipe.Type.INSTANCE;
    public static EntityFlatteningRecipe.Type ENTITY_FLATTENING = EntityFlatteningRecipe.Type.INSTANCE;
    public static RecipeManager.MatchGetter<SingleStackRecipeInput, BlockFlatteningRecipe> FLATTENING_RECIPES;
    public static RecipeManager.MatchGetter<EntityFlatteningRecipe.Input,EntityFlatteningRecipe> ENTITY_FLATTENING_RECIPES;
    public static void register() {
        //Register serializers
        RECIPE_SERIALIZER_REGISTRAR.register(id(BlockFlatteningRecipe.Serializer.ID),()-> BlockFlatteningRecipe.Serializer.INSTANCE);
        RECIPE_SERIALIZER_REGISTRAR.register(id(EntityFlatteningRecipe.Serializer.ID),()->EntityFlatteningRecipe.Serializer.INSTANCE);

        //Register types
        RECIPE_TYPE_REGISTRAR.register(id(BlockFlatteningRecipe.Type.ID),()-> BLOCK_FLATTENING);
        RECIPE_TYPE_REGISTRAR.register(id(EntityFlatteningRecipe.Type.ID),()->ENTITY_FLATTENING);

        //Create CachedMatchGetters
        FLATTENING_RECIPES = RecipeManager.createCachedMatchGetter(BLOCK_FLATTENING);
        ENTITY_FLATTENING_RECIPES = RecipeManager.createCachedMatchGetter(ENTITY_FLATTENING);
    }
}
