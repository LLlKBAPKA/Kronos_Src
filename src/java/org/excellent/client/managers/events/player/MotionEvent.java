package org.excellent.client.managers.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.excellent.client.api.events.CancellableEvent;
import org.excellent.client.api.interfaces.IMinecraft;

@Getter
@Setter
@AllArgsConstructor
public final class MotionEvent extends CancellableEvent implements IMinecraft {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    private boolean isSneaking;
    private boolean isSprinting;

    public void setYaw(float yaw) {
        this.yaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.renderYawOffset = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        mc.player.rotationPitchHead = pitch;
    }

}