package org.excellent.client.managers.events.other;

import lombok.Getter;
import org.excellent.client.api.events.Event;

public class GameUpdateEvent extends Event {
    @Getter
    private static final GameUpdateEvent instance = new GameUpdateEvent();
}
