package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.other.CameraClipEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CameraClip", category = Category.MISC)
public class CameraClip extends Module {
    public static CameraClip getInstance() {
        return Instance.get(CameraClip.class);
    }

    @EventHandler
    public void onEvent(CameraClipEvent event) {
        event.cancel();
    }
}