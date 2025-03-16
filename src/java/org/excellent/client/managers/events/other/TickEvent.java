package org.excellent.client.managers.events.other;

import lombok.Getter;
import org.excellent.client.api.events.Event;

public class TickEvent extends Event {
    @Getter
    private static final TickEvent instance = new TickEvent();
}
