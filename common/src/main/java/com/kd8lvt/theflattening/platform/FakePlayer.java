package com.kd8lvt.theflattening.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.NotImplementedException;

public class FakePlayer {
    public static ServerPlayerEntity THE_FLATTENER;
    @ExpectPlatform
    public static ServerPlayerEntity getFakePlayer(ServerWorld world) {
        throw new NotImplementedException();
    }
}
