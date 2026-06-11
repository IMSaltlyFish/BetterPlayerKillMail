package top.saltlyfish.betterPlayerKillMail.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.saltlyfish.betterPlayerKillMail.BetterPlayerKillMail;
import top.saltlyfish.betterPlayerKillMail.gui.KillMailGui;
import top.saltlyfish.betterPlayerKillMail.record.PlayerDeathRecord;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandManager implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§c用法: /kkkm <击杀记录UUID>");
            return false;
        }
        if (!args[0].isBlank() && commandSender instanceof Player player){
            UUID uuid;
            try {
                uuid = UUID.fromString(args[0]);
                PlayerDeathRecord record = BetterPlayerKillMail.getInstance().getDeathRecord(uuid);

                if (record == null) {
                    commandSender.sendMessage("§c未找到该玩家的击杀记录（可能已过期）");
                    return true;
                }

                KillMailGui gui = new KillMailGui(uuid,player);
                player.openInventory(gui.getInventory());
                return true;
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage("§c无效的 UUID 格式");
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
