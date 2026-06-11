package top.saltlyfish.betterPlayerKillMail.mapper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class DamageTypeToIconMapper {
    public static ItemStack getLocationIcon(Location location){
        ItemStack itemStack = new ItemStack(Material.COMPASS);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text("死亡地点")
                .decoration(TextDecoration.ITALIC,false));
        meta.lore(Collections.singletonList(Component
                .text(String.format("X:%s Y:%s Z:%s",location.getX(),location.getY(),location.getZ()))
                .decoration(TextDecoration.ITALIC,false)));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getIcon(DamageType damageType){
        String key = damageType.getKey().getKey();  // 例如 "in_fire", "fall" 等
        Material material;
        String displayName = switch (key) {
            case "in_fire", "on_fire" -> {
                material = Material.FIRE_CHARGE;
                yield "§c火焰伤害";
            }
            case "lava" ->{
                material = Material.LAVA_BUCKET;
                yield "§c岩浆";
            }
            case "hot_floor" -> {
                material = Material.MAGMA_BLOCK;
                yield "placeholder";
            }
            case "lightning_bolt" -> {
                material = Material.LIGHTNING_ROD;
                yield "§e闪电打击";
            }
            case "drown" -> {
                material = Material.WATER_BUCKET;
                yield "§b溺水";
            }
            case "starve" -> {
                material = Material.ROTTEN_FLESH;
                yield "§8饥饿";
            }
            case "cactus" -> {
                material = Material.CACTUS;
                yield "§a仙人掌刺伤";
            }
            case "fall", "fly_into_wall" -> {
                material = Material.FEATHER;
                yield "§6摔落伤害";
            }
            case "out_of_world", "void" -> {
                material = Material.BARRIER;
                yield "§5虚空伤害";
            }
            case "magic" -> {
                material = Material.ENCHANTED_BOOK;
                yield "§d魔法伤害";
            }
            case "wither" -> {
                material = Material.WITHER_ROSE;
                yield "§8凋零";
            }
            case "anvil", "falling_block" -> {
                material = Material.ANVIL;
                yield "§7坠物砸伤";
            }
            case "dragon_breath" -> {
                material = Material.DRAGON_BREATH;
                yield "§5龙息";
            }
            case "sweet_berry_bush" -> {
                material = Material.SWEET_BERRIES;
                yield "§c甜浆果丛刺伤";
            }
            case "freeze" -> {
                material = Material.BLUE_ICE;
                yield "§b冰冻";
            }
            case "stalactite" -> {
                material = Material.POINTED_DRIPSTONE;
                yield "§7钟乳石";
            }
            case "sonic_boom" -> {
                material = Material.SCULK_SHRIEKER;
                yield "§d音爆";
            }
            case "campfire" -> {
                material = Material.CAMPFIRE;
                yield "§c篝火灼烧";
            }
            case "mob_attack" -> {
                material = Material.IRON_SWORD;
                yield "§c攻击致死";
            }
            case "player_attack"->{
                material = Material.DIAMOND_SWORD;
                yield "§c玩家击杀";
            }
            case "player_explosion" -> {
                material = Material.TNT;
                yield "§c爆炸致死";
            }
            case "arrow" -> {
                material = Material.ARROW;
                yield "§c箭矢伤害";
            }
            case "in_well"->{
                material = Material.MAGMA_BLOCK;
                yield "§8窒息而亡";
            }
            default -> {
                material = Material.BARRIER;
                yield "§8未知伤害";
            }
        };

        ItemStack icon = new ItemStack(material);
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName));
            icon.setItemMeta(meta);
        }
        return icon;
    }
}
