package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import org.excellent.client.api.annotations.Funtime;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.component.impl.rotation.Rotation;
import org.excellent.client.managers.component.impl.rotation.RotationComponent;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.chat.ChatUtil;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.player.InvUtil;
import org.excellent.lib.util.time.StopWatch;

import java.util.stream.IntStream;

@Funtime
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClanUpgrade", category = Category.MISC)
public class ClanUpgrade extends Module {
    public static ClanUpgrade getInstance() {
        return Instance.get(ClanUpgrade.class);
    }

    private final StopWatch timer = new StopWatch();

    @Override
    public void toggle() {
        super.toggle();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        int slot = InvUtil.getItemInHotBar(Items.TORCH);
        if (mc.world.getDimensionKey().getLocation().getPath().equals("lobby")) {
            if (mc.player.ticksExisted % 200 == 0) ChatUtil.addTextWithError("Тепнитесь на РТП");
            return;
        }
        if (slot == -1) {
            if (mc.player.ticksExisted % 200 == 0) ChatUtil.addTextWithError("Нужен редстоун или факел в хотбаре");
            return;
        }

        BlockPos pos = mc.player.getPosition().down();
        RotationComponent.update(new Rotation(Rotation.cameraYaw(), 90), 360, 360, 0, 75);
        if (mc.world.getBlockState(pos).isSolid()) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
            IntStream.range(0, 4).forEach(i -> {
                mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(false, pos.getVec(), Direction.UP, pos, false)));
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos.up(), Direction.UP));
            });
            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
        }
    }
}
