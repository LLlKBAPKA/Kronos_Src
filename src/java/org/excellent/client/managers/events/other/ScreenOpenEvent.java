package org.excellent.client.managers.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import org.excellent.client.api.events.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class ScreenOpenEvent extends CancellableEvent {
    private final Screen screen;
}
