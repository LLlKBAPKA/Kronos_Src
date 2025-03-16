package org.excellent.client.screen.flatgui;

import lombok.Data;
import lombok.experimental.Accessors;
import org.excellent.client.Excellent;
import org.excellent.client.api.interfaces.IScreen;
import org.excellent.client.managers.module.impl.client.Theme;
import org.excellent.client.utils.animation.Animation;
import org.excellent.client.utils.math.Mathf;
import org.excellent.client.utils.render.color.ColorUtil;
import org.joml.Vector2f;

@Data
@Accessors(fluent = true)
public abstract class AbstractPanel implements IScreen {
    private final Vector2f position = new Vector2f();
    private final Vector2f size = new Vector2f();
    private final Animation hover = new Animation();

    public float alpha() {
        return Mathf.clamp01(flatgui().alpha().get());
    }

    public boolean hovered(double mouseX, double mouseY) {
        return isHover(mouseX, mouseY, position.x, position.y, size.x, size.y);
    }

    public FlatGuiScreen flatgui() {
        return Excellent.inst().flatGui();
    }

    protected int themeColor() {
        return ColorUtil.multAlpha(Theme.getInstance().clientColor(), alpha());
    }
}
