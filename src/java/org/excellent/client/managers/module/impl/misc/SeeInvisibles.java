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

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SeeInvisibles", category = Category.MISC)
public class SeeInvisibles extends Module {
    public static SeeInvisibles getInstance() {
        return Instance.get(SeeInvisibles.class);
    }

    private final SliderSetting alpha = new SliderSetting(this, "Прозрачность", 0.5F, 0.0F, 1.0F, 0.1F);
}