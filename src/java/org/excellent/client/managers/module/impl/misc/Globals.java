package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.other.Instance;
import org.excellent.common.impl.globals.ClientAPI;
import org.excellent.common.impl.globals.ServerAPI;
import org.excellent.lib.util.time.StopWatch;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Globals", category = Category.MISC)
public class Globals extends Module {
    public static Globals getInstance() {
        return Instance.get(Globals.class);
    }

    private final StopWatch timer = new StopWatch();
    private boolean init;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (this.timer.finished(20000) && this.init) {
            new Thread(() -> ClientAPI.update(ServerAPI.getClients())).start();
            this.timer.reset();
        }

        if (!this.init) {
            ServerAPI.init();
            ServerAPI.updateName();
            ClientAPI.update(ServerAPI.getClients());
            this.init = true;
        }
    }

    @Override
    public void onDisable() {
        ServerAPI.finish();
        ClientAPI.USERS.clear();
        this.init = false;
    }

}