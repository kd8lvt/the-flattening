package com.kd8lvt.theflattening;

import com.google.common.base.Suppliers;
import com.kd8lvt.theflattening.recipe.BlockFlatteningRecipe;
import com.kd8lvt.theflattening.recipe.EntityFlatteningRecipe;
import com.kd8lvt.theflattening.recipe.ModRecipes;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.kd8lvt.theflattening.platform.FakePlayer.getFakePlayer;
import static com.kd8lvt.theflattening.recipe.ModRecipes.ENTITY_FLATTENING_RECIPES;
import static com.kd8lvt.theflattening.recipe.ModRecipes.FLATTENING_RECIPES;

@SuppressWarnings("deprecation")
public final class TheFlattening {
    public static final String MOD_ID = "the_flattening";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(()->RegistrarManager.get(MOD_ID));
    public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTRAR = MANAGER.get().get(Registries.RECIPE_SERIALIZER);
    public static final Registrar<RecipeType<?>> RECIPE_TYPE_REGISTRAR = MANAGER.get().get(Registries.RECIPE_TYPE);
    public static final ArrayList<LivingEntity> noLootEntities = new ArrayList<>();
    public static void init() {
        ModRecipes.register();

        BlockEvent.FALLING_LAND.register(TheFlattening::tryCraft);
        EntityEvent.LIVING_HURT.register(TheFlattening::tryCraftWithEntity);
    }

    public static Identifier id(String _id) {
        return Identifier.of(MOD_ID,_id);
    }

    public static void debug(String msg) {
        if (Platform.isDevelopmentEnvironment()) LOG.info(msg);
    }

    public static void log(String msg) {
        LOG.info(msg);
    }

    public static void tryCraft(World world, BlockPos pos, BlockState fallState, BlockState stateAtLandingPos, FallingBlockEntity entity) {
        if (world.isClient) return;
        if (!(entity.getBlockState().getBlock() instanceof AnvilBlock)) return;
        pos = pos.down();
        BlockState landOn = world.getBlockState(pos);
        ItemStack input = new ItemStack(landOn.getBlock());
        debug("Trying to craft using %s".formatted(input.toString()));
        Optional<RecipeEntry<BlockFlatteningRecipe>> match = FLATTENING_RECIPES.getFirstMatch(new SingleStackRecipeInput(input),world);
        debug("%s using %s".formatted((match.isEmpty()?"Did not find recipe":"Found recipe for %s".formatted(match.get().value().output.toString())),input.toString()));
        if (match.isEmpty()) return;

        ItemEntity item = new ItemEntity(world,pos.toCenterPos().getX(),pos.up().toCenterPos().getY(),pos.toCenterPos().getZ(),match.get().value().output);
        world.spawnEntity(item);
        world.breakBlock(pos,false);
    }

    private static EventResult tryCraftWithEntity(LivingEntity livingEntity, DamageSource damageSource, float v) {
        if (livingEntity.getWorld().isClient) return EventResult.pass(); //Don't run on the client
        if (!damageSource.isOf(DamageTypes.FALLING_ANVIL)) return EventResult.pass(); //Not a falling anvil, don't care

        debug("Trying to craft using %s".formatted(Objects.requireNonNull(livingEntity.getType().arch$registryName()).toString()));
        Optional<RecipeEntry<EntityFlatteningRecipe>> match = ENTITY_FLATTENING_RECIPES.getFirstMatch(new EntityFlatteningRecipe.Input(livingEntity),livingEntity.getWorld()); //Try to find a matching recipe
        debug("%s using %s".formatted((match.isPresent()?"Found recipe for %s".formatted(match.get().value().output.toString()):"Did not find recipe"), Objects.requireNonNull(livingEntity.getType().arch$registryName()).toString()));
        if (match.isEmpty()) return EventResult.pass(); //Didn't find a recipe

        EntityFlatteningRecipe recipe = match.get().value(); //Get the actual recipe instead of an Optional<RecipeEntry<EntityFlatteningRecipe>>
        if (v < recipe.minimumDamage) return EventResult.pass(); //Not enough damage

        ItemStack output = recipe.output; //Get the recipe output
        World world = livingEntity.getWorld(); //Get the world before we kill the entity
        BlockPos pos = livingEntity.getBlockPos(); //Get the position before we kill the entity
        int xpToDrop = 0;
        if (!recipe.shouldDropXp) livingEntity.disableExperienceDropping(); //Disable XP drops
        else {
            xpToDrop = livingEntity.getXpToDrop((ServerWorld) world, getFakePlayer((ServerWorld) world));
        }
        if (!recipe.shouldDropEntityLoot) noLootEntities.add(livingEntity);
        livingEntity.damage(getFakePlayer((ServerWorld) world).getDamageSources().outOfWorld(),Float.MAX_VALUE);
        world.spawnEntity(new ItemEntity(world,pos.toCenterPos().x,pos.up().toCenterPos().y,pos.toCenterPos().z,output));
        if (xpToDrop > 0) {
            world.spawnEntity(new ExperienceOrbEntity(world,pos.toCenterPos().x,pos.up().toCenterPos().y,pos.toCenterPos().z,xpToDrop));
        }
        return EventResult.pass();
    }
}
