package org.excellent.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.excellent.client.Excellent;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.api.events.orbit.EventPriority;
import org.excellent.client.managers.component.impl.target.TargetComponent;
import org.excellent.client.managers.events.other.PacketEvent;
import org.excellent.client.managers.events.render.Render2DEvent;
import org.excellent.client.managers.events.render.RenderNameEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.impl.combat.AntiBot;
import org.excellent.client.managers.module.impl.misc.Globals;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.MultiBooleanSetting;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.player.PlayerUtil;
import org.excellent.client.utils.render.color.ColorFormatting;
import org.excellent.client.utils.render.color.ColorUtil;
import org.excellent.client.utils.render.draw.GLUtil;
import org.excellent.client.utils.render.draw.Project;
import org.excellent.client.utils.render.draw.RectUtil;
import org.excellent.client.utils.render.draw.RenderUtil3D;
import org.excellent.client.utils.render.font.Font;
import org.excellent.client.utils.render.font.Fonts;
import org.excellent.common.impl.globals.ClientAPI;
import org.excellent.common.impl.globals.Converter;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Nametags", category = Category.RENDER)
public class Nametags extends Module {
    public static Nametags getInstance() {
        return Instance.get(Nametags.class);
    }

    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Элементы",
            BooleanSetting.of("Игроки", true),
            BooleanSetting.of("Предметы", false)
    );

    private final SliderSetting fontSize = new SliderSetting(this, "Размер шрифта", 8F, 6F, 12F, 0.1F);
    private final BooleanSetting optimized = new BooleanSetting(this, "Оптимизировать", true);
    private final BooleanSetting renderDonat = new BooleanSetting(this, "Отображать донат", true);
    private final BooleanSetting showArmor = new BooleanSetting(this, "Отображать броню", true);
    private final int black = ColorUtil.getColor(0, 128);
    private final Font font = Fonts.SF_MEDIUM;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final int timeTick = 600;

    @EventHandler
    public void onRenderName(RenderNameEvent event) {
        if (event.getEntity() instanceof AbstractClientPlayerEntity) event.cancel();
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (PlayerUtil.nullCheck()) return;
        IPacket<?> packet = e.getPacket();
        if (packet instanceof SEntityStatusPacket entityStatus) {
            Entity entity = entityStatus.getEntity(mc.world);
            if (entity instanceof PlayerEntity player && (entityStatus.getOpCode() == 2 || entityStatus.getOpCode() == 33)) {
                if (!(player.prevFallDistance > 3 && player.fallDistance == 0)) {
                    for (Entity ent : mc.world.getAllEntities()) {
                        if (ent instanceof MobEntity mob && mob.getLastAttackedEntity() == player && mob.getLastAttackedEntityTime() < 1000) {
                            return;
                        }
                    }
                    player.addPotionEffect(new EffectInstance(Effects.UNLUCK, timeTick, -1));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRender2D(Render2DEvent event) {
        float fontHeight = fontSize.getValue();

        TargetComponent.getTargets(128, this::isValid, false)
                .forEach(entity -> renderNametag(entity, event.getMatrix(), fontHeight));
        if (checks.getValue("Предметы")) {
            mc.world.loadedItemEntityList().forEach(entity -> renderNametag(entity, event.getMatrix(), fontHeight));
        }
    }

    private void renderNametag(Entity entity, MatrixStack matrix, float fontHeight) {
        Vector3d interpolated = RenderUtil3D.interpolate(entity, mc.getRenderPartialTicks());
        AxisAlignedBB aabb = getEntityBox(entity, interpolated);

        Vector2f min = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
        Vector2f max = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);

        for (Vector3d vector : aabb.getCorners()) {
            Vector2f vec = Project.project2D(vector);
            if (vec.x != Float.MAX_VALUE && vec.y != Float.MAX_VALUE) {
                min.x = Math.min(min.x, vec.x);
                min.y = Math.min(min.y, vec.y);
                max.x = Math.max(max.x, vec.x);
                max.y = Math.max(max.y, vec.y);
            }
        }
        Vector4f position = new Vector4f(min.x, min.y, max.x, max.y);

        float x = position.x;
        float y = position.y - fontHeight;
        float width = position.z - x;
        float height = position.w - y;
        float centerX = x + (width / 2F);
        ITextComponent tagComponent = null;
        float nameWidth = 0;
        float scale = 0.5F;

        if (checks.getValue("Игроки") && entity instanceof PlayerEntity player) {
            tagComponent = createPlayerTagComponent(player);
            nameWidth = getWidth(tagComponent);

            if (showArmor().getValue()) {
                drawArmor(matrix, player, x, y, width, fontHeight);
            }
        } else if (checks.getValue("Предметы") && entity instanceof ItemEntity item) {
            tagComponent = new StringTextComponent("").append(item.getItem().getDisplayName());
            if (item.getItem().getCount() > 1) {
                String GRAY = ColorFormatting.getColor(TextFormatting.GRAY.getColor());
                tagComponent = tagComponent.deepCopy().appendString(GRAY + " " + item.getItem().getCount() + "x");
            }
            nameWidth = getWidth(tagComponent);
        }

        if (tagComponent != null) {
            drawTag(matrix, tagComponent, centerX, y, nameWidth, fontHeight, scale);

            if (checks.getValue("Игроки") && entity instanceof PlayerEntity player && Globals.getInstance().isEnabled()) {
                String client = ClientAPI.getClient(entity.getName().getString());
                if (client != null) {
                    String userImageURL = "https://rockstar.moscow/api/globals/" + client + ".png";
                    mc.getTextureManager().bindTexture(Converter.getResourceLocation(userImageURL));
                    float size = fontHeight + 1;
                    RectUtil.drawRect(matrix, centerX - nameWidth / 2F - size, y - 0.5f, size, size, -1, false, true);
                }
            }
        }
    }

    private ITextComponent createPlayerTagComponent(PlayerEntity player) {
        ItemStack offHandStack = player.getHeldItemOffhand();
        CompoundNBT offHandNBT = offHandStack.getTag();
        String RED = ColorFormatting.getColor(TextFormatting.RED.getColor());
        String GREEN = ColorFormatting.getColor(TextFormatting.GREEN.getColor());
        String DARK_RED = ColorFormatting.getColor(TextFormatting.DARK_RED.getColor());
        String GOLD = ColorFormatting.getColor(TextFormatting.GOLD.getColor());
        String WHITE = ColorFormatting.getColor(TextFormatting.WHITE.getColor());
        String sphere = "";
        String KT = "";
        for (EffectInstance potion : player.getActivePotionEffects()) {
            int duration = potion.getDuration();
            if (duration != 0 && potion.getPotion() == Effects.UNLUCK && potion.getAmplifier() == -1) {
                KT = " [" + RED + StringUtils.ticksToElapsedTime(duration).replace(duration >= 200 ? "0:" : "0:0", "") + WHITE + "]";
                break;
            }
        }
        ITextComponent playerName = renderDonat.getValue() ? player.getDisplayName() : player.getName();
        if (offHandNBT != null) {
            if (PlayerUtil.isFuntime()) {
                sphere = (offHandNBT.getInt("tslevel") != 0) ? (" [" + GOLD + offHandNBT.getString("don-item").replace("sphere-", "").toUpperCase() + RED + " " + offHandNBT.getInt("tslevel") + "/3" + WHITE + "]") : "";
            } else if (PlayerUtil.isHoly()) {
                String itemName = offHandStack.getDisplayName().getString().toLowerCase();
                if (itemName.contains("талисман") || itemName.contains("сфера")) {
                    sphere = " [" + GOLD + itemName.replace("талисман", "").replace("сфера", "").replace(" ", "").replace("-", "") + WHITE + "]";
                }
            }
        }
        String health = PlayerUtil.isFuntime() && player.getHealthFixed() == 1000 ? "" : " [" + RED + player.getHealthFixed() + " HP" + WHITE + "]";
        String friend = Excellent.inst().friendManager().isFriend(player.getGameProfile().getName()) ? GREEN + " [F]" + WHITE : "";
        String bot = (AntiBot.getInstance().isBot(player) ? DARK_RED + " [BOT]" + WHITE : "");

        if (optimized().getValue()) {
            return new StringTextComponent("").appendString(playerName.getString() + health + sphere + friend + bot + KT);
        } else {
            return playerName.deepCopy().appendString(health + sphere + friend + bot + KT);
        }
    }

    private void drawArmor(MatrixStack matrix, PlayerEntity player, float x, float y, float width, float fontHeight) {
        List<ItemStack> items = new ArrayList<>();
        if (!player.getHeldItemOffhand().isEmpty()) items.add(player.getHeldItemOffhand());
        for (ItemStack itemStack : player.getArmorInventoryList()) {
            if (!itemStack.isEmpty()) items.add(itemStack.copy());
        }
        if (!player.getHeldItemMainhand().isEmpty()) items.add(player.getHeldItemMainhand());

        float posX = x + width / 2F + (-items.size() * 5) - 1;
        float posY = y - 12;
        float stackSize = 8;

        for (ItemStack item : items) {
            if (item.isEmpty()) continue;
            GLUtil.startScale(posX + (stackSize / 2F), posY + (stackSize / 2F), 0.5F);
            drawItemStack(matrix, item, posX, posY, true);
            GLUtil.endScale();
            posX += (stackSize + 2);
        }
    }

    public void drawItemStack(MatrixStack matrix, ItemStack stack, double x, double y, boolean drawRect) {
        matrix.push();
        RenderSystem.translated(x, y, 0);
        if (drawRect) RectUtil.drawRect(matrix, 0, 0, 16, 16, black);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (stack.getCount() > 1) {
            RenderSystem.translated(0, 0, 200);
            mc.fontRenderer.drawString(matrix, String.valueOf(stack.getCount()), (float) (16 - mc.fontRenderer.getStringWidth(String.valueOf(stack.getCount()))), 8, 0xFFFFFF);
        }
        RenderSystem.translated(-x, -y, 0);
        matrix.pop();
    }

    private void drawTag(MatrixStack matrix, ITextComponent tagComponent, float centerX, float y, float nameWidth, float fontHeight, float scale) {
        RectUtil.drawRect(matrix, centerX - (nameWidth / 2F) - 1, y - 0.5F, nameWidth + 2, fontHeight + 1, black);
        font.drawTextComponent(matrix, tagComponent, centerX - nameWidth / 2F, y, -1, false, fontHeight);
    }

    private AxisAlignedBB getEntityBox(Entity entity, Vector3d vec) {
        Vector3d size = new Vector3d(entity.getBoundingBox().maxX - entity.getBoundingBox().minX,
                entity.getBoundingBox().maxY - entity.getBoundingBox().minY,
                entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ);

        return new AxisAlignedBB(vec.x - size.x / 2f, vec.y,
                vec.z - size.z / 2f, vec.x + size.x / 2f,
                vec.y + size.y + (0.2F - (entity.isSneaking() ? 0.1F : 0.0F)),
                vec.z + size.z / 2f);
    }

    private boolean isValid(final Entity entity) {
        if (!entity.isAlive()) {
            return false;
        }
        if (mc.renderViewEntity != null && entity == mc.renderViewEntity && mc.gameSettings.getPointOfView().firstPerson()) {
            return false;
        }
        return isInView(entity) && (entity instanceof PlayerEntity || entity instanceof ItemEntity);
    }

    public boolean isInView(Entity entity) {
        if (mc.getRenderViewEntity() == null || mc.worldRenderer.getClippinghelper() == null) {
            return false;
        }
        return mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(entity.getBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private float getWidth(ITextComponent text) {
        return font.getWidth(text, fontSize.getValue());
    }
}