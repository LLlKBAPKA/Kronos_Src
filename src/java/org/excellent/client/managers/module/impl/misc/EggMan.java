package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import org.excellent.client.api.events.orbit.EventHandler;
import org.excellent.client.managers.events.other.EntityRenderMatrixEvent;
import org.excellent.client.managers.events.player.UpdateEvent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.BooleanSetting;
import org.excellent.client.managers.module.settings.impl.SliderSetting;
import org.excellent.client.utils.math.Mathf;
import org.excellent.client.utils.other.Instance;
import org.excellent.client.utils.other.SoundUtil;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "EggMan", category = Category.MISC)
public class EggMan extends Module {
    public static EggMan getInstance() {
        return Instance.get(EggMan.class);
    }

    public final BooleanSetting wobbleMusicWibe = new BooleanSetting(this, "Музыка для флекса", true);
    private final SliderSetting wobbleMusicVolume = new SliderSetting(this, "Громкость музыки", 25, 0, 100, 1).setVisible(wobbleMusicWibe::getValue);

    private final SoundUtil.AudioClipPlayController wobbleMusicTuner = SoundUtil.AudioClipPlayController.build(SoundUtil.AudioClip.build("eggman.wav", true), () -> wobbleMusicWibe.getValue() && isEnabled(), true);
    private float prevVolume;

    @Override
    public void toggle() {
        super.toggle();
        prevVolume = wobbleMusicVolume.getValue();
        wobbleMusicTuner.getAudioClip().setVolume(wobbleMusicVolume.getValue() / 100F);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        updateMusic();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        wobbleMusicTuner.updatePlayingStatus();
    }

    @EventHandler
    public void onEvent(EntityRenderMatrixEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            float wobble = ((System.currentTimeMillis() + event.getEntity().getEntityId() * 100) % 400) / 400F;
            wobble = (wobble > 0.5F ? 1F - wobble : wobble) * 2F;
            wobble = Mathf.clamp01(wobble);
            event.getMatrix().scale(wobble * 2F + 1F, 1F - 0.5F * wobble, wobble * 2F + 1F);
        }
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        updateMusic();
    }

    private void updateMusic() {
        wobbleMusicTuner.updatePlayingStatus();
        if (wobbleMusicTuner.isSucessPlaying() && prevVolume != wobbleMusicVolume.getValue()) {
            wobbleMusicTuner.getAudioClip().setVolume(wobbleMusicVolume.getValue() / 100F);
        }
    }
}