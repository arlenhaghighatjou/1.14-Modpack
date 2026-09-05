package io.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Copyright (c) 2022 lax1dude. All Rights Reserved.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
public class ByteBufEaglercraftImpl extends AbstractByteBuf {

    private ByteBuffer internal;

    public ByteBufEaglercraftImpl(ByteBuffer internal, int maxCapacity) {
        super(maxCapacity);
        if (internal.order() != ByteOrder.BIG_ENDIAN) {
            this.internal = internal.order(ByteOrder.BIG_ENDIAN);
        } else {
            this.internal = internal;
        }
    }

    @Override
    protected byte _getByte(int index) {
        return internal.get(index);
    }

    @Override
    protected short _getShort(int index) {
        return internal.getShort(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return ((internal.get(index) & 0xFF) << 16) | ((internal.get(index + 1) & 0xFF) << 8)
                | (internal.get(index + 2) & 0xFF);
    }

    @Override
    protected int _getInt(int index) {
        return internal.getInt(index);
    }

    @Override
    protected long _getLong(int index) {
        return internal.getLong(index);
    }

    @Override
    protected void _setByte(int index, int value) {
        internal.put(index, (byte) value);
    }

    @Override
    protected void _setShort(int index, int value) {
        internal.putShort(index, (short) value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        internal.put(index, (byte) ((value >>> 16) & 0xFF));
        internal.put(index + 1, (byte) ((value >>> 8) & 0xFF));
        internal.put(index + 2, (byte) (value & 0xFF));
    }

    @Override
    protected void _setInt(int index, int value) {
        internal.putInt(index, value);
    }

    @Override
    protected void _setLong(int index, long value) {
        internal.putLong(index, value);
    }

    @Override
    public int capacity() {
        return internal.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        if (newCapacity > internal.capacity()) {
            ByteBuffer newCap = ByteBuffer.wrap(new byte[(int) (newCapacity * 1.5f)]);
            NioBufferFunctions.put(newCap, 0, internal, 0, internal.capacity());
            newCap.clear();
            internal = newCap;
        }
        return this;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        throw new UnsupportedOperationException("Not supported as it is not used by Eaglercraft");
    }

    @Override
    public ByteBuf unwrap() {
        return this;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        if (dst instanceof ByteBufEaglercraftImpl) {
            NioBufferFunctions.put(((ByteBufEaglercraftImpl) dst).internal, dstIndex, internal, index, length);
        } else {
            byte[] tmp = new byte[length];
            NioBufferFunctions.get(internal, index, tmp);
            dst.setBytes(dstIndex, tmp, 0, length);
        }
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        NioBufferFunctions.get(internal, index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        NioBufferFunctions.put(dst, dst.position(), internal, index, dst.remaining());
        dst.position(dst.limit());
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        byte[] buf = new byte[length];
        NioBufferFunctions.get(internal, index, buf);
        out.write(buf);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        if (src instanceof ByteBufEaglercraftImpl) {
            NioBufferFunctions.put(internal, index, ((ByteBufEaglercraftImpl) src).internal, srcIndex, length);
        } else {
            byte[] tmp = new byte[length];
            src.getBytes(srcIndex, tmp);
            NioBufferFunctions.put(internal, index, tmp, 0, length);
        }
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        NioBufferFunctions.put(internal, index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        NioBufferFunctions.put(internal, index, src, src.position(), src.remaining());
        src.position(src.limit());
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        byte[] buf = new byte[length];
        int r = in.read(buf, 0, length);
        if (r > 0) {
            NioBufferFunctions.put(internal, index, buf, 0, r);
        }
        return r;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        byte[] cpy = new byte[length];
        NioBufferFunctions.get(internal, index, cpy);
        ByteBufEaglercraftImpl buf = new ByteBufEaglercraftImpl(ByteBuffer.wrap(cpy), maxCapacity());
        buf.writerIndex = length;
        return buf;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        ByteBuffer buf = ByteBuffer.allocate(length);
        byte[] arr = new byte[length];
        internal.position(index);
        internal.get(arr, 0, length);
        buf.put(arr);
        buf.flip();
        return buf;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        internal.position(index).limit(index + length);
        return internal;
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{nioBuffer(index, length)};
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        return internal.array();
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        return 0;
    }

    @Override
    public ByteBuf slice(int index, int length) {
        // return new ByteBufEaglercraftImpl(internal.slice(index, length),
        // maxCapacity());
        throw new UnsupportedOperationException("Not supported in JDK 8");
    }

    @Override
    public ByteBuf duplicate() {
        ByteBufEaglercraftImpl dup = new ByteBufEaglercraftImpl(internal.duplicate(), maxCapacity());
        dup.setIndex(readerIndex, writerIndex);
        return dup;
    }

}
