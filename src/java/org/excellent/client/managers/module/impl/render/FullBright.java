package org.excellent.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.ModeSetting;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FullBright", category = Category.RENDER)
public class FullBright extends Module {
    public static FullBright getInstance() {
        return Instance.get(FullBright.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Гамма", "Ночное Зрение");
    
}