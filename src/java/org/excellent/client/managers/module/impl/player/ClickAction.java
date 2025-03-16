package org.excellent.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.excellent.client.Excellent;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.input.KeyboardPressEvent;
import org.excellent.client.managers.events.input.MousePressEvent;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BindSetting;
import org.excellent.client.utils.chat.ChatUtil;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.player.InvUtil;
import org.excellent.client.utils.player.PlayerUtil;
import org.excellent.common.impl.taskript.Script;
import org.excellent.lib.util.time.StopWatch;

import java.util.HashMap;
import java.util.Map;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClickAction", category = Category.PLAYER)
public class ClickAction extends Module {
    public static ClickAction getInstance() {
        return Instance.get(ClickAction.class);
    }

    private final Map<Item, BindSetting> keyBindings = new HashMap<>();
    private final StopWatch time = new StopWatch();
    private final Script script = new Script();

    public ClickAction() {
        keyBindings.put(Items.ENDER_PEARL, new BindSetting(this, "Кнопка пёрки"));
        keyBindings.put(Items.EXPERIENCE_BOTTLE, new BindSetting(this, "Кнопка опыта"));
        keyBindings.put(Items.CROSSBOW, new BindSetting(this, "Кнопка арбалета"));
        keyBindings.put(Items.AIR, new BindSetting(this, "Добавить в друзья"));
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.currentScreen != null) return;

        keyBindings.forEach((item, bindSetting) -> {
            if (item == Items.EXPERIENCE_BOTTLE && InputMappings.keyPressed(mw.getHandle(), bindSetting.getValue())) {
                if (PlayerUtil.isFuntime()) throwEXP(item);
                else InvUtil.findItemAndThrow(item, mc.player.rotationYaw, 80);
            }
        });
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent event) {
        if (event.getScreen() != null) return;

        keyBindings.forEach((item, bindSetting) -> {
            if (event.isKey(bindSetting.getValue())) {
                performAction(item);
            }
        });
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null) return;

        keyBindings.forEach((item, bindSetting) -> {
            if (event.isKey(bindSetting.getValue())) {
                performAction(item);
            }
        });
    }

    public void performAction(Item item) {
        if (item == Items.CROSSBOW || item == Items.ENDER_PEARL) {
            InvUtil.findItemAndThrow(item, mc.player.rotationYaw, mc.player.rotationPitch);
        } else if (item == Items.AIR) {
            clickFriend();
        }
    }

    public void clickFriend() {
        if (mc.pointedEntity instanceof PlayerEntity player) {
            final String name = TextFormatting.removeFormatting(player.getGameProfile().getName());
            final boolean friend = Excellent.inst().friendManager().isFriend(name);
            if (friend) {
                Excellent.inst().friendManager().removeFriend(name);
                ChatUtil.addText(TextFormatting.RED + "\"" + name + "\" удалён из списка друзей.");
            } else {
                Excellent.inst().friendManager().addFriend(name);
                ChatUtil.addText(TextFormatting.GREEN + "\"" + name + "\" добавлен в список друзей.");
            }
        }
    }

    public void throwEXP(Item item) {
        Slot slot = InvUtil.getInventorySlot(item);
        if (slot == null) {
            if (time.finished(2000)) {
                ChatUtil.addTextWithError("Нету опыта");
                time.reset();
            }
            return;
        }

        if (slot.slotNumber >= 36) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot.slotNumber));
            InvUtil.useItem(Hand.MAIN_HAND, mc.player.rotationYaw, 80);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
        } else if (InvUtil.noEmptyHotBarSlots() == 9) {
            mc.player.connection.sendPacket(new CPickItemPacket(slot.slotNumber));
            InvUtil.useItem(Hand.MAIN_HAND, mc.player.rotationYaw, 80);
            mc.player.connection.sendPacket(new CPickItemPacket(slot.slotNumber));
        }
    }
}
