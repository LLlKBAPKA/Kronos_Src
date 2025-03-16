package org.excellent.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.api.events.orbit.EventPriority;
import org.excellent.client.managers.component.impl.rotation.Rotation;
import org.excellent.client.managers.component.impl.rotation.RotationComponent;
import org.excellent.client.managers.component.impl.rotation.SmoothRotationComponent;
import org.excellent.client.managers.component.impl.target.TargetComponent;
import org.excellent.client.managers.events.other.GameUpdateEvent;
import org.excellent.client.managers.events.other.PacketEvent;
import org.excellent.client.managers.events.player.MoveEvent;
import org.excellent.client.managers.events.player.MoveInputEvent;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.events.render.Render2DEvent;
import org.excellent.client.managers.events.world.WorldChangeEvent;
import org.excellent.client.managers.events.world.WorldLoadEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.ModeSetting;
import org.excellent.client.managers.module.settings.impl.MultiBooleanSetting;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.chat.ChatUtil;
import org.excellent.client.utils.math.Interpolator;
import org.excellent.client.utils.math.PerfectDelay;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.other.ViaUtil;
import org.excellent.client.utils.player.InvUtil;
import org.excellent.client.utils.player.PlayerUtil;
import org.excellent.client.utils.render.color.ColorUtil;
import org.excellent.client.utils.render.draw.RenderUtil;
import org.excellent.client.utils.render.draw.Round;
import org.excellent.client.utils.rotation.AuraUtil;
import org.excellent.client.utils.rotation.RayTraceUtil;
import org.excellent.client.utils.rotation.RotationUtil;
import org.excellent.common.impl.fastrandom.FastRandom;
import org.excellent.common.impl.taskript.Script;
import org.excellent.lib.util.time.StopWatch;
import org.joml.Vector4f;

import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "KillAura", category = Category.COMBAT)
public class KillAura extends Module {

    public static KillAura getInstance() {
        return Instance.get(KillAura.class);
    }

    private final ModeSetting componentMode = new ModeSetting(this, "Тип наводки", "Обычный", "Плавный", "Грим", "Фантайм");

    private final SliderSetting fov = new SliderSetting(this, "Угол обзора", 70F, 30F, 180F, 1F).setVisible(() -> !componentMode.is("Грим"));
    private final SliderSetting attackRange = new SliderSetting(this, "Дистанция", 3F, 3F, 6F, 0.1F);
    private final SliderSetting preRange = new SliderSetting(this, "Доп дистанция", 1F, 0F, 3F, 0.1F).setVisible(() -> !componentMode.is("Грим"));

    private final BooleanSetting onlyCrits = new BooleanSetting(this, "Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting(this, "Умные криты", false).setVisible(onlyCrits::getValue);

    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Прочее",
            BooleanSetting.of("Таргет есп", false),
            BooleanSetting.of("Отображать FOV", false).setVisible(() -> !componentMode.is("Грим")),
            BooleanSetting.of("Не бить когда ешь", false),
            BooleanSetting.of("Бить только с оружием", false),
            BooleanSetting.of("Выключить после смерти", true)
    );

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final PerfectDelay perfectDelay = new PerfectDelay();
    private final StopWatch stopWatch = new StopWatch();
    private final Script script = new Script();
    public LivingEntity target;
    private boolean canCrit;

    @Override
    public void toggle() {
        super.toggle();
        reset();
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (packet instanceof CHeldItemChangePacket) {
            perfectDelay.reset(550L);
        } else if (packet instanceof CAnimateHandPacket) {
            perfectDelay.reset(450L);
        }
    }

    int p;

    @EventHandler
    public void onInput(MoveInputEvent e) {
        if (p > 0) {
            e.setForward(0);
            p--;
        }
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        reset();
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        reset();
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        if (target == null || !checks.getValue("Отображать FOV")) return;

        MatrixStack matrix = event.getMatrix();

        float baseFov = this.fov.getValue();
        float calcFov = (float) AuraUtil.calculateFOVFromCamera(target);

        float centerX = mw.getScaledWidth() / 2F;
        float centerY = mw.getScaledHeight() / 2F;

        boolean allow = calcFov < baseFov;
        float delta = Math.max(5F, calcFov - baseFov);

        if (!allow) {
            float percent = delta / (fov.max - baseFov);
            int calcColor = ColorUtil.multAlpha(ColorUtil.RED, 1F - percent);
            RenderUtil.Rounded.roundedOutline(matrix, centerX - calcFov, centerY - calcFov, calcFov * 2F, calcFov * 2F, delta, calcColor, Round.of(calcFov - delta));
        }
        int baseColor = allow ? ColorUtil.GREEN : ColorUtil.RED;
        float outline = allow ? 5F : delta;
        RenderUtil.Rounded.roundedOutline(matrix, centerX - baseFov, centerY - baseFov, baseFov * 2F, baseFov * 2F, outline, baseColor, Round.of(baseFov - outline));
    }

