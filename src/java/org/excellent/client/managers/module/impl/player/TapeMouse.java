package org.excellent.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.RayTraceResult;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.ModeSetting;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.other.Instance;
import org.excellent.lib.util.time.StopWatch;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "TapeMouse", category = Category.PLAYER)
public class TapeMouse extends Module {
    public static TapeMouse getInstance() {
        return Instance.get(TapeMouse.class);
    }

    private final BooleanSetting entityRaytrace = new BooleanSetting(this, "Проверка на энтити", false);
    private final ModeSetting attackMode = new ModeSetting(this, "Режим ударов", "По кулдауну", "По задержке");
    private final SliderSetting delay = new SliderSetting(this, "Задержка", 1000, 100, 5000, 100).setVisible(() -> attackMode.is("По задержке"));
    private final StopWatch timerUtil = new StopWatch();

    @Override
    public void toggle() {
        super.toggle();
        resetTimer();
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (entityRaytrace.getValue() && (mc.objectMouseOver == null || !mc.objectMouseOver.getType().equals(RayTraceResult.Type.ENTITY))) {
            return;
        }

        if (attackMode.is("По задержке") && timerUtil.finished(delay.getValue().intValue())) {
            mc.clickMouse();
            timerUtil.reset();
        }

        if (attackMode.is("По кулдауну") && mc.player.getCooledAttackStrength(1F) >= 1F) {
            mc.clickMouse();
        }
    }

    private void resetTimer() {
        timerUtil.reset();
    }
}