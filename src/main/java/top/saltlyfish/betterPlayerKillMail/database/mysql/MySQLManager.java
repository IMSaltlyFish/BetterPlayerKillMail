package top.saltlyfish.betterPlayerKillMail.database.mysql;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLManager {
    private static final Logger log = LoggerFactory.getLogger(MySQLManager.class);
    private static SqlSessionFactory sqlSessionFactory;
    private static HikariDataSource dataSource;
    private static boolean isInited;

    public static void init(FileConfiguration pluginConfig){
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + pluginConfig.getString("bpkm.mysql.url","jdbc:mysql://localhost:3306/sfpserver"));
        config.setUsername(pluginConfig.getString("bpkm.mysql.username","root"));
        config.setPassword(pluginConfig.getString("bpkm.mysql.password","admin"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        dataSource = new HikariDataSource(config);

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("production", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);

        // Mapper 扫描
        configuration.addMappers("top.saltlyfish.betterPlayerKillMail.mapper");

        sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        isInited = true;
        log.info("--- power by MySQLManager ---");
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        if (!getSQLManagerState()){
            log.warn("SQLManager is not init");
        }
        return sqlSessionFactory;
    }

    private static boolean getSQLManagerState(){
        return isInited;
    }

    public static void shutdown() {
        isInited = false;
        if (dataSource != null) dataSource.close();
    }
}
