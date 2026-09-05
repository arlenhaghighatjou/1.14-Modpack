package net.minecraft.nbt;

import net.lax1dude.eaglercraft.EaglerZLIB;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.*;

public class CompressedStreamTools {
    public static CompoundNBT readCompressed(InputStream is) throws IOException {
        CompoundNBT compoundnbt;
        try (DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(EaglerZLIB.newGZIPInputStream(is)))) {
            compoundnbt = read(datainputstream, NBTSizeTracker.INFINITE);
        }

        return compoundnbt;
    }

    public static void writeCompressed(CompoundNBT compound, OutputStream outputStream) throws IOException {
        try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(EaglerZLIB.newGZIPOutputStream(outputStream)))) {
            write(compound, dataoutputstream);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void safeWrite(CompoundNBT compound, VFile2 fileIn) throws IOException {
        VFile2 file1 = new VFile2(fileIn.getPath() + "_tmp");
        if (file1.exists()) {
            file1.delete();
        }

        write(compound, file1);
        if (fileIn.exists()) {
            fileIn.delete();
        }

        if (fileIn.exists()) {
            throw new IOException("Failed to delete " + fileIn);
        } else {
            file1.renameTo(fileIn);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void write(CompoundNBT compound, VFile2 fileIn) throws IOException {
        DataOutputStream dataoutputstream = new DataOutputStream(fileIn.getOutputStream());

        try {
            write(compound, dataoutputstream);
        } finally {
            dataoutputstream.close();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static CompoundNBT read(VFile2 fileIn) throws IOException {
        if (!fileIn.exists()) {
            return null;
        } else {
            DataInputStream datainputstream = new DataInputStream(fileIn.getInputStream());

            CompoundNBT compoundnbt;
            try {
                compoundnbt = read(datainputstream, NBTSizeTracker.INFINITE);
            } finally {
                datainputstream.close();
            }

            return compoundnbt;
        }
    }

    public static CompoundNBT read(DataInputStream inputStream) throws IOException {
        return read(inputStream, NBTSizeTracker.INFINITE);
    }

    public static CompoundNBT read(DataInput input, NBTSizeTracker accounter) throws IOException {
        INBT inbt = read(input, 0, accounter);
        if (inbt instanceof CompoundNBT) {
            return (CompoundNBT) inbt;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(CompoundNBT compound, DataOutput output) throws IOException {
        writeTag(compound, output);
    }

    private static void writeTag(INBT tag, DataOutput output) throws IOException {
        output.writeByte(tag.getId());
        if (tag.getId() != 0) {
            output.writeUTF("");
            tag.write(output);
        }
    }

    private static INBT read(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
        byte b0 = input.readByte();
        if (b0 == 0) {
            return new EndNBT();
        } else {
            input.readUTF();
            INBT inbt = INBT.create(b0);

            try {
                inbt.read(input, depth, accounter);
                return inbt;
            } catch (IOException ioexception) {
                CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
                crashreportcategory.addDetail("Tag type", b0);
                throw new ReportedException(crashreport);
            }
        }
    }
}
