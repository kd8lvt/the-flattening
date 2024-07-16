package com.kd8lvt.theflattening.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kd8lvt.theflattening.TheFlattening.noLootEntities;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(value= LivingEntity.class)
public class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Inject(at=@At("HEAD"),method="shouldDropLoot()Z",cancellable = true)
    public void the_flattening$shouldDropLoot(CallbackInfoReturnable<Boolean> ci) {
        if (noLootEntities.contains(this)) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }
    @Override
    @Unique
    public Text getStyledDisplayName() {
        return super.getStyledDisplayName();
    }

    @Override
    @Unique
    public boolean cannotBeSilenced() {
        return super.cannotBeSilenced();
    }

    @Override
    @Unique
    public void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    @Unique
    public void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    @Unique
    public void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
