package org.excellent.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CCloseWindowPacket;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.other.PacketEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "XCarry", category = Category.PLAYER)
public class XCarry extends Module {
    public static XCarry getInstance() {
        return Instance.get(XCarry.class);
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        if (mc.player == null) return;

        if (event.getPacket() instanceof CCloseWindowPacket) {
            event.cancel();
        }
    }
}