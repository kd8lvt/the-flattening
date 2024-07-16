package com.kd8lvt.theflattening.mixin;

import com.kd8lvt.theflattening.TheFlattening;
import net.minecraft.block.AnvilBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference") //No idea why but mixins freak my Idea out. This makes it take a chill pill.
@Mixin(value=AnvilBlock.class)
public class AnvilBlockMixin {
    @Inject(at=@At("HEAD"),method= "onDestroyedOnLanding(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/FallingBlockEntity;)V")
    private void the_flattening$onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity, CallbackInfo ci) {
        TheFlattening.debug("Destroyed on landing!");
        TheFlattening.tryCraft(world,pos.up(),fallingBlockEntity.getBlockState(),world.getBlockState(pos),fallingBlockEntity);
    }
}
