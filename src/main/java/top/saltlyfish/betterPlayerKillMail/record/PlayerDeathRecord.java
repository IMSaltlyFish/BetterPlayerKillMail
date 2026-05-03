package top.saltlyfish.betterPlayerKillMail.record;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import top.saltlyfish.betterPlayerKillMail.util.EntityItemUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class PlayerDeathRecord{
    private UUID uuid;
    private LocalDateTime deathDate;

    private Entity killer;
    private Entity victim;
    private Location location;

    private List<ItemStack> drops;
    private DamageSource deathCause;

    public PlayerDeathRecord(LocalDateTime deathDate, Entity killer, Entity victim,Location location, List<ItemStack> drops, DamageSource deathCause) {
        uuid = UUID.randomUUID();
        this.deathDate = deathDate;
        this.killer = killer;
        this.victim = victim;
        this.location = location;
        // copy record
        this.drops = drops
                .stream()
                .map(ItemStack::clone)
                .collect(Collectors.toList());
        this.deathCause = deathCause;
    }

    public ItemStack getKillerToItemStack() {
        ItemStack itemStack;
        if (killer == null){
            itemStack = buildHeadItemStack();
        }else {
            itemStack = buildHeadItemStack(killer);
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Component
                    .text("击杀者：" + killer.getName())
                    .decoration(TextDecoration.ITALIC,false)
                    .color(NamedTextColor.RED));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public ItemStack getVictimToItemStack() {
        if (victim == null) {
            return new ItemStack(Material.AIR);
        }
        ItemStack itemStack = buildHeadItemStack(victim);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component
                .text("受害者：" + victim.getName())
                .decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.YELLOW)
        );
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack buildHeadItemStack(Entity entity) {
        if (entity == null) return new ItemStack(Material.AIR);

        ItemStack head = EntityItemUtil.getHeadForEntityType(entity.getType());
        if (head == null)return new ItemStack(Material.AIR);
        // if Entity is Player
        if(entity instanceof Player player){
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            head.setItemMeta(meta);
        }else {
            ItemMeta meta = head.getItemMeta();
            meta.displayName(Component.text(entity.getType().name()));
            head.setItemMeta(meta);
        }
        return head;
    }

    private ItemStack buildHeadItemStack() {
        return new ItemStack(Material.AIR);
    }

}