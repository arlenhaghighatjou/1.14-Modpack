package net.lax1dude.eaglercraft.internal.teavm;

import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Int32Array;

public interface WebGLMultiDraw extends JSObject {

    void multiDrawArraysWEBGL(int mode, Int32Array firsts, int firstsOffset, Int32Array counts,
                              int countsOffset, int drawCount);

    void multiDrawElementsWEBGL(int mode, Int32Array counts, int countsOffset, int type, Int32Array offsets,
                                int offsetsOffset, int drawCount);

}
