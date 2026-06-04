package top.saltlyfish.betterPlayerKillMail.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class EntityItemUtil {
    /**
     * 根据实体类型获取对应的头颅物品（若存在）
     * @param type 实体类型
     * @return 物品 ItemStack，若无对应物品则返回占位符
     */
    public static ItemStack getHeadForEntityType(EntityType type) {
        Material headMaterial = getHeadMaterial(type);
        if (headMaterial == null) return null;
        return new ItemStack(headMaterial);
    }

    /**
     * 获取对应的 Material
     */
    private static Material getHeadMaterial(EntityType type) {
        if (type == null)return Material.STRUCTURE_VOID;
        return switch (type) {
            case ZOMBIE, HUSK, DROWNED, ZOMBIE_VILLAGER -> Material.ZOMBIE_HEAD;
            case CREEPER -> Material.CREEPER_HEAD;
            case SKELETON, STRAY, BOGGED -> Material.SKELETON_SKULL;
            case WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL;
            case ENDER_DRAGON -> Material.DRAGON_HEAD;
            case PIGLIN, PIGLIN_BRUTE -> Material.PIGLIN_HEAD;
            case PLAYER -> Material.PLAYER_HEAD;
            case ENDERMAN -> Material.ENDER_PEARL;
            default -> Material.STRUCTURE_VOID;
        };
    }
}
