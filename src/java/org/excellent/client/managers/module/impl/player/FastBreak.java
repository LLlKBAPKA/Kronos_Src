package org.excellent.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.math.BlockRayTraceResult;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.other.Instance;


@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FastBreak", category = Category.PLAYER)
public class FastBreak extends Module {
    public static FastBreak getInstance() {
        return Instance.get(FastBreak.class);
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.objectMouseOver instanceof BlockRayTraceResult result && mc.playerController.curBlockDamageMP > 0.5) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, result.getPos(), result.getFace()));
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, result.getPos(), result.getFace()));
        }
    }
}
