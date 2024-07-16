package com.kd8lvt.theflattening;

import com.google.common.base.Suppliers;
import com.kd8lvt.theflattening.recipe.FlatteningRecipe;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public final class TheFlattening {
    public static final String MOD_ID = "the_flattening";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(()->RegistrarManager.get(MOD_ID));
    public static RecipeManager.MatchGetter<SingleStackRecipeInput,FlatteningRecipe> FLATTENING_RECIPES;
    private static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTRAR = MANAGER.get().get(Registries.RECIPE_SERIALIZER);
    private static final Registrar<RecipeType<?>> RECIPE_TYPE_REGISTRAR = MANAGER.get().get(Registries.RECIPE_TYPE);
    public static void init() {
        RECIPE_SERIALIZER_REGISTRAR.register(id(FlatteningRecipe.Serializer.ID),()->FlatteningRecipe.Serializer.INSTANCE);
        RECIPE_TYPE_REGISTRAR.register(id(FlatteningRecipe.Type.ID),()->FlatteningRecipe.Type.INSTANCE);
        FLATTENING_RECIPES = RecipeManager.createCachedMatchGetter(FlatteningRecipe.Type.INSTANCE);

        BlockEvent.FALLING_LAND.register(TheFlattening::tryCraft);
    }

    public static Identifier id(String _id) {
        return Identifier.of(MOD_ID,_id);
    }

    public static void tryCraft(World world, BlockPos pos, BlockState fallState, BlockState stateAtLandingPos, FallingBlockEntity entity) {
        if (world.isClient) return;
        if (!(entity.getBlockState().getBlock() instanceof AnvilBlock)) return;
        pos = pos.down();
        BlockState landOn = world.getBlockState(pos);
        ItemStack input = new ItemStack(landOn.getBlock());
        if (Platform.isDevelopmentEnvironment()) LOG.info("Trying to craft using %s".formatted(input.toString()));
        Optional<RecipeEntry<FlatteningRecipe>> match = FLATTENING_RECIPES.getFirstMatch(new SingleStackRecipeInput(input),world);
        if (Platform.isDevelopmentEnvironment()) LOG.info("%s using %s".formatted((match.isEmpty()?"Did not find recipe":"Found recipe for %s".formatted(match.get().value().output.toString())),input.toString()));
        if (match.isEmpty()) return;

        ItemEntity item = new ItemEntity(world,pos.toCenterPos().getX(),pos.up().toCenterPos().getY(),pos.toCenterPos().getZ(),match.get().value().output);
        world.spawnEntity(item);
        world.breakBlock(pos,false);
    }
}
