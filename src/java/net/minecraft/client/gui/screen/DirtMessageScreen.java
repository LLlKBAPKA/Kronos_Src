package net.minecraft.client.gui.screen;

import net.minecraft.util.text.ITextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;

public class DirtMessageScreen extends Screen {
    public DirtMessageScreen(ITextComponent p_i51114_1_) {
        super(p_i51114_1_);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(0);
        drawCenteredStringWithShadow(matrixStack, this.font, this.title, this.width / 2, 70, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
