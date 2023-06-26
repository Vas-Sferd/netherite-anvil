package net.gamenet.netherite_anvil;

import net.fabricmc.api.ClientModInitializer;
import net.gamenet.netherite_anvil.screen.NetheriteAnvilScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class NetheriteAnvilModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(NetheriteAnvilMod.NETHERITE_ANVIL_MENU_TYPE, NetheriteAnvilScreen::new);
    }
}
