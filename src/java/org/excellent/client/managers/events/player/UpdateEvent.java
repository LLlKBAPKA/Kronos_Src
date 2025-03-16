package org.excellent.client.managers.events.player;

import lombok.Getter;
import org.excellent.client.api.events.Event;

public class UpdateEvent extends Event {
    @Getter
    private static final UpdateEvent instance = new UpdateEvent();
}