package org.excellent.client.utils.math;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.excellent.client.managers.module.impl.combat.AutoTotem;
import org.excellent.client.managers.module.impl.movement.NoSlow;
import org.excellent.lib.util.time.StopWatch;

@Getter
@Accessors(fluent = true)
public class PerfectDelay {
    private long delay = 0;
    private final StopWatch time = new StopWatch();

    public boolean cooldownComplete() {
        return cooldownComplete(this.delay);
    }

    public boolean cooldownComplete(long delay) {
        return time.finished(delay);
    }

    public void reset(long delay) {
        if (NoSlow.getInstance().isEnabled() || AutoTotem.getInstance().isEnabled()) {
            return;
        }
        this.time.reset();
        this.delay = delay;
    }

}