package net.minecraft.data;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface IDataProvider {
    HashFunction HASH_FUNCTION = new HashFunction() {
        public com.google.common.hash.Hasher newHasher() {
            return null;
        }

        public com.google.common.hash.Hasher newHasher(int expectedInputSize) {
            return null;
        }

        @Override
        public HashCode hashInt(int input) {
            return null;
        }

        @Override
        public HashCode hashLong(long input) {
            return null;
        }

        @Override
        public HashCode hashBytes(byte[] input) {
            return null;
        }

        @Override
        public HashCode hashBytes(byte[] input, int off, int len) {
            return null;
        }

        public com.google.common.hash.HashCode hashUnencodedChars(CharSequence input) {
            return com.google.common.hash.HashCode.fromInt(input.toString().hashCode());
        }

        @Override
        public HashCode hashString(CharSequence input, Charset charset) {
            return null;
        }

        @Override
        public <T> HashCode hashObject(T instance, Funnel<? super T> funnel) {
            return null;
        }

        @Override
        public int bits() {
            return 0;
        }
    };

    static void save(Gson gson, DirectoryCache cache, JsonElement jsonElement, Path pathIn) throws IOException {
        String s = gson.toJson(jsonElement);
        String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
        if (!Objects.equals(cache.getPreviousHash(pathIn), s1) || !Files.exists(pathIn)) {
            Files.createDirectories(pathIn.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(pathIn)) {
                bufferedwriter.write(s);
            }
        }

        cache.func_208316_a(pathIn, s1);
    }

    void act(DirectoryCache cache) throws IOException;

    String getName();
}
