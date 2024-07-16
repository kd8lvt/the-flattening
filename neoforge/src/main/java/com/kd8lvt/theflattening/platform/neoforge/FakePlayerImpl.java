package com.kd8lvt.theflattening.platform.neoforge;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.Objects;
import java.util.UUID;

import static com.kd8lvt.theflattening.platform.FakePlayer.THE_FLATTENER;

public class FakePlayerImpl {
    public static ServerPlayerEntity getFakePlayer(ServerWorld world) {
        if (Objects.isNull(THE_FLATTENER)) THE_FLATTENER = new FakePlayer(world, new GameProfile(UUID.randomUUID(),"TheFlattener"));
        return THE_FLATTENER;
    }
}
