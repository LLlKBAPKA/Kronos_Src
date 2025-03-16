package org.excellent.client.api.events;


import org.excellent.client.Excellent;

public abstract class Handler {
    public Handler() {
        Excellent.eventHandler().subscribe(this);
    }
}