package org.excellent.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.AirBlock;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.player.MotionEvent;
import org.excellent.client.managers.events.player.MoveInputEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.player.PlayerUtil;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Eagle", category = Category.PLAYER)
public class Eagle extends Module {
    public static Eagle getInstance() {
        return Instance.get(Eagle.class);
    }

    private final SliderSetting sneakSpeed = new SliderSetting(this, "Скорость приседа", 0.1F, 0.1F, 1, 0.1F);
    private final BooleanSetting groundOnly = new BooleanSetting(this, "Только на земле", false);
    private boolean sneaked;
    private int ticksOverEdge;

    @Override
    protected void onDisable() {
        super.onDisable();
        if (sneaked) {
            sneaked = false;
        }
    }

    @EventHandler
    public void onEvent(MotionEvent event) {
        if (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof AirBlock) {
            if (!sneaked) sneaked = true;
        } else if (sneaked) sneaked = false;
        if (sneaked) mc.gameSettings.keyBindSprint.setPressed(false);
        if (sneaked) ticksOverEdge++;
        else ticksOverEdge = 0;
    }


    @EventHandler
    public void onEvent(MoveInputEvent event) {
        event.setSneaking(sneaked);
        if (sneaked && ticksOverEdge <= 2) event.setSneakSlow(sneakSpeed.getValue().doubleValue());
    }

}