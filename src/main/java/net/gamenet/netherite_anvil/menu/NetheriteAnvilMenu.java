package net.gamenet.netherite_anvil.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class NetheriteAnvilMenu extends AnvilMenu {
    private int repairItemCountCost;
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();
    private static final int COST_MAX = 150;
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 0;
    private static final int COST_REDUCTION_FACTOR = 2;

    public NetheriteAnvilMenu(int i, Inventory inventory) {
        super(i, inventory);
    }

    public NetheriteAnvilMenu(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(i, inventory, containerLevelAccess);
    }

    @Override
    protected void onTake(Player player, ItemStack itemStack) {
        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemStack2 = this.inputSlots.getItem(1);
            if (!itemStack2.isEmpty() && itemStack2.getCount() > this.repairItemCountCost) {
                itemStack2.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemStack2);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }
        this.cost.set(0);
        this.repairItemCountCost = 0;
        this.access.execute((level, blockPos) -> level.levelEvent(1030, blockPos, 0));
    }

    @Override
    public void createResult() {
        ItemStack leftItem = this.inputSlots.getItem(0);
        ItemStack rightItemOrMaterial = this.inputSlots.getItem(1);
        int experienceLevelCost = COST_BASE;
        this.repairItemCountCost = 0;

        boolean canRepairThisItemCount = leftItem.getCount() == 1 || this.player.getAbilities().instabuild;
        if (leftItem.isEmpty() && canRepairThisItemCount) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(COST_FAIL);
            return;
        }

        ItemStack resultItem = leftItem.copy();

        if (StringUtils.isBlank(this.itemName)) {
            if (leftItem.hasCustomHoverName()) {
                experienceLevelCost += COST_RENAME;
                resultItem.resetHoverName();
            }
        } else if (!this.itemName.equals(leftItem.getHoverName().getString())) {
            experienceLevelCost += COST_RENAME;
            resultItem.setHoverName(Component.literal(this.itemName));
        }

        if (!rightItemOrMaterial.isEmpty() && canRepairThisItemCount) {
            int leftRepairCost = leftItem.getBaseRepairCost();
            int rightRepairCost = rightItemOrMaterial.getBaseRepairCost();

            int baseRepairCost = leftRepairCost + rightRepairCost;
            experienceLevelCost += baseRepairCost;

            if (leftItem.isDamageableItem()) {
                if (resultItem.getItem().isValidRepairItem(leftItem, rightItemOrMaterial))
                {
                    experienceLevelCost += repairByMaterial(resultItem, rightItemOrMaterial) * COST_REPAIR_MATERIAL;
                } else if (rightItemOrMaterial.is(leftItem.getItem())) {
                    experienceLevelCost += repairBySameItem(resultItem, rightItemOrMaterial) * COST_REPAIR_SACRIFICE;
                }
            }
            boolean isRightIsEnchantedBook = rightItemOrMaterial.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(rightItemOrMaterial).isEmpty();
            if (rightItemOrMaterial.isEnchanted() || isRightIsEnchantedBook) {
                Map<Enchantment, Integer> resultEnchantments = EnchantmentHelper.getEnchantments(resultItem);
                Map<Enchantment, Integer> rightEnchantments = EnchantmentHelper.getEnchantments(rightItemOrMaterial);

                boolean hasSuitableEnchantment = false;
                boolean hasUnsuitableEnchantment = false;
                for (Enchantment enchantment : rightEnchantments.keySet()) {
                    int resultEnchantmentLevel = resultEnchantments.getOrDefault(enchantment, 0);
                    int rightEnchantmentLevel = resultEnchantments.get(enchantment);

                    int newEnchantmentLevel;
                    if (resultEnchantmentLevel == rightEnchantmentLevel) {
                        newEnchantmentLevel = resultEnchantmentLevel + 1;
                    } else {
                        newEnchantmentLevel = Math.max(resultEnchantmentLevel, rightEnchantmentLevel);
                    }

                    boolean canEnchant = enchantment.canEnchant(leftItem) || this.player.getAbilities().instabuild || leftItem.is(Items.ENCHANTED_BOOK);
                    for (Enchantment resultEnchantment : resultEnchantments.keySet()) {
                        if (resultEnchantment == enchantment || enchantment.isCompatibleWith(resultEnchantment)) continue;
                        canEnchant = false;
                        experienceLevelCost += COST_INCOMPATIBLE_PENALTY;
                    }

                    if (!canEnchant) {
                        hasUnsuitableEnchantment = true;
                        continue;
                    }

                    hasSuitableEnchantment = true;
                    if (newEnchantmentLevel > enchantment.getMaxLevel()) {
                        newEnchantmentLevel = enchantment.getMaxLevel();
                    }
                    resultEnchantments.put(enchantment, newEnchantmentLevel);

                    int enchantmentCost = 0;
                    switch (enchantment.getRarity()) {
                        case COMMON -> enchantmentCost = 1;
                        case UNCOMMON -> enchantmentCost = 2;
                        case RARE -> enchantmentCost = 4;
                        case VERY_RARE -> enchantmentCost = 8;
                    }

                    if (isRightIsEnchantedBook) {
                        enchantmentCost = Math.max(1, enchantmentCost / 2);
                    }

                    experienceLevelCost += enchantmentCost * newEnchantmentLevel;
                }
                if (hasUnsuitableEnchantment && !hasSuitableEnchantment) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
                EnchantmentHelper.setEnchantments(resultEnchantments, resultItem);
            }
        }

        this.cost.set(experienceLevelCost / COST_REDUCTION_FACTOR);
        resultItem.setRepairCost(1);

        if (this.cost.get() >= COST_MAX && !this.player.getAbilities().instabuild) {
            resultItem = ItemStack.EMPTY;
        }

        this.resultSlots.setItem(0, resultItem);
        this.broadcastChanges();
    }

    public void setItemName(String name) {
        this.itemName = name;
        if (this.getSlot(2).hasItem()) {
            ItemStack itemStack = this.getSlot(2).getItem();
            if (StringUtils.isBlank(name)) {
                itemStack.resetHoverName();
            } else {
                itemStack.setHoverName(Component.literal(this.itemName));
            }
        }
        this.createResult();
    }

    public int repairByMaterial(ItemStack left, ItemStack material) {
        boolean isRightIsNetheriteIngot = material.is(Items.NETHERITE_INGOT);
        int repairedPerOneMaterial = left.getMaxDamage() / 2;
        if (isRightIsNetheriteIngot) {
            repairedPerOneMaterial = left.getMaxDamage();
        }

        this.repairItemCountCost = Math.min(left.getDamageValue() % repairedPerOneMaterial, material.getCount());
        int repairedDamageValue = Math.max(0, left.getDamageValue() - this.repairItemCountCost * repairedPerOneMaterial);
        left.setDamageValue(repairedDamageValue);
        return this.repairItemCountCost;
    }

    public int repairBySameItem(ItemStack left, ItemStack right) {
        int leftQuantity = left.getMaxDamage() - left.getDamageValue();
        int rightQuantify = right.getMaxDamage() - right.getDamageValue();
        int newQuantify = leftQuantity + rightQuantify + left.getMaxDamage() * 20 / 100;
        int newDamage = Math.max(0, left.getMaxDamage() - newQuantify);
        int cost = 0;
        if (newDamage < left.getDamageValue()) {
            cost = 1;
        }
        left.setDamageValue(newDamage);
        return cost;
    }

    public int getCost() {
        return this.cost.get();
    }
}
