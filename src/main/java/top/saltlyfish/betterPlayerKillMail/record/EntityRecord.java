package top.saltlyfish.betterPlayerKillMail.record;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public record EntityRecord(UUID uuid, EntityType entityType) {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);  // 可选，格式化输出

    /**
     * 序列化为 JSON 字符串。
     */
    public String serialize() throws JsonProcessingException {
        return MAPPER.writeValueAsString(this);
    }

    /**
     * 从 JSON 字符串反序列化。
     */
    public static EntityRecord deserialize(String json) throws JsonProcessingException {
        return MAPPER.readValue(json, EntityRecord.class);
    }
}
