package com.kd8lvt.theflattening.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import static com.kd8lvt.theflattening.TheFlattening.LOG;

public class FlatteningRecipe implements Recipe<SingleStackRecipeInput> {
    public ItemStack input;
    public ItemStack output;
    public FlatteningRecipe(ItemStack input, ItemStack output) {
        LOG.info("New FlatteningRecipe: %s -> %s".formatted(input.toString(),output.toString()));
        this.input = input;
        this.output = output;
    }
    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return FlatteningRecipe.Serializer.INSTANCE;
    }
    @Override
    public RecipeType<?> getType() {
        return FlatteningRecipe.Type.INSTANCE;
    }
    @Override
    public boolean matches(SingleStackRecipeInput inventory, World world) {
        return input.getItem().equals(inventory.item().getItem());
    }

    @Override
    public boolean fits(int width, int height) {return false;}
    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {return ItemStack.EMPTY;}

    public static class Type implements RecipeType<FlatteningRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "flattening";
    }

    public static class Serializer implements RecipeSerializer<FlatteningRecipe> {
        public static Serializer INSTANCE = new Serializer();
        public static final String ID = "flattening";
        public Serializer() {}

        public static final MapCodec<FlatteningRecipe> CODEC = RecordCodecBuilder.mapCodec((instance)-> instance.group(ItemStack.VALIDATED_CODEC.fieldOf("input").forGetter((FlatteningRecipe recipe)-> recipe.input),ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter((FlatteningRecipe recipe)-> recipe.output)).apply(instance,FlatteningRecipe::new));
        public static final PacketCodec<RegistryByteBuf, FlatteningRecipe> PACKET_CODEC = PacketCodec.ofStatic(FlatteningRecipe.Serializer::write, FlatteningRecipe.Serializer::read);


        @Override
        public MapCodec<FlatteningRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FlatteningRecipe> packetCodec() {
            return PACKET_CODEC;
        }
        public static FlatteningRecipe read(RegistryByteBuf buf) {
            ItemStack input = ItemStack.PACKET_CODEC.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            return new FlatteningRecipe(input,output);
        }
        public static void write(RegistryByteBuf buf, FlatteningRecipe recipe) {
            ItemStack.PACKET_CODEC.encode(buf,recipe.input);
            ItemStack.PACKET_CODEC.encode(buf,recipe.output);
        }
    }
}
