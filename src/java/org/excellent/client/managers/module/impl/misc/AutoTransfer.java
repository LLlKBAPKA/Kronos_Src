package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import org.excellent.client.api.annotations.Funtime;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.StringSetting;
import org.excellent.client.utils.chat.ChatUtil;
import org.excellent.client.utils.math.Mathf;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.player.PlayerUtil;
import org.excellent.common.impl.taskript.ScriptManager;

@Funtime
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoTransfer", category = Category.MISC)
public class AutoTransfer extends Module {
    public static AutoTransfer getInstance() {
        return Instance.get(AutoTransfer.class);
    }

    private final StringSetting anarchy = new StringSetting(this, "Анархия", "101", true);
    private final StringSetting sellCommand = new StringSetting(this, "Команда продажи", "/ah dsell 10");

    private final ScriptManager scripts = new ScriptManager();
    private int serverAnarchy = -1;

    @Override
    protected void onEnable() {
        super.onEnable();
        serverAnarchy = PlayerUtil.getAnarchy();

        try {
            if (serverAnarchy == Integer.parseInt(anarchy.getValue())) {
                ChatUtil.addText("Вы не можете перенести на ту же анархию в какой и находитесь.");
                toggle();
                return;
            }
        } catch (NumberFormatException ignored) {
        }

        if (!isInAnarchy()) {
            ChatUtil.addText("Нужно находится на сервере анархии.");
            toggle();
            return;
        }
        int delay = 20 * 10;
        if (mc.player.ticksExisted < delay) {
            ChatUtil.addText("Подождите " + Mathf.round((delay - mc.player.ticksExisted) / 20F, 1) + " секунд.");
            toggle();
            return;
        }

        scripts.getScript("seller-task").ifPresent(script -> {
            script.cleanup()
                    .addStep(0, () -> mc.player.inventory.currentItem = 0)
                    .addStep(250, () -> {
                        ChatUtil.sendText(sellCommand.getValue());
                    }, () -> !mc.player.inventory.getStackInSlot(0).isEmpty());
            if (mc.player.inventory.getStackInSlot(0).isEmpty()) {
                script.addStep(250, () -> {
                    for (int slotIndex = mc.player.inventory.mainInventory.size() - 9; slotIndex < mc.player.inventory.mainInventory.size(); slotIndex++) {
                        Slot slot = mc.player.container.getSlot(slotIndex + 9);
                        if (slot.getStack().isEmpty()) continue;
                        if (mc.player.inventory.getStackInSlot(0).isEmpty() && slot.slotNumber != 0) {
                            mc.playerController.windowClick(mc.player.container.windowId, slot.slotNumber, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(mc.player.container.windowId, mc.player.inventory.mainInventory.size(), 0, ClickType.PICKUP, mc.player);
                        }
                    }
                    for (int slotIndex = 0; slotIndex < mc.player.inventory.mainInventory.size() - 9; slotIndex++) {
                        Slot slot = mc.player.container.getSlot(slotIndex + 9);
                        if (slot.getStack().isEmpty()) continue;
                        if (mc.player.inventory.getStackInSlot(0).isEmpty() && slot.slotNumber != 0) {
                            mc.playerController.windowClick(mc.player.container.windowId, slot.slotNumber, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(mc.player.container.windowId, mc.player.inventory.mainInventory.size(), 0, ClickType.PICKUP, mc.player);
                        }
                    }
                }, () -> mc.player.inventory.getStackInSlot(0).isEmpty(), 10);
            }
            script.addStep(250, () -> ChatUtil.sendText("/anarchy" + anarchy.getValue()))
                    .addStep(250, () -> scripts.getScript("buy-auc-task")
                            .ifPresent(buyTask ->
                                    buyTask.cleanup()
                                            .addStep(100, () -> ChatUtil.sendText("/ah " + mc.session.getProfile().getName()))
                                            .addStep(100, () -> {
                                                if (mc.currentScreen instanceof ContainerScreen<?> container) {
                                                    String title = container.getTitle().getString().toLowerCase();
                                                    if (title.contains("аукционы") && title.contains(mc.session.getProfile().getName().toLowerCase())) {
                                                        if (container.getContainer() instanceof ChestContainer chest) {
                                                            scripts.getScript("window-click-task")
                                                                    .ifPresent(buytask -> {
                                                                        buytask.cleanup()
                                                                                .addStep(100, () -> mc.playerController.windowClick(chest.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player))
                                                                                .addStep(100, () -> {
                                                                                    mc.player.closeScreen();
                                                                                    toggle();
                                                                                });
                                                                    });
                                                        }
                                                    }
                                                }
                                            }, () -> {
                                                if (mc.currentScreen instanceof ContainerScreen<?> container) {
                                                    String title = container.getTitle().getString().toLowerCase();
                                                    if (title.contains("аукционы") && title.contains(mc.session.getProfile().getName().toLowerCase())) {
                                                        return container.getContainer() instanceof ChestContainer;
                                                    }
                                                }
                                                return false;
                                            })), () -> isInAnarchy() && serverAnarchy != PlayerUtil.getAnarchy() || isInAnarchy())
            ;
        });
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        scripts.updateAll();
    }

    private boolean isInAnarchy() {
        return PlayerUtil.getAnarchy() != -1;
    }
}
