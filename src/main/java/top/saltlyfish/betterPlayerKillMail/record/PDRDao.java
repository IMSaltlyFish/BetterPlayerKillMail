package top.saltlyfish.betterPlayerKillMail.record;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class PDRDao {
    private String uuid;
    private LocalDateTime deathDate;

    /** EntityRecord 的 序列化对象 */
    private String killer;
    /** EntityRecord 的 序列化对象 */
    private String victim;

    /** Map<Object,String>的 Json 序列化对象 */
    private String location;
    /** List<ItemStack>的 Json 序列化对象 */
    private String drops;
    /** damageType 的 key 值 */
    private String damageType;

}
