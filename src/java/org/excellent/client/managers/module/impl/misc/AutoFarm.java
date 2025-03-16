package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.component.impl.rotation.Rotation;
import org.excellent.client.managers.component.impl.rotation.RotationComponent;
import org.excellent.client.managers.events.player.StopUseItemEvent;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.autobuy.AutoBuyUtils;
import org.excellent.client.utils.player.InvUtil;
import org.excellent.client.utils.player.MoveUtil;
import org.excellent.lib.util.time.StopWatch;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoFarm", category = Category.MISC)
public class AutoFarm extends Module {
    private final StopWatch watchClose = new StopWatch();
    private final StopWatch watchOther = new StopWatch();
    private boolean autoRepair;

    @Override
    public void toggle() {
        super.toggle();
        autoRepair = false;
    }

    @EventHandler
    public void onStopUseItem(StopUseItemEvent e) {
        e.setCancelled(mc.player.getFoodStats().needFood());
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        List<Item> hoeItems = List.of(Items.NETHERITE_HOE, Items.DIAMOND_HOE);
        List<Item> plantsItems = List.of(Items.CARROT, Items.POTATO);
        Slot expSlot = InvUtil.getInventorySlot(Items.EXPERIENCE_BOTTLE);
        Slot plantSlot = InvUtil.getInventorySlot(plantsItems);
        Slot hoeSlot = InvUtil.getInventorySlot(hoeItems);
        Slot foodSlot = InvUtil.getFoodMaxSaturationSlot();
        Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
        Item offHandItem = mc.player.getHeldItemOffhand().getItem();
        if (hoeSlot == null || MoveUtil.isMoving() || !watchClose.finished(400)) return;
        float itemStrength = 1 - MathHelper.clamp((float) hoeSlot.getStack().getDamage() / (float) hoeSlot.getStack().getMaxDamage(), 0, 1);
        autoRepair = itemStrength < 0.05 || itemStrength != 1 && autoRepair;

        RotationComponent.update(new Rotation(Rotation.cameraYaw(), 90), 360, 360, 0, 5);
        if (mc.player.getFoodStats().needFood() && foodSlot != null) {
            if (!offHandItem.equals(foodSlot.getStack().getItem()) && !containerScreen()) {
                InvUtil.swapHand(foodSlot, Hand.OFF_HAND, false);
            } else {
                mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
            }
        } else if (mc.player.inventory.getFirstEmptyStack() == -1) {
            if (!plantsItems.contains(offHandItem) && !containerScreen()) {
                InvUtil.swapHand(plantSlot, Hand.OFF_HAND, false);
            }
            if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                if (screen.getTitle().getString().equals("● Выберите секцию")) {
                    InvUtil.clickSlotId(21, 0, ClickType.PICKUP, true);
                    return;
                }
                if (screen.getTitle().getString().equals("Скупщик еды")) {
                    InvUtil.clickSlotId(offHandItem.equals(Items.CARROT) ? 10 : 11, 0, ClickType.PICKUP, true);
                    return;
                }
            }
            if (watchOther.every(1000)) mc.player.sendChatMessage("/buyer");
        } else if (autoRepair) {
            if (InvUtil.getInventoryCount(Items.EXPERIENCE_BOTTLE) > hoeSlot.getStack().getDamage() / 6.5) {
                if (containerScreen()) return;
                if (!offHandItem.equals(Items.EXPERIENCE_BOTTLE)) InvUtil.swapHand(expSlot, Hand.OFF_HAND, false);
                if (!hoeItems.contains(mainHandItem)) InvUtil.swapHand(hoeSlot, Hand.MAIN_HAND, false);

                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                watchOther.setMs(500);
            } else if (watchOther.finished(1000)) {
                if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                    if (screen.getTitle().getString().contains("Пузырек опыта")) {
                        mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getTag() != null && s.slotNumber < 45)
                                .min(Comparator.comparingInt(s -> AutoBuyUtils.getPrice(s.getStack()) / s.getStack().getCount()))
                                .ifPresent(s -> InvUtil.clickSlot(s, 0, ClickType.QUICK_MOVE, true));
                        watchOther.setMs(500);
                        return;
                    } else if (screen.getTitle().getString().contains("Подозрительная цена")) {
                        InvUtil.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
                        watchOther.setMs(500);
                        return;
                    }
                }
                mc.player.sendChatMessage("/ah search Пузырёк Опыта");
                watchOther.reset();
            }
        } else if (watchOther.finished(500)) {
            BlockPos pos = mc.player.getPosition();
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.FARMLAND)) {
                if (hoeItems.contains(mainHandItem) && plantsItems.contains(offHandItem)) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.OFF_HAND, new BlockRayTraceResult(pos.getVec(), Direction.UP, pos, false)));
                    IntStream.range(0, 3).forEach(i -> mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(pos.getVec(), Direction.UP, pos.up(), false))));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos.up(), Direction.UP));
                } else if (!containerScreen()) {
                    if (!plantsItems.contains(offHandItem)) InvUtil.swapHand(plantSlot, Hand.OFF_HAND, false);
                    if (!hoeItems.contains(mainHandItem)) InvUtil.swapHand(hoeSlot, Hand.MAIN_HAND, false);
                }
            }
        }
    }

    public boolean containerScreen() {
        if (mc.currentScreen instanceof ContainerScreen<?>) {
            mc.player.closeScreen();
            watchClose.reset();
            return true;
        }
        return false;
    }
}
