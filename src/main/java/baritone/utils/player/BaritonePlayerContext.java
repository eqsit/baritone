/*
 * This file is part of Baritone.
 */
package baritone.utils.player;

import baritone.Baritone;
import baritone.api.cache.IWorldData;
import baritone.api.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Player context backed by the {@link Baritone}'s bound client context.
 * The binding may be session-backed, so no player/world/controller lookup in
 * this class consults Minecraft's mutable global account fields directly.
 */
public final class BaritonePlayerContext implements IPlayerContext {

    private final Baritone baritone;
    private final IPlayerController playerController;

    public BaritonePlayerContext(Baritone baritone) {
        this.baritone = baritone;
        this.playerController = new BaritonePlayerController(baritone);
    }

    @Override
    public Minecraft minecraft() {
        return this.baritone.getClientContext().minecraft();
    }

    @Override
    public LocalPlayer player() {
        return this.baritone.getClientContext().player();
    }

    @Override
    public IPlayerController playerController() {
        return this.playerController;
    }

    @Override
    public Level world() {
        return this.baritone.getClientContext().world();
    }

    @Override
    public IWorldData worldData() {
        return this.baritone.getWorldProvider().getCurrentWorld();
    }

    @Override
    public BetterBlockPos viewerPos() {
        final Entity entity = this.baritone.getClientContext().cameraEntity();
        return entity == null ? this.playerFeet() : BetterBlockPos.from(entity.blockPosition());
    }

    @Override
    public Rotation playerRotations() {
        return this.baritone.getLookBehavior().getEffectiveRotation().orElseGet(IPlayerContext.super::playerRotations);
    }

    @Override
    public HitResult objectMouseOver() {
        LocalPlayer player = player();
        if (player == null) return null;
        return RayTraceUtils.rayTraceTowards(player, playerRotations(), playerController().getBlockReachDistance());
    }
}
