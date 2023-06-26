package net.gamenet.netherite_anvil;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.gamenet.netherite_anvil.item.NetheriteAnvil;
import net.gamenet.netherite_anvil.menu.NetheriteAnvilMenu;
import net.gamenet.netherite_anvil.screen.NetheriteAnvilScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetheriteAnvilMod implements ModInitializer {
    public static final String  MOD_ID = "netherite-anvil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item NETHERITE_ANVIL_SMITHING_TEMPLATE
            = Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "netherite_anvil_smithing_template"), new Item(new FabricItemSettings().group(CreativeModeTab.TAB_MATERIALS)));

    public static Block NETHERITE_ANVIL_BLOCK
            = Registry.register(Registry.BLOCK, new ResourceLocation(MOD_ID, "netherite_anvil_block"), new NetheriteAnvil((FabricBlockSettings) FabricBlockSettings.of(Material.HEAVY_METAL, MaterialColor.METAL)
            .requiresTool()
            .requiresCorrectToolForDrops().strength(15.0f, 1200.0f).sound(SoundType.ANVIL)
    ));

    public static Item NETHERITE_ANVIL_BLOCK_ITEM
            = Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "netherite_anvil_block"), new BlockItem(NETHERITE_ANVIL_BLOCK, new FabricItemSettings().group(CreativeModeTab.TAB_DECORATIONS)));

    public static final MenuType<NetheriteAnvilMenu> NETHERITE_ANVIL_MENU_TYPE
            = Registry.register(Registry.MENU, new ResourceLocation(MOD_ID, "netherite_anvil"), new MenuType<>(NetheriteAnvilMenu::new));

    @Override
    public void onInitialize() {
        MenuScreens.register(NETHERITE_ANVIL_MENU_TYPE, NetheriteAnvilScreen::new);
    }
}
