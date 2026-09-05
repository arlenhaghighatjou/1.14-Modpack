package net.minecraft.util;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    private static final Pattern field_214996_a = Pattern.compile("(.*) \\((\\d*)\\)", 66);
    private static final Pattern field_214997_b = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

    @OnlyIn(Dist.CLIENT)
    public static String func_214992_a(VFile2 p_214992_0_, String p_214992_1_, String p_214992_2_) throws IOException {
        for (char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            p_214992_1_ = p_214992_1_.replace(c0, '_');
        }

        p_214992_1_ = p_214992_1_.replaceAll("[./\"]", "_");
        if (field_214997_b.matcher(p_214992_1_).matches()) {
            p_214992_1_ = "_" + p_214992_1_ + "_";
        }

        Matcher matcher = field_214996_a.matcher(p_214992_1_);
        int j = 0;
        if (matcher.matches()) {
            p_214992_1_ = matcher.group(1);
            j = Integer.parseInt(matcher.group(2));
        }

        if (p_214992_1_.length() > 255 - p_214992_2_.length()) {
            p_214992_1_ = p_214992_1_.substring(0, 255 - p_214992_2_.length());
        }

        while (true) {
            String s = p_214992_1_;
            if (j != 0) {
                String s1 = " (" + j + ")";
                int i = 255 - s1.length();
                if (p_214992_1_.length() > i) {
                    s = p_214992_1_.substring(0, i);
                }

                s = s + s1;
            }

            s = s + p_214992_2_;
            VFile2 file = new VFile2(p_214992_0_, s);

            if (!file.exists()) {
                return s;
            } else {
                ++j;
            }
        }
    }

    public static boolean func_214995_a(VFile2 p_214995_0_) {
        return !p_214995_0_.getPath().contains("..");
    }

    public static boolean func_214994_b(VFile2 p_214994_0_) {
        return true;
    }

    public static VFile2 func_214993_b(VFile2 p_214993_0_, String p_214993_1_, String p_214993_2_) {
        String s = p_214993_1_ + p_214993_2_;
        if (s.endsWith(p_214993_2_) && s.length() == p_214993_2_.length()) {
            throw new RuntimeException("empty resource name");
        } else {
            return new VFile2(p_214993_0_, s);
        }
    }
}
