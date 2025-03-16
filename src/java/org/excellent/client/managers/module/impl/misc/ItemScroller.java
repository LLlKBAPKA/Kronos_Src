package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.other.Instance;
import org.excellent.lib.util.time.StopWatch;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ItemScroller", category = Category.MISC)
public class ItemScroller extends Module {
    public static ItemScroller getInstance() {
        return Instance.get(ItemScroller.class);
    }

    private final SliderSetting delay = new SliderSetting(this, "Задержка", 100, 0, 1000, 1);
    private final StopWatch time = new StopWatch();
}