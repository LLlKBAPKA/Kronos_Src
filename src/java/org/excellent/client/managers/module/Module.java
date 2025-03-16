package org.excellent.client.managers.module;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;
import org.excellent.client.Excellent;
import org.excellent.client.api.annotations.Beta;
import org.excellent.client.api.interfaces.IMinecraft;
import org.excellent.client.managers.module.impl.misc.Notifications;
import org.excellent.client.managers.module.impl.render.Hud;
import org.excellent.client.managers.module.settings.Setting;
import org.excellent.client.managers.other.notification.NotificationType;
import org.excellent.client.utils.animation.Animation;
import org.excellent.client.utils.animation.util.Easings;
import org.excellent.client.utils.other.SoundUtil;
import org.excellent.client.utils.render.color.ColorFormatting;
import org.excellent.client.utils.render.color.ColorUtil;
import org.excellent.client.utils.render.font.StripFont;

import java.util.List;

@Data
public abstract class Module implements IMinecraft {
    private final List<Setting<?>> settings = new ObjectArrayList<>();
    private ModuleInfo moduleInfo;
    private String name;
    private Category category;
    private boolean enabled;
    private boolean autoEnabled;
    private boolean allowDisable;
    private boolean hidden;
    private int key;
    private final Animation animation = new Animation();
    private final StripFont stripFont = new StripFont();

    public Module() {
        Class<? extends Module> clazz = this.getClass();
        ModuleInfo moduleInfo = clazz.getAnnotation(ModuleInfo.class);

        if (moduleInfo == null) {
            throw new NotImplementedException("@ModuleInfo annotation not found on " + clazz.getSimpleName());
        }

        this.moduleInfo = moduleInfo;
        this.name = moduleInfo.name().trim().replaceAll(" ", "");
        this.category = moduleInfo.category();
        this.autoEnabled = moduleInfo.autoEnabled();
        this.allowDisable = moduleInfo.allowDisable();
        this.hidden = moduleInfo.hidden();
        this.key = moduleInfo.key();
        setup();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(final boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(final boolean enabled, boolean notification) {
        if (this.enabled == enabled || (!this.allowDisable && !enabled)) {
            return;
        }
        this.enabled = enabled;
        final boolean beta = this.getClass().isAnnotationPresent(Beta.class);
        Notifications notifications = Notifications.getInstance();
        if (!this.isHidden() && notification && notifications.isEnabled() && notifications.sound().getValue()) {
            Excellent.inst().notificationManager().register((beta ? ColorFormatting.getColor(ColorUtil.RED) + "(beta) " + ColorFormatting.reset() : "") + "Модуль" + (enabled ? ColorFormatting.getColor(ColorUtil.overCol(ColorUtil.GREEN, ColorUtil.WHITE)) : ColorFormatting.getColor(ColorUtil.overCol(ColorUtil.RED, ColorUtil.WHITE))) + " " + this.name + ColorFormatting.reset() + " " + (enabled ? "включён" : "выключен"), NotificationType.INFO, 1500);
            SoundUtil.playSound(enabled ? "enabled.wav" : "disabled.wav", notifications.volume().getValue() / 100F);
        }
        if (enabled) {
            superEnable();
        } else {
            superDisable();
        }
        if (Hud.getInstance().checks().getValue("Keybinds") || Hud.getInstance().checks().getValue("ArrayList")) {
            animation.run(enabled ? 1 : 0, 0.5F, Easings.EXPO_OUT, false);
        }
    }

    private void eventEnable() {
        Excellent.eventHandler().subscribe(this);
    }

    private void eventDisable() {
        Excellent.eventHandler().unsubscribe(this);
    }

    private void superEnable() {
        if (mc.player != null) onEnable();
        eventEnable();
    }

    private void superDisable() {
        if (mc.player != null) onDisable();
        eventDisable();
    }

    public void setup() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }
}