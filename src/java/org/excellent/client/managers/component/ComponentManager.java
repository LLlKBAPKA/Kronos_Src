package org.excellent.client.managers.component;


import org.excellent.client.Excellent;
import org.excellent.client.managers.component.impl.aura.AuraComponent;
import org.excellent.client.managers.component.impl.client.ClientComponent;
import org.excellent.client.managers.component.impl.drag.DragComponent;
import org.excellent.client.managers.component.impl.inventory.HandComponent;
import org.excellent.client.managers.component.impl.inventory.InvComponent;
import org.excellent.client.managers.component.impl.other.ConnectionComponent;
import org.excellent.client.managers.component.impl.other.SyncFixComponent;
import org.excellent.client.managers.component.impl.rotation.FreeLookComponent;
import org.excellent.client.managers.component.impl.rotation.RotationComponent;
import org.excellent.client.managers.component.impl.rotation.SmoothRotationComponent;
import org.excellent.client.managers.component.impl.target.TargetComponent;

import java.util.HashMap;

public final class ComponentManager extends HashMap<Class<? extends Component>, Component> {


    public void init() {
        add(
                new SyncFixComponent(),
                new AuraComponent(),
                new TargetComponent(),
                new DragComponent(),
                new FreeLookComponent(),
                new RotationComponent(),
                new SmoothRotationComponent(),
                new HandComponent(),
                new InvComponent(),
                new ClientComponent(),
                new ConnectionComponent()
        );

        this.values().forEach(component -> Excellent.eventHandler().subscribe(component));
    }

    public void add(Component... components) {
        for (Component component : components) {
            this.put(component.getClass(), component);
        }
    }

    public void unregister(Component... components) {
        for (Component component : components) {
            Excellent.eventHandler().unsubscribe(component);
            this.remove(component.getClass());
        }
    }

    public <T extends Component> T get(final Class<T> clazz) {
        return this.values()
                .stream()
                .filter(component -> component.getClass() == clazz)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}