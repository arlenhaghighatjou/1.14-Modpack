package net.lax1dude.eaglercraft.internal;

import net.lax1dude.eaglercraft.recording.EnumScreenRecordingCodec;

public class ScreenRecordParameters {

    public final EnumScreenRecordingCodec codec;
    public final int resolutionDivisior;
    public final int videoBitsPerSecond;
    public final int audioBitsPerSecond;
    public final int captureFrameRate;

    public ScreenRecordParameters(EnumScreenRecordingCodec codec, int resolutionDivisior, int videoBitsPerSecond,
                                  int audioBitsPerSecond, int captureFrameRate) {
        this.codec = codec;
        this.resolutionDivisior = resolutionDivisior;
        this.videoBitsPerSecond = videoBitsPerSecond;
        this.audioBitsPerSecond = audioBitsPerSecond;
        this.captureFrameRate = captureFrameRate;
    }

}
