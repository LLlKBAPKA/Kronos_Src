package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Notifications", category = Category.MISC)
public class Notifications extends Module {
    public static Notifications getInstance() {
        return Instance.get(Notifications.class);
    }

    private final BooleanSetting sound = new BooleanSetting(this, "Звук", true);
    private final SliderSetting volume = new SliderSetting(this, "Громкость", 50, 1, 100, 1);
}
