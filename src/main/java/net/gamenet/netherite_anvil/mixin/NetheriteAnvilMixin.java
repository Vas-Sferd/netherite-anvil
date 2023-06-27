package net.gamenet.netherite_anvil.mixin;

import net.gamenet.netherite_anvil.menu.NetheriteAnvilMenu;
import net.minecraft.SharedConstants;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class NetheriteAnvilMixin {
    @Final
    @Shadow
    static Logger LOGGER;

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleRenameItem", at = @At("TAIL"), cancellable = true)
    private void handleRenameItem(ServerboundRenameItemPacket serverboundRenameItemPacket, CallbackInfo ci) {
        AbstractContainerMenu abstractContainerMenu = this.player.containerMenu;
        if (abstractContainerMenu instanceof NetheriteAnvilMenu netheriteAnvilMenu) {
            if (!netheriteAnvilMenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)netheriteAnvilMenu);
                return;
            }
            String string = SharedConstants.filterText(serverboundRenameItemPacket.getName());
            if (string.length() <= NetheriteAnvilMenu.MAX_NAME_LENGTH) {
                netheriteAnvilMenu.setItemName(string);
            }
        }
    }
}
