package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.optifine.Config;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.util.PropertiesOrdered;
import org.excellent.client.api.client.Constants;
import org.excellent.client.managers.module.impl.client.Theme;
import org.excellent.client.utils.render.color.ColorFormatting;
import org.excellent.client.utils.render.color.ColorUtil;
import org.excellent.client.utils.render.draw.RectUtil;
import org.excellent.client.utils.render.draw.RenderUtil;
import org.excellent.client.utils.render.font.Fonts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

public class ResourceLoadProgressGui extends LoadingGui {
    private static final ResourceLocation MOJANG_LOGO_TEXTURE = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final int field_238627_b_ = ColorHelper.PackedColor.packColor(255, 239, 50, 61);
    private static final int field_238628_c_ = field_238627_b_ & 16777215;
    private final Minecraft mc;
    private final IAsyncReloader asyncReloader;
    private final Consumer<Optional<Throwable>> completedCallback;
    private final boolean reloading;
    private float progress;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;
    private int colorBackground = field_238628_c_;
    private int colorBar = field_238628_c_;
    private int colorOutline = 16777215;
    private int colorProgress = 16777215;
    private GlBlendState blendState = null;
    private boolean fadeOut = false;

    public ResourceLoadProgressGui(Minecraft p_i225928_1_, IAsyncReloader p_i225928_2_, Consumer<Optional<Throwable>> p_i225928_3_, boolean p_i225928_4_) {
        this.mc = p_i225928_1_;
        this.asyncReloader = p_i225928_2_;
        this.completedCallback = p_i225928_3_;
        this.reloading = false;
    }

    public static void loadLogoTexture(Minecraft mc) {
        mc.getTextureManager().loadTexture(MOJANG_LOGO_TEXTURE, new ResourceLoadProgressGui.MojangLogoTexture());
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = this.mc.getMainWindow().getScaledWidth();
        int j = this.mc.getMainWindow().getScaledHeight();
        long k = Util.milliTime();

        if (this.reloading && (this.asyncReloader.asyncPartDone() || this.mc.currentScreen != null) && this.fadeInStart == -1L) {
            this.fadeInStart = k;
        }

        float f = this.fadeOutStart > -1L ? (float) (k - this.fadeOutStart) / 1000.0F : -1.0F;
        float f1 = this.fadeInStart > -1L ? (float) (k - this.fadeInStart) / 500.0F : -1.0F;
        float f2;

        if (f >= 1.0F) {
            this.fadeOut = true;

            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.render(matrixStack, 0, 0, partialTicks);
            }
            float l = (1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F));
            RenderUtil.drawShaderBackground(matrixStack, l);
            f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            if (this.mc.currentScreen != null && f1 < 1.0F) {
                this.mc.currentScreen.render(matrixStack, mouseX, mouseY, partialTicks);
            }

