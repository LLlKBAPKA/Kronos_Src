package org.excellent.client.api.events;

import org.excellent.client.Excellent;

public class Event {
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public void hook() {
        Excellent.eventHandler().post(this);
    }
}
