package org.excellent.client.managers.events.other;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import org.excellent.client.api.events.Event;

@Getter
@RequiredArgsConstructor
public class EntitySpawnEvent extends Event {
    private final Entity entity;
}