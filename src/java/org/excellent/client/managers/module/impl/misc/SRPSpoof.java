package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.other.PacketEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SRPSpoof", category = Category.MISC)
public class SRPSpoof extends Module {
    public static SRPSpoof getInstance() {
        return Instance.get(SRPSpoof.class);
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        if (event.getPacket() instanceof CResourcePackStatusPacket wrapper) {
            wrapper.action = CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED;
        }
    }
}