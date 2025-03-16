package org.excellent.client.managers.module.impl.client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import org.excellent.client.Excellent;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.input.KeyboardPressEvent;
import org.excellent.client.managers.events.input.MousePressEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.utils.other.Instance;
import org.lwjgl.glfw.GLFW;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClickGui", category = Category.CLIENT, autoEnabled = true, allowDisable = false, key = GLFW.GLFW_KEY_RIGHT_SHIFT)
public class ClickGui extends Module {
    public static ClickGui getInstance() {
        return Instance.get(ClickGui.class);
    }

    private final BooleanSetting bat = new BooleanSetting(this, "Гифка в меню", false);

    @EventHandler
    public void onKey(KeyboardPressEvent event) {
        if (event.getScreen() != null) return;
        if (event.isKey(getKey())) {
            Minecraft.getInstance().displayScreen(Excellent.inst().clickGui());
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null) return;

        if (event.isKey(getKey())) {
            Minecraft.getInstance().displayScreen(Excellent.inst().clickGui());
        }
    }
}