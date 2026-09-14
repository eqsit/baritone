/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package baritone.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Supplies all mutable client-side account state consumed by one Baritone instance.
 *
 * <p>The default context returned by {@link #forMinecraft(Minecraft)} preserves
 * upstream single-player behavior by reading the active fields from the supplied
 * {@link Minecraft} instance. Multi-account clients may instead provide a context
 * backed by a stable session object, allowing several Baritone instances to share
 * one Minecraft object without sharing player/world/controller state.</p>
 */
public interface IBaritoneClientContext {

    Minecraft minecraft();

    LocalPlayer player();

    ClientLevel world();

    MultiPlayerGameMode interactionManager();

    /** The network handler belonging to {@link #player()}, if available. */
    default ClientPacketListener networkHandler() {
        LocalPlayer player = player();
        return player == null ? null : player.connection;
    }

    /** Camera/view entity used for viewer-position calculations. */
    default Entity cameraEntity() {
        return player();
    }

    /**
     * Legacy secondary Baritone instances behave as autonomous bots and keep
     * a Baritone movement input installed even while idle. Session-based
     * multi-account clients can return false so a foreground account regains
     * normal keyboard input whenever Baritone is not controlling movement.
     */
    default boolean keepBaritoneInputWhenIdle() {
        return true;
    }

    /**
     * Creates the legacy singleton-backed context used by normal Baritone clients.
     */
    static IBaritoneClientContext forMinecraft(Minecraft minecraft) {
        return new IBaritoneClientContext() {
            @Override
            public Minecraft minecraft() {
                return minecraft;
            }

            @Override
            public LocalPlayer player() {
                return minecraft.player;
            }

            @Override
            public ClientLevel world() {
                return minecraft.level;
            }

            @Override
            public MultiPlayerGameMode interactionManager() {
                return minecraft.gameMode;
            }

            @Override
            public Entity cameraEntity() {
                return minecraft.getCameraEntity();
            }
        };
    }
}
