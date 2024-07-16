package com.kd8lvt.theflattening.recipe;

import com.kd8lvt.theflattening.TheFlattening;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityFlatteningRecipe implements EntityRecipe<EntityFlatteningRecipe.Input> {
    public EntityType<?> input;
    public ItemStack output;
    public boolean shouldDropEntityLoot;
    public boolean shouldDropXp;
    public float minimumDamage;
    public EntityFlatteningRecipe(Identifier input, ItemStack output, boolean shouldDropEntityLoot, boolean shouldDropXp, float minimumDamage) {
        TheFlattening.debug("New EntityFlatteningRecipe: %s -> %s (%s)".formatted(input.toString(), output.toString(), (shouldDropEntityLoot?"will drop loot":"will not drop loot")));
        this.input = Registries.ENTITY_TYPE.get(input);
        this.output = output;
        this.shouldDropEntityLoot = shouldDropEntityLoot;
        this.shouldDropXp = shouldDropXp;
        this.minimumDamage = minimumDamage;
    }
    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return EntityFlatteningRecipe.Serializer.INSTANCE;
    }
    @Override
    public RecipeType<?> getType() {
        return EntityFlatteningRecipe.Type.INSTANCE;
    }
    @Override
    public boolean matches(Input input, World world) {
        return input.getEntityInSlot(0).getType().equals(this.input);
    }
    @Override
    public boolean fits(int width, int height) {return true;}
    @Override
    public ItemStack craft(Input input, RegistryWrapper.WrapperLookup lookup) {return ItemStack.EMPTY;}

    public static class Type implements RecipeType<EntityFlatteningRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "entity_flattening";
    }

    public static class Serializer implements RecipeSerializer<EntityFlatteningRecipe> {
        public static Serializer INSTANCE = new Serializer();
        public static final String ID = "entity_flattening";
        public Serializer() {}

        public static final MapCodec<EntityFlatteningRecipe> CODEC = RecordCodecBuilder.mapCodec((instance)-> instance.group(
                Identifier.CODEC.fieldOf("input").forGetter((EntityFlatteningRecipe recipe)-> recipe.input.arch$registryName()),
                ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter((EntityFlatteningRecipe recipe)-> recipe.output),
                Codec.BOOL.fieldOf("shouldDropEntityLoot").orElse(false).forGetter((EntityFlatteningRecipe recipe)->recipe.shouldDropEntityLoot),
                Codec.BOOL.fieldOf("shouldDropXp").orElse(false).forGetter((EntityFlatteningRecipe recipe)->recipe.shouldDropXp),
                Codec.FLOAT.fieldOf("minimumDamage").orElse(0f).forGetter((EntityFlatteningRecipe recipe)->recipe.minimumDamage)
        ).apply(instance, EntityFlatteningRecipe::new));
        public static final PacketCodec<RegistryByteBuf, EntityFlatteningRecipe> PACKET_CODEC = PacketCodec.ofStatic(EntityFlatteningRecipe.Serializer::write, EntityFlatteningRecipe.Serializer::read);


        @Override
        public MapCodec<EntityFlatteningRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, EntityFlatteningRecipe> packetCodec() {
            return PACKET_CODEC;
        }
        public static EntityFlatteningRecipe read(RegistryByteBuf buf) {
            Identifier input = Identifier.PACKET_CODEC.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            boolean shouldDropLoot = PacketCodecs.BOOL.decode(buf);
            boolean shouldDropXp = PacketCodecs.BOOL.decode(buf);
            float minimumDamage = PacketCodecs.FLOAT.decode(buf);
            return new EntityFlatteningRecipe(input,output,shouldDropLoot,shouldDropXp,minimumDamage);
        }
        public static void write(RegistryByteBuf buf, EntityFlatteningRecipe recipe) {
            Identifier.PACKET_CODEC.encode(buf,recipe.input.arch$registryName());
            ItemStack.PACKET_CODEC.encode(buf,recipe.output);
            PacketCodecs.BOOL.encode(buf,recipe.shouldDropEntityLoot);
            PacketCodecs.BOOL.encode(buf,recipe.shouldDropXp);
            PacketCodecs.FLOAT.encode(buf,recipe.minimumDamage);
        }
    }

    public static class Input implements EntityRecipeInput {
        public Input(LivingEntity input) {entities = DefaultedList.ofSize(1,input);}
        public static DefaultedList<LivingEntity> entities = DefaultedList.ofSize(1);
        @Override
        public Entity getEntityInSlot(int slot) {
            return entities.get(slot);
        }

        @Override
        public boolean isEmpty() {
            AtomicBoolean found = new AtomicBoolean(false);
            for (LivingEntity entity : entities) {
                if (!Objects.isNull(entity)) {
                    found.set(true);
                    break;
                }
            }
            return !found.get();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {return ItemStack.EMPTY;}

        @Override
        public int getSize() {
            return entities.size();
        }
    }
}
