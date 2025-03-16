package org.excellent.client.screen.hud.impl;

import net.mojang.blaze3d.matrix.MatrixStack;
import org.excellent.client.Excellent;
import org.excellent.client.api.interfaces.IWindow;
import org.excellent.client.managers.events.render.Render2DEvent;
import org.excellent.client.screen.hud.IRenderer;
import org.excellent.client.utils.math.Mathf;
import org.excellent.client.utils.player.MoveUtil;
import org.excellent.client.utils.player.PlayerUtil;
import org.excellent.client.utils.render.color.ColorFormatting;
import org.excellent.client.utils.render.color.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class InformationRenderer implements IRenderer, IWindow {
    @Override
    public void render(Render2DEvent event) {
        MatrixStack matrix = event.getMatrix();

        List<String> leftInfo = new ArrayList<>();

        int textColor = theme().textColor();
        int textAccentColor = theme().textAccentColor();

        leftInfo.add(ColorFormatting.getColor(textColor) + "Pos: " + ColorFormatting.getColor(textAccentColor) + Mathf.round(mc.player.getPosX(), 1) + ", " + Mathf.round(mc.player.getPosY(), 1) + ", " + Mathf.round(mc.player.getPosZ(), 1)
                + ColorFormatting.getColor(ColorUtil.overCol(ColorUtil.WHITE, ColorUtil.RED, 0.75F)) + " (%s, %s)".formatted(Mathf.round(mc.player.getPosX() / 8F, 1), Mathf.round(mc.player.getPosZ() / 8F, 1)));
        leftInfo.add(ColorFormatting.getColor(textColor) + "Bps: " + ColorFormatting.getColor(textAccentColor) + Mathf.round(MoveUtil.speedSqrt() * 20.0F, 1));

        leftInfo.sort((s1, s2) -> Float.compare(-font.getWidth(s1, fontSize), -font.getWidth(s2, fontSize)));

        float y = height() - fontSize - 2.5F;

        float leftX = 2.5F;
        float leftOffset = 0;
        for (String s : leftInfo) {
            font.drawOutline(matrix, s, leftX, y + leftOffset, -1, fontSize);
            leftOffset -= fontSize;
        }

        List<String> rightInfo = new ArrayList<>();

        rightInfo.add(ColorFormatting.getColor(textColor) + "Tps: " + ColorFormatting.getColor(textAccentColor) + Mathf.round(Excellent.inst().serverTps().getTPS(), 1));
        rightInfo.add(ColorFormatting.getColor(textColor) + "Ping: " + ColorFormatting.getColor(textAccentColor) + PlayerUtil.getPing(mc.player));

        rightInfo.sort((s1, s2) -> Float.compare(-font.getWidth(s1, fontSize), -font.getWidth(s2, fontSize)));

        float rightX = width() - 2.5F;
        float rightOffset = 0;
        for (String s : rightInfo) {
            font.drawRightOutline(matrix, s, rightX, y + rightOffset, -1, fontSize);
            rightOffset -= fontSize;
        }
    }
}
