package com.kd8lvt.theflattening.recipe;

import net.minecraft.entity.Entity;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.RecipeInput;

public interface EntityRecipe<T extends EntityRecipe.EntityRecipeInput> extends Recipe<T> {
    interface EntityRecipeInput extends RecipeInput {
        Entity getEntityInSlot(int slot);
        boolean isEmpty();
    }
}
