package top.saltlyfish.betterPlayerKillMail.database.async;

import org.apache.ibatis.session.SqlSession;

@FunctionalInterface
public interface SqlTask {
    void execute(SqlSession session) throws Exception;
}