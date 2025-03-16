package org.excellent.client.screen.hud;

import org.excellent.client.api.interfaces.IMinecraft;
import org.excellent.client.managers.events.render.RenderScoreBoardEvent;

public interface IScoreBoardRenerer extends IMinecraft {
    void renderScoreBoard(RenderScoreBoardEvent event);
}
