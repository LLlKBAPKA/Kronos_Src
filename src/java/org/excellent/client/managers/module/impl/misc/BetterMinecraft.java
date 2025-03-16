package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "BetterMinecraft", category = Category.MISC)
public class BetterMinecraft extends Module {
    public static BetterMinecraft getInstance() {
        return Instance.get(BetterMinecraft.class);
    }

    private final BooleanSetting simpleChat = new BooleanSetting(this, "Простой чат", true);
    private final BooleanSetting chatHistory = new BooleanSetting(this, "История чата", true);
    private final BooleanSetting antiSpam = new BooleanSetting(this, "АнтиСпам в чате", true);
}