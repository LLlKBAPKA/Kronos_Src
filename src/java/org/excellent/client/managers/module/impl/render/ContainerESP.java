package org.excellent.client.managers.module.impl.render;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.api.events.orbit.EventPriority;
import org.excellent.client.managers.events.render.Render3DLastEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.MultiBooleanSetting;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.render.draw.RenderUtil3D;

import java.awt.*;
import java.util.List;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ContainerESP", category = Category.RENDER)
public class ContainerESP extends Module {
    public static ContainerESP getInstance() {
        return Instance.get(ContainerESP.class);
    }

    private final MultiBooleanSetting blocks = new MultiBooleanSetting(this, "Элементы",
            BooleanSetting.of("Сундук", true),
            BooleanSetting.of("Эндер-Сундук", false),
            BooleanSetting.of("Шалкер", false),
            BooleanSetting.of("Бочка", false),
            BooleanSetting.of("Воронка", false),
            BooleanSetting.of("Печка", false)
    );
    private final List<BlockPos> list = Lists.newArrayList();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRender3D(Render3DLastEvent e) {
        for (TileEntity entity : mc.world.loadedTileEntityList) {
            Color color = null;

            if (entity instanceof ChestTileEntity && blocks.getValue("Сундук")) {
                color = new Color(0xFFB327);
            } else if (entity instanceof EnderChestTileEntity && blocks.getValue("Эндер-Сундук")) {
                color = new Color(0x9456FF);
            } else if (entity instanceof BarrelTileEntity && blocks.getValue("Бочка")) {
                color = new Color(0xDC8024);
            } else if (entity instanceof HopperTileEntity && blocks.getValue("Воронка")) {
                color = new Color(0x47484D);
            } else if (entity instanceof FurnaceTileEntity && blocks.getValue("Печка")) {
                color = new Color(0x181818);
            } else if (entity instanceof ShulkerBoxTileEntity && blocks.getValue("Шалкер")) {
                color = new Color(0xF12289);
            }

            AxisAlignedBB box = entity.getBlockState().getShape(mc.world, entity.getPos()).getBoundingBox().offset(entity.getPos());

            if (color == null) continue;
            if (!mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(box)) continue;

            RenderUtil3D.drawBoundingBox(e.getMatrix(), box, color.getRGB(), 1, false, false);
        }
    }
}
