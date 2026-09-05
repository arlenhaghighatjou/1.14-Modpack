package net.minecraft.client.audio;

import com.google.common.collect.*;
import net.lax1dude.eaglercraft.internal.IAudioHandle;
import net.lax1dude.eaglercraft.internal.IAudioResource;
import net.lax1dude.eaglercraft.internal.PlatformAudio;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class SoundEngine {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<ResourceLocation> UNABLE_TO_PLAY = Sets.newHashSet();
    private final SoundHandler sndHandler;
    private final GameSettings options;
    private boolean loaded;
    private final Listener listener = new Listener();
    private int ticks;
    private final Map<ISound, IAudioHandle> playingSoundsChannel = Maps.newHashMap();
    private final Multimap<SoundCategory, ISound> field_217943_n = HashMultimap.create();
    private final List<ITickableSound> tickableSounds = Lists.newArrayList();
    private final Map<ISound, Integer> delayedSounds = Maps.newHashMap();
    private final Map<ISound, Integer> playingSoundsStopTime = Maps.newHashMap();
    private final List<ISoundEventListener> listeners = Lists.newArrayList();
    private final List<Sound> soundsToPreload = Lists.newArrayList();

    public SoundEngine(SoundHandler p_i50892_1_, GameSettings p_i50892_2_, IResourceManager p_i50892_3_) {
        this.sndHandler = p_i50892_1_;
        this.options = p_i50892_2_;
    }

    public void reload() {
        UNABLE_TO_PLAY.clear();

        for (SoundEvent soundevent : Registry.SOUND_EVENT) {
            ResourceLocation resourcelocation = soundevent.getName();
            if (this.sndHandler.getAccessor(resourcelocation) == null) {
                LOGGER.warn("Missing sound for event: {}", (Object) Registry.SOUND_EVENT.getKey(soundevent));
                UNABLE_TO_PLAY.add(resourcelocation);
            }
        }

        this.unload();
        this.load();
    }

    private synchronized void load() {
        if (!this.loaded) {
            try {
                this.listener.init();
                this.listener.setGain(this.options.getSoundLevel(SoundCategory.MASTER));
                this.soundsToPreload.clear();
                this.loaded = true;
            } catch (RuntimeException runtimeexception) {
            }
        }
    }

    private float getVolume(SoundCategory category) {
        return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
    }

    public void setVolume(SoundCategory category, float volume) {
        if (this.loaded) {
            if (category == SoundCategory.MASTER) {
                this.listener.setGain(volume);
            } else {
                this.playingSoundsChannel.forEach((p_217926_1_, p_217926_2_) -> {
                    float f = this.getClampedVolume(p_217926_1_);
                    if (f <= 0.0F) {
                        p_217926_2_.gain(0.0F);
                    } else {
                        p_217926_2_.gain(f);
                    }
                });
            }
        }
    }

    public void unload() {
        if (this.loaded) {
            this.stopAllSounds();
            PlatformAudio.flushAudioCache();
            this.loaded = false;
        }
    }

    public void stop(ISound sound) {
        if (this.loaded) {
            IAudioHandle handle = this.playingSoundsChannel.get(sound);
            if (handle != null) {
                handle.end();
            }
        }
    }

    public void stopAllSounds() {
        if (this.loaded) {
            this.playingSoundsChannel.values().forEach(IAudioHandle::end);
            this.playingSoundsChannel.clear();
            this.delayedSounds.clear();
            this.tickableSounds.clear();
            this.field_217943_n.clear();
            this.playingSoundsStopTime.clear();
        }
    }

    public void addListener(ISoundEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISoundEventListener listener) {
        this.listeners.remove(listener);
    }

    public void tick(boolean p_217921_1_) {
        if (!p_217921_1_) {
            this.func_217927_h();
        }
    }

    private void func_217927_h() {
        ++this.ticks;

        for (ITickableSound itickablesound : this.tickableSounds) {
            itickablesound.tick();
            if (itickablesound.isDonePlaying()) {
                this.stop(itickablesound);
            } else {
                float f = this.getClampedVolume(itickablesound);
                float f1 = this.getClampedPitch(itickablesound);
                IAudioHandle handle = this.playingSoundsChannel.get(itickablesound);
                if (handle != null) {
                    handle.gain(f);
                    handle.pitch(f1);
                    handle.move(itickablesound.getX(), itickablesound.getY(), itickablesound.getZ());
                }
            }
        }

        Iterator<Entry<ISound, IAudioHandle>> iterator = this.playingSoundsChannel.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ISound, IAudioHandle> entry = iterator.next();
            IAudioHandle handle = entry.getValue();
            ISound isound = entry.getKey();
            float f2 = this.options.getSoundLevel(isound.getCategory());
            if (f2 <= 0.0F) {
                handle.end();
                iterator.remove();
            } else if (handle.shouldFree()) {
                int j = this.playingSoundsStopTime.get(isound);
                if (j <= this.ticks) {
                    int i = isound.getRepeatDelay();
                    if (isound.canRepeat() && i > 0) {
                        this.delayedSounds.put(isound, this.ticks + i);
                    }

                    iterator.remove();
                    this.playingSoundsStopTime.remove(isound);

                    try {
                        this.field_217943_n.remove(isound.getCategory(), isound);
                    } catch (RuntimeException var9) {
                        ;
                    }

                    if (isound instanceof ITickableSound) {
                        this.tickableSounds.remove(isound);
                    }
                }
            }
        }

        Iterator<Entry<ISound, Integer>> iterator1 = this.delayedSounds.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry<ISound, Integer> entry1 = iterator1.next();
            if (this.ticks >= entry1.getValue()) {
                ISound isound1 = entry1.getKey();
                if (isound1 instanceof ITickableSound) {
                    ((ITickableSound) isound1).tick();
                }

                this.play(isound1);
                iterator1.remove();
            }
        }
    }

    public boolean isPlaying(ISound p_217933_1_) {
        if (!this.loaded) {
            return false;
        } else {
            return this.playingSoundsStopTime.containsKey(p_217933_1_) && this.playingSoundsStopTime.get(p_217933_1_) <= this.ticks ? true : this.playingSoundsChannel.containsKey(p_217933_1_);
        }
    }

    public void play(ISound p_sound) {
        if (this.loaded) {
            SoundEventAccessor soundeventaccessor = p_sound.createAccessor(this.sndHandler);
            ResourceLocation resourcelocation = p_sound.getSoundLocation();
            if (soundeventaccessor == null) {
                if (UNABLE_TO_PLAY.add(resourcelocation)) {
                    LOGGER.warn("Unable to play unknown soundEvent: {}", (Object) resourcelocation);
                }
            } else {
                if (!this.listeners.isEmpty()) {
                    for (ISoundEventListener isoundeventlistener : this.listeners) {
                        isoundeventlistener.onPlaySound(p_sound, soundeventaccessor);
                    }
                }

                if (this.listener.getGain() <= 0.0F) {
                } else {
                    Sound sound = p_sound.getSound();
                    if (sound == SoundHandler.MISSING_SOUND) {
                        if (UNABLE_TO_PLAY.add(resourcelocation)) {
                            LOGGER.warn("Unable to play empty soundEvent: {}", (Object) resourcelocation);
                        }
                    } else {
                        float f3 = p_sound.getVolume();
                        float f = Math.max(f3, 1.0F) * (float) sound.getAttenuationDistance();
                        SoundCategory soundcategory = p_sound.getCategory();
                        float f1 = this.getClampedVolume(p_sound);
                        float f2 = this.getClampedPitch(p_sound);
                        boolean flag = p_sound.isGlobal();
                        if (f1 == 0.0F && !p_sound.canBeSilent()) {
                        } else {
                            boolean flag1 = p_sound.canRepeat() && p_sound.getRepeatDelay() == 0;

                            ResourceLocation oggLoc = sound.getSoundAsOggLocation();
                            String filepath = "assets/" + oggLoc.getNamespace() + "/" + oggLoc.getPath();
                            IAudioResource resource = PlatformAudio.loadAudioData(filepath, false);

                            if (resource != null) {
                                IAudioHandle handle;
                                if (flag) {
                                    handle = PlatformAudio.beginPlaybackStatic(resource, f1, f2, flag1);
                                } else {
                                    handle = PlatformAudio.beginPlayback(resource, p_sound.getX(), p_sound.getY(), p_sound.getZ(), f1, f2, flag1);
                                }

                                if (handle != null) {
                                    this.playingSoundsStopTime.put(p_sound, this.ticks + 20);
                                    this.playingSoundsChannel.put(p_sound, handle);
                                    this.field_217943_n.put(soundcategory, p_sound);

                                    if (p_sound instanceof ITickableSound) {
                                        this.tickableSounds.add((ITickableSound) p_sound);
                                    }
                                }
                            } else {
                                if (UNABLE_TO_PLAY.add(oggLoc)) {
                                    LOGGER.warn("Unable to find sound file: {}", (Object) oggLoc);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void enqueuePreload(Sound soundIn) {
        this.soundsToPreload.add(soundIn);
    }

    private float getClampedPitch(ISound soundIn) {
        return MathHelper.clamp(soundIn.getPitch(), 0.5F, 2.0F);
    }

    private float getClampedVolume(ISound soundIn) {
        return MathHelper.clamp(soundIn.getVolume() * this.getVolume(soundIn.getCategory()), 0.0F, 1.0F);
    }

    public void pause() {
        if (this.loaded) {
            this.playingSoundsChannel.values().forEach(h -> h.pause(true));
        }
    }

    public void resume() {
        if (this.loaded) {
            this.playingSoundsChannel.values().forEach(h -> h.pause(false));
        }
    }

    public void playDelayed(ISound sound, int delay) {
        this.delayedSounds.put(sound, this.ticks + delay);
    }

    public void updateListener(ActiveRenderInfo p_217920_1_) {
        if (this.loaded && p_217920_1_.isValid()) {
            Vec3d vec3d = p_217920_1_.getProjectedView();
            PlatformAudio.setListener((float) vec3d.x, (float) vec3d.y, (float) vec3d.z, p_217920_1_.getPitch(), p_217920_1_.getYaw());
        }
    }

    public void stop(ResourceLocation soundName, SoundCategory category) {
        if (category != null) {
            for (ISound isound : this.field_217943_n.get(category)) {
                if (soundName == null || isound.getSoundLocation().equals(soundName)) {
                    this.stop(isound);
                }
            }
        } else if (soundName == null) {
            this.stopAllSounds();
        } else {
            for (ISound isound1 : this.playingSoundsChannel.keySet()) {
                if (isound1.getSoundLocation().equals(soundName)) {
                    this.stop(isound1);
                }
            }
        }
    }

    public String getDebugString() {
        return "PlatformAudio Sounds: " + this.playingSoundsChannel.size();
    }
}
