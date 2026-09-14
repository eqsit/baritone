/*
 * This file is part of Baritone.
 */
package baritone.utils.player;

import baritone.Baritone;
import baritone.api.utils.IPlayerController;
import baritone.utils.accessor.IPlayerControllerMP;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/** Controller actions are always sent through this Baritone's bound session. */
public final class BaritonePlayerController implements IPlayerController {

    private final Baritone baritone;

    public BaritonePlayerController(Baritone baritone) {
        this.baritone = baritone;
    }

    private MultiPlayerGameMode controller() {
        MultiPlayerGameMode controller = this.baritone.getClientContext().interactionManager();
        if (controller == null) {
            throw new IllegalStateException("Baritone client context has no interaction manager");
        }
        return controller;
    }

    @Override
    public void syncHeldItem() {
        ((IPlayerControllerMP) controller()).callSyncCurrentPlayItem();
    }

    @Override
    public boolean hasBrokenBlock() {
        return !((IPlayerControllerMP) controller()).isHittingBlock();
    }

    @Override
    public boolean onPlayerDamageBlock(BlockPos pos, Direction side) {
        return controller().continueDestroyBlock(pos, side);
    }

    @Override
    public void resetBlockRemoving() {
        controller().stopDestroyBlock();
    }

    @Override
    public void windowClick(int windowId, int slotId, int mouseButton, ClickType type, Player player) {
        controller().handleInventoryMouseClick(windowId, slotId, mouseButton, type, player);
    }

    @Override
    public GameType getGameType() {
        return controller().getPlayerMode();
    }

    @Override
    public InteractionResult processRightClickBlock(LocalPlayer player, Level world, InteractionHand hand, BlockHitResult result) {
        return controller().useItemOn(player, hand, result);
    }

    @Override
    public InteractionResult processRightClick(LocalPlayer player, Level world, InteractionHand hand) {
        return controller().useItem(player, hand);
    }

    @Override
    public boolean clickBlock(BlockPos loc, Direction face) {
        return controller().startDestroyBlock(loc, face);
    }

    @Override
    public void setHittingBlock(boolean hittingBlock) {
        ((IPlayerControllerMP) controller()).setIsHittingBlock(hittingBlock);
    }

    @Override
    public void setDestroyDelay(int delay) {
        ((IPlayerControllerMP) controller()).setDestroyDelay(delay);
    }
}
