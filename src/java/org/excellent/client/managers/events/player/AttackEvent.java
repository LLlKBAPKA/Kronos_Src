package org.excellent.client.managers.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import org.excellent.client.api.events.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class AttackEvent extends CancellableEvent {
    private Entity target;
}