package org.excellent.client.managers.component.impl.other;

import net.minecraft.client.settings.KeyBinding;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.component.Component;
import org.excellent.client.managers.events.world.WorldLoadEvent;

public class SyncFixComponent extends Component {
    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        KeyBinding.unPressAllKeys();
        for (KeyBinding binding : mc.gameSettings.keyBindings) {
            binding.setPressed(false);
        }
    }
}
