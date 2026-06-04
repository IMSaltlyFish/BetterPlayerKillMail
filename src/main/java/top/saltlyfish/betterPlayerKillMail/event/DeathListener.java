package top.saltlyfish.betterPlayerKillMail.event;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import top.saltlyfish.betterPlayerKillMail.BetterPlayerKillMail;
import top.saltlyfish.betterPlayerKillMail.database.mysql.MySQLService;
import top.saltlyfish.betterPlayerKillMail.gui.KillMailGui;
import top.saltlyfish.betterPlayerKillMail.record.EntityRecord;
import top.saltlyfish.betterPlayerKillMail.record.PlayerDeathRecord;
import top.saltlyfish.betterPlayerKillMail.util.RandomUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class DeathListener implements Listener {

    private final BetterPlayerKillMail plugin = BetterPlayerKillMail.getInstance();
    private final MySQLService sqlService = plugin.getSqlService();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Entity killer = event.getDamageSource().getCausingEntity();


        List<ItemStack> drops = event.getDrops();
        List<ItemStack> keepDrops = event.getItemsToKeep();
        Iterator<ItemStack> iterator = drops.iterator();
        // 如果为玩家击杀
        if (killer instanceof Player){
            while (iterator.hasNext()){
                ItemStack item = iterator.next();
                keepDrops.add(item);
                iterator.remove();
            }
        } else{
            // 随机掉落算法
            while(iterator.hasNext()){
                ItemStack item = iterator.next();
                if (RandomUtil.shouldDropPercent(70)){
                    keepDrops.add(item);
                    iterator.remove();
                }
            }
        }

        // 记录快照
        PlayerDeathRecord km;
        if (killer == null){
            km = new PlayerDeathRecord(
                    LocalDateTime.now(),
                    new EntityRecord(null, null),
                    new EntityRecord(player.getUniqueId(),player.getType()),
                    player.getLastDeathLocation(),
                    drops,
                    event.getDamageSource().getDamageType()
            );
        }else {
            km = new PlayerDeathRecord(
                    LocalDateTime.now(),
                    new EntityRecord(killer.getUniqueId(),killer.getType()),
                    new EntityRecord(player.getUniqueId(),player.getType()),
                    player.getLastDeathLocation(),
                    drops,
                    event.getDamageSource().getDamageType()
            );
        }

        BetterPlayerKillMail.getInstance().putDeathRecord(km.getUuid(),km);
        log.debug(km.toString());

        Component original = event.deathMessage();
        if (original == null) return;

        Component suffix = Component.text(" [击杀报告]")
                .color(NamedTextColor.GRAY)
                .clickEvent(ClickEvent.runCommand("/kkkm " + km.getUuid()))
                .hoverEvent(HoverEvent.showText(Component.text("查看死亡报告")));

        Component newMessage = original.append(suffix);
        // 应用新信息
        event.deathMessage(newMessage);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof KillMailGui) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof KillMailGui) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent e) {
        if (e.getInventory().getHolder() instanceof KillMailGui) {
            e.setCancelled(true);
        }
    }
}
