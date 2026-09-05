package net.lax1dude.eaglercraft.recording;

import net.lax1dude.eaglercraft.internal.PlatformScreenRecord;
import net.lax1dude.eaglercraft.internal.ScreenRecordParameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScreenRecordingController {

    public static final int DEFAULT_FPS = 30;
    public static final int DEFAULT_RESOLUTION = 1;
    public static final int DEFAULT_AUDIO_BITRATE = 120;
    public static final int DEFAULT_VIDEO_BITRATE = 2500;
    public static final float DEFAULT_GAME_VOLUME = 1.0f;
    public static final float DEFAULT_MIC_VOLUME = 0.0f;

    public static final List<EnumScreenRecordingCodec> simpleCodecsOrdered = new ArrayList<>();
    public static final List<EnumScreenRecordingCodec> advancedCodecsOrdered = new ArrayList<>();
    public static final Set<EnumScreenRecordingCodec> codecs = new HashSet<>();
    private static boolean supported = false;

    public static void initialize() {
        simpleCodecsOrdered.clear();
        advancedCodecsOrdered.clear();
        codecs.clear();
        supported = PlatformScreenRecord.isSupported();
        if (supported) {
            EnumScreenRecordingCodec[] codecsOrdered = EnumScreenRecordingCodec.preferred_codec_order;
            for (int i = 0; i < codecsOrdered.length; ++i) {
                EnumScreenRecordingCodec codec = codecsOrdered[i];
                if (PlatformScreenRecord.isCodecSupported(codec)) {
                    if (!codec.advanced) {
                        simpleCodecsOrdered.add(codec);
                    }
                    advancedCodecsOrdered.add(codec);
                    codecs.add(codec);
                }
            }
        }
        if (codecs.isEmpty()) {
            supported = false;
        }
    }

    public static boolean isSupported() {
        return supported;
    }

    public static void setGameVolume(float volume) {
        PlatformScreenRecord.setGameVolume(volume);
    }

    public static void setMicrophoneVolume(float volume) {
        PlatformScreenRecord.setMicrophoneVolume(volume);
    }

    public static void startRecording(ScreenRecordParameters params) {
        PlatformScreenRecord.startRecording(params);
    }

    public static void endRecording() {
        PlatformScreenRecord.endRecording();
    }

    public static boolean isRecording() {
        return PlatformScreenRecord.isRecording();
    }

    public static boolean isMicVolumeLocked() {
        return PlatformScreenRecord.isMicVolumeLocked();
    }

    public static boolean isVSyncLocked() {
        return PlatformScreenRecord.isVSyncLocked();
    }

    public static EnumScreenRecordingCodec getDefaultCodec() {
        return simpleCodecsOrdered.isEmpty() ? (advancedCodecsOrdered.isEmpty() ? null : advancedCodecsOrdered.get(0)) : simpleCodecsOrdered.get(0);
    }

}
