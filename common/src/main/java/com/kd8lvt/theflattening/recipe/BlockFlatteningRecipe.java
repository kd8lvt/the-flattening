package com.kd8lvt.theflattening.recipe;

import com.kd8lvt.theflattening.TheFlattening;
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

public class BlockFlatteningRecipe implements Recipe<SingleStackRecipeInput> {
    public ItemStack input;
    public ItemStack output;
    public BlockFlatteningRecipe(ItemStack input, ItemStack output) {
        TheFlattening.debug("New FlatteningRecipe: %s -> %s".formatted(input.toString(), output.toString()));
        this.input = input;
        this.output = output;
    }
    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockFlatteningRecipe.Serializer.INSTANCE;
    }
    @Override
    public RecipeType<?> getType() {
        return BlockFlatteningRecipe.Type.INSTANCE;
    }
    @Override
    public boolean matches(SingleStackRecipeInput inventory, World world) {
        return input.getItem().equals(inventory.item().getItem());
    }

    @Override
    public boolean fits(int width, int height) {return false;}
    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {return ItemStack.EMPTY;}

    public static class Type implements RecipeType<BlockFlatteningRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "flattening";
    }

    public static class Serializer implements RecipeSerializer<BlockFlatteningRecipe> {
        public static Serializer INSTANCE = new Serializer();
        public static final String ID = "flattening";
        public Serializer() {}

        public static final MapCodec<BlockFlatteningRecipe> CODEC = RecordCodecBuilder.mapCodec((instance)-> instance.group(ItemStack.VALIDATED_CODEC.fieldOf("input").forGetter((BlockFlatteningRecipe recipe)-> recipe.input),ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter((BlockFlatteningRecipe recipe)-> recipe.output)).apply(instance, BlockFlatteningRecipe::new));
        public static final PacketCodec<RegistryByteBuf, BlockFlatteningRecipe> PACKET_CODEC = PacketCodec.ofStatic(BlockFlatteningRecipe.Serializer::write, BlockFlatteningRecipe.Serializer::read);


        @Override
        public MapCodec<BlockFlatteningRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BlockFlatteningRecipe> packetCodec() {
            return PACKET_CODEC;
        }
        public static BlockFlatteningRecipe read(RegistryByteBuf buf) {
            ItemStack input = ItemStack.PACKET_CODEC.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            return new BlockFlatteningRecipe(input,output);
        }
        public static void write(RegistryByteBuf buf, BlockFlatteningRecipe recipe) {
            ItemStack.PACKET_CODEC.encode(buf,recipe.input);
            ItemStack.PACKET_CODEC.encode(buf,recipe.output);
        }
    }
}
