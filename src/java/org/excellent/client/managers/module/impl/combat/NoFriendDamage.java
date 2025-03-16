package org.excellent.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import org.excellent.client.Excellent;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.player.AttackEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoFriendDamage", category = Category.COMBAT)
public class NoFriendDamage extends Module {
    public static NoFriendDamage getInstance() {
        return Instance.get(NoFriendDamage.class);
    }

    @EventHandler
    public void onEvent(AttackEvent event) {
        if (event.getTarget() instanceof PlayerEntity player && Excellent.inst().friendManager().isFriend(TextFormatting.removeFormatting(player.getGameProfile().getName()))) {
            event.cancel();
        }
    }
}
