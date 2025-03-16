package org.excellent.client.managers.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.Entity;
import org.excellent.client.api.events.Event;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotemPopEvent extends Event {
    private Entity entity;
}
