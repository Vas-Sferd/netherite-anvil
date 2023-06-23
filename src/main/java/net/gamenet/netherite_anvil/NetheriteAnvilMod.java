package net.gamenet.netherite_anvil;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.gamenet.netherite_anvil.item.NetheriteAnvil;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetheriteAnvilMod implements ModInitializer {
    public static final String  MOD_ID = "netherite-anvil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item NETHERITE_ANVIL_SMITHING_TEMPLATE
            = Registry.register(Registry.ITEM, MOD_ID + ":netherite_anvil_smithing_template", new Item(new FabricItemSettings().group(CreativeModeTab.TAB_MATERIALS)));

    public static Block NETHERITE_ANVIL_BLOCK
            = Registry.register(Registry.BLOCK, MOD_ID + ":netherite_anvil_block", new NetheriteAnvil(FabricBlockSettings.of(Material.HEAVY_METAL, MaterialColor.METAL)
    ));

    public static Item NETHERITE_ANVIL_BLOCK_ITEM
            = Registry.register(Registry.ITEM, MOD_ID + ":netherite_anvil_block", new BlockItem(NETHERITE_ANVIL_BLOCK, new FabricItemSettings().group(CreativeModeTab.TAB_DECORATIONS)));


    @Override
    public void onInitialize() {
    }
}