    @EventHandler
    public void onEvent(MoveEvent event) {
        if (target == null || mc.player == null || mc.world == null) {
            canCrit = false;
            return;
        }
        final boolean fallCheck = mc.player.nextFallDistance != 0F;
        canCrit = !event.isToGround() && event.getFrom().y > event.getTo().y && fallCheck;
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        script.update();
        if (!ViaUtil.allowedBypass() && componentMode.is("Грим")) {
            ChatUtil.addTextWithError("Нужно зайти на сервер с версии 1.17 и выше!");
            toggle();
            return;
        }
        if (checks.getValue("Выключить после смерти") && !mc.player.isAlive()) {
            toggle();
            return;
        }

        updateTarget();

        if (target == null || mc.player == null || mc.world == null) {
            reset();
            return;
        }

        if (checkReturn() || shieldBreaker()) return;

        updateAttack();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(GameUpdateEvent event) {
        if (target == null || mc.player == null || mc.world == null) {
            reset();
            return;
        }

        if (checkReturn()) return;

        updateRotation();
    }

    private Vector2f lerpRotation = Vector2f.ZERO;
    private int count;

    private void updateRotation() {
        double maxHeight = (AuraUtil.getStrictDistance((target)) / attackDistance());
        Vector3d vec = target.getPositionVec()
                .add(0, MathHelper.clamp(mc.player.getEyePosition(mc.getRenderPartialTicks()).y - target.getPosY(), 0, maxHeight), 0)
                .subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()))
                .normalize();

