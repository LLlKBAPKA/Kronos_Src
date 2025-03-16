package org.excellent.client.managers.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import org.excellent.client.api.events.CancellableEvent;

@Getter
@AllArgsConstructor
public class EntityRayTraceEvent extends CancellableEvent {
    private final Entity entity;
}