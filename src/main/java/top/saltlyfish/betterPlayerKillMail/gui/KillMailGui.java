package top.saltlyfish.betterPlayerKillMail.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import top.saltlyfish.betterPlayerKillMail.BetterPlayerKillMail;
import top.saltlyfish.betterPlayerKillMail.mapper.DamageTypeToIconMapper;
import top.saltlyfish.betterPlayerKillMail.record.PlayerDeathRecord;
import top.saltlyfish.betterPlayerKillMail.util.DropLoreComponent;
import top.saltlyfish.betterPlayerKillMail.util.EntityItemUtil;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KillMailGui implements InventoryHolder {
    private final Inventory inventory;
    private static final int SLOT_KILLER = 0;
    private static final int SLOT_DEATH_CAUSE = 1;
    private static final int SLOT_VICTIM = 2;
    private static final int SLOT_LOCATION = 3;
    private static final int LOOT_INFO = 4;
    private static final int SLOT_LOOT_START = 9;

    public KillMailGui(UUID kmUuid, Player player){
        PlayerDeathRecord record = BetterPlayerKillMail.getInstance().getDeathRecord(kmUuid);
        List<ItemStack> loot = record.getDrops();
        this.inventory = Bukkit.createInventory(
                this,
                6*9,
                Component.text("击杀报告 - ")
                        .append(Component.text(record.getDeathDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
        );

        // Kill info
        if(record.getKiller() != null){
            inventory.setItem(SLOT_KILLER,record.getKillerToItemStack());
        }

        // DeathReason
        inventory.setItem(SLOT_DEATH_CAUSE, DamageTypeToIconMapper.getIcon(record.getDamageType()));

        // Victim info
        inventory.setItem(SLOT_VICTIM,record.getVictimToItemStack());

        // Death Location
        if (player.getUniqueId() == record.getVictim().uuid()){
            inventory.setItem(SLOT_LOCATION,DamageTypeToIconMapper.getLocationIcon(record.getLocation()));
        }
        // Loot info
        ItemStack lootItem = new ItemStack(Material.NAME_TAG);

        ItemMeta meta = lootItem.getItemMeta();
        meta.displayName(Component.text("掉落物品："));
        List<Component> lores = new ArrayList<>();
        for (ItemStack item : loot){
            lores.add(DropLoreComponent.of(item,item.getAmount()));
        }
        meta.lore(lores);
        lootItem.setItemMeta(meta);

        inventory.setItem(LOOT_INFO,lootItem);
        // put loot
        int index = SLOT_LOOT_START;
        int maxItems = inventory.getSize() - 9;
        for (ItemStack item : loot){
            if (index >= maxItems)break;
            inventory.setItem(index,item);
            index++;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
