package org.excellent.client.managers.events.player;

import lombok.Getter;
import org.excellent.client.api.events.Event;

public class PostUpdateEvent extends Event {
    @Getter
    private static final PostUpdateEvent instance = new PostUpdateEvent();
}