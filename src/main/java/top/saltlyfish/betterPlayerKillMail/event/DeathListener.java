package top.saltlyfish.betterPlayerKillMail.event;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
import top.saltlyfish.betterPlayerKillMail.gui.KillMailGui;
import top.saltlyfish.betterPlayerKillMail.record.PlayerDeathRecord;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        List<ItemStack> drops = event.getDrops();

        PlayerDeathRecord km = new PlayerDeathRecord(
                LocalDateTime.now(),
                event.getDamageSource().getCausingEntity(),
                player,
                player.getLastDeathLocation(),
                drops,
                event.getDamageSource()
        );
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