            float i2 = MathHelper.clamp(f1, 0.15F, 1.0F);
            RenderUtil.drawShaderBackground(matrixStack, i2);
            f2 = MathHelper.clamp(f1, 0.0F, 1.0F);
        } else {
            float i2 = MathHelper.clamp(f1, 0.15F, 1.0F);
            RenderUtil.drawShaderBackground(matrixStack, 1F);
            f2 = 1.0F;
        }

        int j2 = (int) ((double) this.mc.getMainWindow().getScaledWidth() * 0.5D);
        int i1 = (int) ((double) this.mc.getMainWindow().getScaledHeight() * 0.5D);
        double d0 = Math.min((double) this.mc.getMainWindow().getScaledWidth() * 0.75D, this.mc.getMainWindow().getScaledHeight()) * 0.25D;
        int j1 = (int) (d0 * 0.5D);
        double d1 = d0 * 4.0D;
        int k1 = (int) (d1 * 0.125D);

        int textColor = ColorUtil.getColor(200, 200, 230, f2);
        int accentColor = ColorUtil.multAlpha(Theme.getInstance().clientColor(), f2);
        float darker = 0.5F;
        String namespace = ColorFormatting.getColor(accentColor) + Constants.NAMESPACE + ColorFormatting.reset() + " - выбор победителей.";

        Fonts.SF_REGULAR.drawCenter(matrixStack, namespace, j2, i1 - 12, ColorUtil.multDark(textColor, darker), 8);

        float textWidth = Fonts.SF_REGULAR.getWidth(namespace, 8);

        int l1 = (int) ((double) this.mc.getMainWindow().getScaledHeight() * 0.5 + 12);
        float f3 = this.asyncReloader.estimateExecutionSpeed();
        this.progress = MathHelper.clamp(this.progress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);

        int k2 = (int) (textWidth / 2F);
        if (f < 1.0F) {
            this.drawProgressBar(matrixStack, i / 2 - k2, l1 - 1, i / 2 + k2, l1 + 1, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
        }

        if (f >= 2.0F) {
            this.mc.setLoadingGui(null);
        }

        if (this.fadeOutStart == -1L && this.asyncReloader.fullyDone() && (!this.reloading || f1 >= 2.0F)) {
            this.fadeOutStart = Util.milliTime();

            try {
                this.asyncReloader.join();
                this.completedCallback.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.completedCallback.accept(Optional.of(throwable));
            }

            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.init(this.mc, this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight());
            }
        }
    }

    private void drawProgressBar(MatrixStack matrix, int minX, int minY, int maxX, int maxY, float alphaPC) {
        int width = maxX - minX;
        int height = maxY - minY;
        int progressWidth = MathHelper.ceil((float) (width) * this.progress);
        int alpha = Math.round(alphaPC * 255.0F);

        int color = ColorUtil.multDark(ColorUtil.multAlpha(Theme.getInstance().clientColor(), alpha), 0.5F);
        int colorGlow = ColorUtil.multAlpha(ColorUtil.multAlpha(Theme.getInstance().clientColor(), alpha), 0.25F);
        int colorb = ColorUtil.getColor(20, 20, 30, alpha);
        RectUtil.drawRoundedRectShadowed(matrix, minX, minY, width, height, height / 4F, 1, colorb, colorb, colorb, colorb, false, false, true, true);
        RectUtil.drawRoundedRectShadowed(matrix, minX, minY + height / 2F, progressWidth, 0, 0, 8, colorGlow, colorGlow, colorGlow, colorGlow, true, true, true, true);
        RectUtil.drawRoundedRectShadowed(matrix, minX, minY, progressWidth, height, height / 4F, 1, color, color, color, color, true, false, true, true);
    }

    public void update() {
        this.colorBackground = field_238628_c_;
        this.colorBar = field_238628_c_;
        this.colorOutline = 16777215;
        this.colorProgress = 16777215;

        if (Config.isCustomColors()) {
            try {
                String s = "optifine/color.properties";
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (!Config.hasResource(resourcelocation)) {
                    return;
                }

                InputStream inputstream = Config.getResourceStream(resourcelocation);
                Config.dbg("Loading " + s);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                this.colorBackground = readColor(properties, "screen.loading", this.colorBackground);
                this.colorOutline = readColor(properties, "screen.loading.outline", this.colorOutline);
                this.colorBar = readColor(properties, "screen.loading.bar", this.colorBar);
                this.colorProgress = readColor(properties, "screen.loading.progress", this.colorProgress);
                this.blendState = ShaderPackParser.parseBlendState(properties.getProperty("screen.loading.blend"));
            } catch (Exception exception) {
                Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
    }

    private static int readColor(Properties p_readColor_0_, String p_readColor_1_, int p_readColor_2_) {
        String s = p_readColor_0_.getProperty(p_readColor_1_);

        if (s == null) {
            return p_readColor_2_;
        } else {
            s = s.trim();
            int i = parseColor(s, p_readColor_2_);

            if (i < 0) {
                Config.warn("Invalid color: " + p_readColor_1_ + " = " + s);
                return i;
            } else {
                Config.dbg(p_readColor_1_ + " = " + s);
                return i;
            }
        }
    }

    private static int parseColor(String p_parseColor_0_, int p_parseColor_1_) {
        if (p_parseColor_0_ == null) {
            return p_parseColor_1_;
        } else {
            p_parseColor_0_ = p_parseColor_0_.trim();

            try {
                return Integer.parseInt(p_parseColor_0_, 16) & 16777215;
            } catch (NumberFormatException numberformatexception) {
                return p_parseColor_1_;
            }
        }
    }

    public boolean isFadeOut() {
        return this.fadeOut;
    }

    static class MojangLogoTexture extends SimpleTexture {
        public MojangLogoTexture() {
            super(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
        }

        protected SimpleTexture.TextureData getTextureData(IResourceManager resourceManager) {
            Minecraft minecraft = Minecraft.getInstance();
            VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();

            try (InputStream inputstream = getLogoInputStream(resourceManager, vanillapack)) {
                return new SimpleTexture.TextureData(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
            } catch (IOException ioexception1) {
                return new SimpleTexture.TextureData(ioexception1);
            }
        }

        private static InputStream getLogoInputStream(IResourceManager p_getLogoInputStream_0_, VanillaPack p_getLogoInputStream_1_) throws IOException {
            return p_getLogoInputStream_0_.hasResource(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE) ? p_getLogoInputStream_0_.getResource(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE).getInputStream() : p_getLogoInputStream_1_.getResourceStream(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
        }
    }
}
