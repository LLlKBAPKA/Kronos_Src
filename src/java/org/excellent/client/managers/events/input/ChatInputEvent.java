package org.excellent.client.managers.events.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.excellent.client.api.events.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public final class ChatInputEvent extends CancellableEvent {
    private String message;
}