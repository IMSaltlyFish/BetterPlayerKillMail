package top.saltlyfish.betterPlayerKillMail.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    private static final Random RANDOM = ThreadLocalRandom.current();

    private RandomUtil() {
        // 禁止实例化
    }

    /**
     * 根据指定的概率判定是否“掉落”。
     *
     * @param chance 掉落概率，范围 [0.0, 1.0]（例如 0.75 表示 75% 概率掉落）
     * @return 若随机值 ≤ 概率则返回 true，否则 false
     * @throws IllegalArgumentException 当 chance 不在 [0.0, 1.0] 范围内时抛出
     */
    public static boolean shouldDrop(double chance) {
        if (chance < 0.0 || chance > 1.0) {
            throw new IllegalArgumentException("概率必须在 0.0 到 1.0 之间，当前值：" + chance);
        }
        return RANDOM.nextDouble() < chance;
    }

    /**
     * 根据百分比概率判定是否“掉落”。
     *
     * @param percentage 掉落百分比，范围 [0, 100]（例如 75 表示 75% 概率掉落）
     * @return 若随机值 ≤ 百分比则返回 true，否则 false
     * @throws IllegalArgumentException 当 percentage 不在 [0, 100] 范围内时抛出
     */
    public static boolean shouldDropPercent(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("百分比必须在 0 到 100 之间，当前值：" + percentage);
        }
        return RANDOM.nextInt(100) < percentage;
    }

    /**
     * 生成一个 [0, bound) 之间的随机整数。
     *
     * @param bound 上限（不包括）
     * @return 随机整数
     */
    public static int randomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * 生成一个 [min, max] 之间的随机整数（包含两端）。
     *
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 随机整数
     * @throws IllegalArgumentException 当 min > max 时抛出
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min 不能大于 max");
        }
        return min + RANDOM.nextInt(max - min + 1);
    }

    /**
     * 生成一个 [0.0, 1.0) 之间的随机双精度浮点数。
     *
     * @return 随机 double
     */
    public static double randomDouble() {
        return RANDOM.nextDouble();
    }

    /**
     * 生成一个 [min, max) 之间的随机双精度浮点数。
     *
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     * @return 随机 double
     * @throws IllegalArgumentException 当 min >= max 时抛出
     */
    public static double randomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("min 必须小于 max");
        }
        return min + (max - min) * RANDOM.nextDouble();
    }
}
