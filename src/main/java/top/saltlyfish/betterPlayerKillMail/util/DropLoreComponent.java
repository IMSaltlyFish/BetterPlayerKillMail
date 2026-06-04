package top.saltlyfish.betterPlayerKillMail.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public final class DropLoreComponent {

    public static Component of(ItemStack itemStack, int amount) {
        Component itemComponent = getLocalizedComponent(itemStack);
        return Component.text("- ")
                .append(itemComponent)
                .append(Component.text(": "))
                .append(Component.text(amount));
    }

    private static Component getLocalizedComponent(ItemStack itemStack) {
        // 判断是物品还是方块（简单处理，大多数情况下用 item. 前缀即可）
        // 获取物品的 "item_name" 组件，这通常包含了本地化信息
        Component translatable = itemStack.getData(DataComponentTypes.ITEM_NAME);
        if (translatable != null) {
            return translatable;
        }
        // 降级方案：手动构造翻译键
        return itemStack.displayName();
    }
}