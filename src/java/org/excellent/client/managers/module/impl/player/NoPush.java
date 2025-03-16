package org.excellent.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.MultiBooleanSetting;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoPush", category = Category.PLAYER)
public class NoPush extends Module {
    public static NoPush getInstance() {
        return Instance.get(NoPush.class);
    }

    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Не отталкиваться от",
            BooleanSetting.of("Игроков", true),
            BooleanSetting.of("Блоков", true),
            BooleanSetting.of("Воды", true)
    );
}
