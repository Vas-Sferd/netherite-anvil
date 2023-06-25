package net.gamenet.netherite_anvil.menu;

import net.gamenet.netherite_anvil.NetheriteAnvilMod;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;

public class NetheriteAnvilMenu extends AnvilMenu {
    public NetheriteAnvilMenu(int i, Inventory inventory) {
        super(i, inventory);
    }

    public NetheriteAnvilMenu(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(i, inventory, containerLevelAccess);
    }

//    @Override
//    protected boolean isValidBlock(BlockState blockState) {
//        return blockState.is(NetheriteAnvilMod.NETHERITE_ANVIL_TAG);
//    }
}
