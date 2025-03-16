package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import org.excellent.client.api.annotations.HolyWorld;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.input.KeyboardPressEvent;
import org.excellent.client.managers.events.input.MousePressEvent;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.events.render.Render2DEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BindSetting;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.other.ViaUtil;
import org.excellent.client.utils.player.InvUtil;
import org.excellent.client.utils.player.PlayerUtil;
import org.excellent.client.utils.render.color.ColorUtil;
import org.excellent.client.utils.render.draw.Project;
import org.excellent.client.utils.render.draw.RectUtil;
import org.excellent.client.utils.rotation.AuraUtil;
import org.excellent.lib.util.time.StopWatch;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import static org.excellent.client.screen.hud.IRenderer.font;
import static org.excellent.client.screen.hud.IRenderer.fontSize;

@HolyWorld
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HolyWorldAssist", category = Category.MISC)
public class HolyWorldAssist extends Module {
    public static HolyWorldAssist getInstance() {
        return Instance.get(HolyWorldAssist.class);
    }

    private final BooleanSetting autoDrochka = new BooleanSetting(this, "Забирать лут у Мобов", false);
    private final BooleanSetting addingBrokenBlocks = new BooleanSetting(this, "Спуф Cломанных Блоков", false);
    private final SliderSetting brokenBlocks = new SliderSetting(this, "Кол-во Блоков", 200, 1, 400, 1).setVisible(addingBrokenBlocks::getValue);
    private final BindSetting keyClipUp = new BindSetting(this, "Клип вверх (лодка)");
    private final Map<Item, BindSetting> keyBindings = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final StopWatch boatTimer = new StopWatch();
    private final StopWatch timer = new StopWatch();

    public HolyWorldAssist() {
        keyBindings.put(Items.PRISMARINE_SHARD, new BindSetting(this, "Кнопка трапки"));
        keyBindings.put(Items.SNOWBALL, new BindSetting(this, "Кнопка снежка"));
        keyBindings.put(Items.FIREWORK_STAR, new BindSetting(this, "Кнопка прощального гуля"));
        keyBindings.put(Items.NETHER_STAR, new BindSetting(this, "Кнопка стана"));
    }

    @EventHandler
    public void onRender2D(Render2DEvent e) {
        if (!PlayerUtil.isHoly() || mc.world.getDimensionKey().getLocation().getPath().contains("spawn_world")) {
            return;
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof VillagerEntity || entity instanceof WanderingTraderEntity) {
                boolean timePassed = timer.finished(29000) && !timer.finished(60000);
                boolean haveLoot = false;
                for (ItemStack stack : entity.getHeldEquipment()) {
                    if (stack.getItem() == Items.EXPERIENCE_BOTTLE || stack.getItem() == Items.TRIPWIRE_HOOK) {
                        haveLoot = true;
                        timer.reset();
                        break;
                    }
                }
                Vector2f vec2f = Project.project2D(Vector3d.copyCentered(entity.getPosition()).add(0, -0.5, 0));
                String str = haveLoot ? "Можно забрать" : timePassed ? "Скоро" : (int) (29 - (timer.elapsedTime() / 1000)) + " сек";
                float posX = vec2f.x;
                float posY = vec2f.y;
                float width = font.getWidth(str, fontSize);
                if (timer.finished(60000)) continue;

                RectUtil.drawRect(e.getMatrix(), posX - width / 2 - 1, posY + 5, width + 2, 8, ColorUtil.getColor(0, 0, 0, 128));
                font.draw(e.getMatrix(), str, posX - width / 2, posY + 5.5f, -1, fontSize);
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (!PlayerUtil.isHoly()) return;
        if (InputMappings.keyPressed(mw.getHandle(), keyClipUp.getValue())) {
            Entity ridingEntity = mc.player.getRidingEntity();
            if (ridingEntity != null && boatTimer.finished(1500)) {
                ridingEntity.setPosition(ridingEntity.getPosX(), ridingEntity.getPosY() + 350, ridingEntity.getPosZ());
                IntStream.range(0, 20).forEach(i -> mc.player.connection.sendPacket(new CMoveVehiclePacket(ridingEntity)));
                mc.player.connection.sendPacket(new CUseEntityPacket(ridingEntity, Hand.MAIN_HAND, false));
                boatTimer.reset();
            }
        }
        if (autoDrochka.getValue()) {
            for (Entity entity : mc.world.getAllEntities()) {
                boolean haveLoot = false;
                for (ItemStack stack : entity.getHeldEquipment()) {
                    if (stack.getItem() == Items.EXPERIENCE_BOTTLE || stack.getItem() == Items.TRIPWIRE_HOOK) {
                        haveLoot = true;
                        break;
                    }
                }
                if (haveLoot && mc.player.getDistance(entity) < 6) {
                    final Vector4f rotation = AuraUtil.calculateRotation(entity);
                    ViaUtil.sendPositionPacket(rotation.x, rotation.y, false);
                    mc.player.connection.sendPacket(new CUseEntityPacket(entity, Hand.MAIN_HAND, mc.player.isSneaking()));
                    ViaUtil.sendPositionPacket(mc.player.rotationYaw, mc.player.rotationPitch, false);
                }
            }
        }
    }

    @EventHandler
    private void onKeyboardPress(KeyboardPressEvent event) {
        if (event.getScreen() != null) return;

        keyBindings.forEach((item, bindSetting) -> {
            if (event.isKey(bindSetting.getValue())) {
                InvUtil.findItemAndThrow(item, mc.player.rotationYaw, mc.player.rotationPitch);
            }
        });
    }

    @EventHandler
    private void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null) return;

        keyBindings.forEach((item, bindSetting) -> {
            if (event.isKey(bindSetting.getValue())) {
                InvUtil.findItemAndThrow(item, mc.player.rotationYaw, mc.player.rotationPitch);
            }
        });
    }
}
