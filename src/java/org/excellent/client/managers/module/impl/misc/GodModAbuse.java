package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.other.PacketEvent;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.player.PlayerUtil;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "GodModAbuse", category = Category.MISC)
public class GodModAbuse extends Module {

    @EventHandler
    public void onPacket(PacketEvent e) {
        IPacket<?> packet = e.getPacket();
        if (PlayerUtil.isPvp() && packet instanceof CPlayerPacket) {

        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.currentScreen instanceof ContainerScreen<?> screen) {

        }
    }
}