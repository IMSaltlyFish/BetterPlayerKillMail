package top.saltlyfish.betterPlayerKillMail.record;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import top.saltlyfish.betterPlayerKillMail.util.EntityItemUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PlayerDeathRecord{

    private static final RegistryAccess REGISTRY_ACCESS = RegistryAccess.registryAccess();
    private final UUID uuid;
    private final LocalDateTime deathDate;

    private final EntityRecord killer;

    // 受害者永远是玩家 可以直接存Entity
    private final EntityRecord victim;

    private final Location location;

    private final List<ItemStack> drops;
    private final DamageType damageType;


    public PlayerDeathRecord(LocalDateTime deathDate, EntityRecord killer, EntityRecord victim, Location location, List<ItemStack> drops, DamageType damageType) {
        this.uuid = UUID.randomUUID();
        this.deathDate = deathDate;
        this.killer = killer;
        this.victim = victim;
        this.location = location;
        // 物品堆
        this.drops = drops
                .stream()
                .map(ItemStack::clone)
                .collect(Collectors.toList());
        this.damageType = damageType;
    }

    private PlayerDeathRecord(PDRDao dao) throws JsonProcessingException {
        this.uuid = UUID.fromString(dao.getUuid());
        this.deathDate = dao.getDeathDate();

        this.killer = EntityRecord.deserialize(dao.getKiller());
        this.victim = EntityRecord.deserialize(dao.getVictim());

        this.location = deserializeLocation(dao.getLocation());
        this.drops = deserializeItemStacks(dao.getDrops());

        this.damageType = REGISTRY_ACCESS
                .getRegistry(RegistryKey.DAMAGE_TYPE)
                .getOrThrow(Objects.requireNonNull(NamespacedKey.fromString(dao.getDamageType())));

    }

    /**
     * 将一个 ItemStack 列表序列化为 JSON 字符串。
     */
    public String serializeItemStacks(List<ItemStack> items) throws JsonProcessingException {
        if (items == null) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 List<ItemStack> 转换为 List<Map<String, Object>>
        List<Map<String, Object>> serializedItems = new ArrayList<>();
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) {
                serializedItems.add(null);
            } else {
                // 关键一步：调用 Bukkit 的 serialize 方法
                serializedItems.add(item.serialize());
            }
        }
        return objectMapper.writeValueAsString(serializedItems);
    }

    /**
     * 将一个 Location 序列化为 JSON 字符串。
     */
    public String serializeLocation(Location location) throws JsonProcessingException {
        if (location == null) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Object> serializeItem = location.serialize();
        return objectMapper.writeValueAsString(serializeItem);
    }

    /**
     * 从 JSON 字符串反序列化出 ItemStack 列表。
     */
    public List<ItemStack> deserializeItemStacks(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 先将 JSON 解析为 List<Map>
        List<Map<String, Object>> serializedItems = objectMapper.readValue(json,
                new TypeReference<List<Map<String, Object>>>() {});

        List<ItemStack> result = new ArrayList<>();
        if (serializedItems == null) return result;

        for (Map<String, Object> map : serializedItems) {
            if (map == null) {
                result.add(null); // 代表空槽位
            } else {
                // 关键一步：调用 Bukkit 的反序列化方法
                result.add(ItemStack.deserialize(map));
            }
        }
        return result;
    }
    /**
     * 从 JSON 字符串反序列化出 Location
     */
    public Location deserializeLocation(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Object> item = objectMapper.readValue(json,
                new TypeReference<Map<String, Object>>() {});

        return Location.deserialize(item);
    }

    // 序列化为 PDRDao 对象
    public PDRDao serialization(PlayerDeathRecord pdr) throws JsonProcessingException {
        PDRDao dao = new PDRDao();
        dao.setUuid(pdr.getUuid().toString());
        dao.setDeathDate(pdr.getDeathDate());

        dao.setKiller(pdr.getKiller().serialize());
        dao.setVictim(pdr.getVictim().serialize());

        dao.setLocation(serializeLocation(pdr.getLocation()));
        dao.setDrops(serializeItemStacks(pdr.getDrops()));
        dao.setDamageType(pdr.getDamageType().getKey().getKey());

        return dao;
    }

    public PlayerDeathRecord deserialize(PDRDao dao) throws JsonProcessingException {
        return new PlayerDeathRecord(dao);
    }

    public ItemStack getKillerToItemStack() {
        ItemStack itemStack;
        if (killer.uuid() == null){
            itemStack = buildHeadItemStack();
        }else {
            itemStack = buildHeadItemStack(killer);
            ItemMeta meta = itemStack.getItemMeta();
            String killerInfo;
            if (killer.entityType() == EntityType.PLAYER){
                Player player = Bukkit.getPlayer(killer.uuid());
                // 防御性编程
                if (player == null)return buildHeadItemStack();
                killerInfo = String.format("击杀者：%s",player.getName());
            }else {
                killerInfo = String.format("击杀者：%s",killer.entityType().getKey().getKey());
            }
            meta.displayName(Component
                    .text(killerInfo)
                    .decoration(TextDecoration.ITALIC,false)
                    .color(NamedTextColor.RED));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public ItemStack getVictimToItemStack() {
        Player player = Bukkit.getPlayer(victim.uuid());
        // 防御性编程 理论上不可能存在找不到玩家的情况，因为记录的受害者都是玩家
        if (player == null)return new ItemStack(Material.AIR);
        ItemStack itemStack = buildHeadItemStack(victim);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component
                .text("受害者：" + player.getName())
                .decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.YELLOW)
        );
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack buildHeadItemStack(EntityRecord entityRecord) {

        if (entityRecord.uuid() == null) return new ItemStack(Material.AIR);

        ItemStack head = EntityItemUtil.getHeadForEntityType(entityRecord.entityType());
        if (head == null)return new ItemStack(Material.AIR);
        // if Entity is Player
        if(entityRecord.entityType() == EntityType.PLAYER){
            Player player = Bukkit.getPlayer(entityRecord.uuid());
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            head.setItemMeta(meta);
        }else {
            ItemMeta meta = head.getItemMeta();
            meta.displayName(Component.text(entityRecord.entityType().name()));
            head.setItemMeta(meta);
        }
        return head;
    }

    private ItemStack buildHeadItemStack() {
        return new ItemStack(Material.AIR);
    }



}