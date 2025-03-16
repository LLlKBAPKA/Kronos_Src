package org.excellent.client.screen.hud;


import org.excellent.client.api.interfaces.IMinecraft;
import org.excellent.client.managers.events.render.Render2DEvent;
import org.excellent.client.managers.module.impl.client.Theme;
import org.excellent.client.utils.render.font.Font;
import org.excellent.client.utils.render.font.Fonts;

public interface IRenderer extends IMinecraft {
    Font font = Fonts.SF_SEMIBOLD;
    float fontSize = 7;

    void render(Render2DEvent event);

    default Theme theme() {
        return Theme.getInstance();
    }

}
