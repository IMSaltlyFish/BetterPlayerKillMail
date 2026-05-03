package top.saltlyfish.betterPlayerKillMail;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.saltlyfish.betterPlayerKillMail.command.CommandManager;
import top.saltlyfish.betterPlayerKillMail.event.DeathListener;
import top.saltlyfish.betterPlayerKillMail.record.PlayerDeathRecord;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BetterPlayerKillMail extends JavaPlugin {

    private static final Logger log = LoggerFactory.getLogger(BetterPlayerKillMail.class);
    @Getter
    private static BetterPlayerKillMail instance;

    @Getter
    private Cache<UUID, PlayerDeathRecord> recordCache;
    @Getter
    private Cache<UUID, Inventory> guiCache;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // 单例模式
        instance = this;
        // init cache
        recordCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)                       // 最大1000个条目
                .recordStats()                           // 记录命中率
                .build();// 写入后10分钟过期

        guiCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)  // 写入后10分钟过期
                .maximumSize(1000)                       // 最大1000个条目
                .recordStats()                           // 记录命中率
                .build();


        getLogger().info("cache initialized.");
        log.info("BPKM Start");
        // login event
        getServer().getPluginManager().registerEvents(new DeathListener(),this);
        // login command
        getCommand("kkkm").setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (recordCache != null) {
            recordCache.invalidateAll();
            recordCache.cleanUp();
        }
        log.info("disable cache");
        instance = null;
    }

    public PlayerDeathRecord getDeathRecord(UUID uuid) {
        return recordCache.getIfPresent(uuid);
    }

    public void putDeathRecord(UUID key,PlayerDeathRecord record){
        recordCache.put(key,record);
    }
}