        float rawYaw = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
        float rawPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))), -90F, 90F);

        float speed = new SecureRandom().nextBoolean() ? randomLerp(0.3F, 0.4F) : randomLerp(0.5F, 0.6F);

        float cos = (float) Math.cos(System.currentTimeMillis() / 100D);
        float sin = (float) Math.sin(System.currentTimeMillis() / 100D);
        float yaw = (float) Math.ceil(randomLerp(6F, 12) * cos + (1F - cooldownFromLastSwing()) * (randomLerp(60, 90) * (count == 0 ? 1 : -1)));
        float pitch = (float) Math.ceil(randomLerp(6F, 12) * sin + (1F - cooldownFromLastSwing()) * (randomLerp(15, 45) * (count == 0 ? 1 : -1)));

        lerpRotation = new Vector2f(wrapLerp(speed, MathHelper.wrapDegrees(lerpRotation.x), MathHelper.wrapDegrees(rawYaw + yaw)), wrapLerp(speed / 2F, lerpRotation.y, MathHelper.clamp(rawPitch + pitch, -90F, 90F)));

        Rotation rotation = new Rotation(mc.player.rotationYaw + (float) Math.ceil(MathHelper.wrapDegrees(lerpRotation.x) - MathHelper.wrapDegrees(mc.player.rotationYaw)), mc.player.rotationPitch + (float) Math.ceil(MathHelper.wrapDegrees(lerpRotation.y) - MathHelper.wrapDegrees(mc.player.rotationPitch)));

        float fov = (float) AuraUtil.calculateFOVFromCamera(target);
        float baseFov = this.fov.getValue();

        boolean toFast = cooldownFromLastSwing() > 0.5F;
        if (Math.abs(fov) < baseFov) {
            if (componentMode.is("Плавный")) {
                SmoothRotationComponent.update(rotation, toFast || rayTrace() ? new FastRandom().nextFloat() : 3F, 10F, 3F, 3F, 1, 5, false);
            }
            if (componentMode.is("Обычный")) {
                RotationComponent.update(rotation, toFast || rayTrace() ? baseFov : (1F - (fov / baseFov)) * baseFov, 20, 1, 5);
            }
        }
    }

    public float wrapLerp(float step, float input, float target) {
        return input + step * MathHelper.wrapDegrees(target - input);
    }

    public float randomLerp(float min, float max) {
        return Interpolator.lerp(max, min, new SecureRandom().nextFloat());
    }

    public float cooldownFromLastSwing() {
        return MathHelper.clamp(mc.player.ticksSinceLastSwing / randomLerp(8, 12), 0.0F, 1.0F);
    }

    private void updateAttack() {
        if (shouldAttack() && rayTrace() && AuraUtil.getStrictDistance(target) < attackDistance()) {
            boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
            boolean sprinting = mc.player.isSprinting();
            if (!isInLiquid && sprinting) {
//                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
//                mc.player.setServerSprintState(false);
//                mc.player.setSprinting(false);
                p = 1;
                if (mc.player.isServerSprintState()) return;
            }
            if (componentMode.is("Фантайм")) {
                Vector2f vec2f = RotationUtil.calculate(target);
                RotationComponent.update(new Rotation(vec2f.x, vec2f.y), 360, 360, 0, 5);
            }
            attackEntity(target);
            count = (count + 1) % 2;
            canCrit = false;
            stopWatch.reset();
        } else if (!mc.player.canEntityBeSeen(target) && PlayerUtil.isHoly()) {
            RotationComponent.update(new Rotation(Rotation.cameraYaw(), 90), 360, 360, 0, 5);
        }
    }

    private boolean shieldBreaker() {
        Slot axeSlot = InvUtil.getAxeSlot();
        if (target.getActiveItemStack().getItem() == Items.SHIELD && axeSlot != null && rayTrace()) {
            InvUtil.clickSlot(axeSlot, mc.player.inventory.currentItem, ClickType.SWAP, true);
            attackEntity(target);
            InvUtil.clickSlot(axeSlot, mc.player.inventory.currentItem, ClickType.SWAP, true);
            return true;
        }
        return false;
    }

    private boolean checkReturn() {
        return mc.player.isHandActive() && checks.getValue("Не бить когда ешь") || (!(mc.player.getHeldItemMainhand().getItem() instanceof AxeItem || mc.player.getHeldItemMainhand().getItem() instanceof SwordItem) && checks.getValue("Бить только с оружием"));
    }

    private void attackEntity(Entity entity) {
        rotateGrim(true);
        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(Hand.MAIN_HAND);
        script.cleanup().addTickStep(0, () -> rotateGrim(false));
    }

    private void updateTarget() {
        target = TargetComponent.getTarget(attackRange.getValue() + preRange.getValue());
    }

    public void rotateGrim(boolean start) {
        if (componentMode.is("Грим")) {
            if (start) {
                final Vector4f rotation = AuraUtil.calculateRotation(target);
                float yaw = rotation.x + randomLerp(-0.02f, 0.02f);
                float pitch = rotation.y + randomLerp(-0.02f, 0.02f);
                float yOffset = Criticals.getInstance().isEnabled() && !mc.player.isOnGround() ? 1e-6f : 0f;
                ViaUtil.sendPositionPacket(mc.player.getPosX(), mc.player.getPosY() - yOffset, mc.player.getPosZ(), yaw, pitch, mc.player.isOnGround());
            } else {
                ViaUtil.sendPositionPacket(mc.player.rotationYaw, mc.player.rotationPitch, false);
            }
        }
    }

    private boolean shouldAttack() {
        if (!perfectDelay.cooldownComplete() || !cooldownComplete()) return false;

        boolean isBlockAboveHead = PlayerUtil.isBlockAboveHead();

        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);

        if (isInLiquid) return true;

        boolean canDefaultCrit = (mc.player.fallDistance > 0 && canCrit && isBlockAboveHead) || (Criticals.getInstance().isEnabled() && !mc.player.isOnGround());
        boolean canSmartCrit = isBlockAboveHead && !mc.player.movementInput.jump && !canCrit && mc.player.isOnGround() && mc.player.collidedVertically;

        if (onlyCrits.getValue() && !smartCrits.getValue()) {
            return shouldCritical() && canDefaultCrit;
        }

        if (smartCrits.getValue()) {
            return (shouldCritical() || canDefaultCrit) || canSmartCrit;
        }

        return true;
    }

    private boolean shouldCritical() {
        boolean isDeBuffed = mc.player.isPotionActive(Effects.LEVITATION) || mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isPotionActive(Effects.SLOW_FALLING);
        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
        boolean isFlying = mc.player.abilities.isFlying || mc.player.isElytraFlying();
        boolean isClimbing = mc.player.isOnLadder();
        boolean isCantJump = mc.player.isPassenger();
        boolean isOnWeb = PlayerUtil.isPlayerInWeb();

        return !(isDeBuffed || isInLiquid || isFlying || isClimbing || isCantJump || isOnWeb);
    }

    public boolean cooldownComplete() {
        return mc.player.getCooledAttackStrength(1.5F) >= 0.93F;
    }

    public boolean rayTrace() {
        return (RayTraceUtil.rayTraceEntity(mc.player.rotationYaw, mc.player.rotationPitch, attackDistance(), target) || componentMode.is("Грим") || componentMode.is("Фантайм"));
    }

    public double attackDistance() {
        return Math.max(mc.playerController.extendedReach() ? 6.0D : 3.0D, attackRange.getValue());
    }

    private void reset() {
        TargetComponent.clearTarget();
        TargetComponent.updateTargetList();
        target = null;
        canCrit = false;
    }
}