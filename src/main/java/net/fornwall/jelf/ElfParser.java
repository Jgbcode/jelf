package net.fornwall.jelf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;

/** Package internal class used for parsing ELF files. */
class ElfParser {

	public final ElfHeader header;
	private final ByteArrayInputStream fsFile;

    private final MappedByteBuffer mappedByteBuffer;
    private final long mbbStartPosition;


	ElfParser(ByteArrayInputStream fsFile) throws ElfException, IOException {
		this.fsFile = fsFile;
        mappedByteBuffer = null;
        mbbStartPosition = -1;
        header = new ElfHeader(this);
        header.parse();
    }

    ElfParser(MappedByteBuffer byteBuffer, long mbbStartPos) throws ElfException, IOException {
        mappedByteBuffer = byteBuffer;
        mbbStartPosition = mbbStartPos;
        mappedByteBuffer.position((int)mbbStartPosition);
        fsFile = null;
        header = new ElfHeader(this);
        header.parse();
	}

	public void seek(long offset) {
        if (fsFile != null) {
    		fsFile.reset();
	    	if (fsFile.skip(offset) != offset) throw new ElfException("seeking outside file");
        }
        else if (mappedByteBuffer != null) {
            mappedByteBuffer.position((int)(mbbStartPosition + offset)); // we may be limited to sub-4GB mapped filess
        }
	}

	/**
	 * Signed byte utility functions used for converting from big-endian (MSB) to little-endian (LSB).
	 */
	short byteSwap(short arg) {
		return (short) ((arg << 8) | ((arg >>> 8) & 0xFF));
	}

	int byteSwap(int arg) {
		return ((byteSwap((short) arg)) << 16) | (((byteSwap((short) (arg >>> 16)))) & 0xFFFF);
	}

	long byteSwap(long arg) {
		return ((((long) byteSwap((int) arg)) << 32) | (((long) byteSwap((int) (arg >>> 32))) & 0xFFFFFFFF));
	}

	short readUnsignedByte() {
        int val = -1;
        if (fsFile != null) {
            val = fsFile.read();
        } else if (mappedByteBuffer != null) {
            byte temp = mappedByteBuffer.get();
            val = temp & 0xFF; // bytes are signed in Java =_= so assigning them to a longer type risks sign extension.
        }

		if (val < 0) throw new ElfException("Trying to read outside file");
		return (short) val;
	}

	short readShort() throws ElfException {
		int ch1 = readUnsignedByte();
		int ch2 = readUnsignedByte();
		short val = (short) ((ch1 << 8) + (ch2 << 0));
		if (header.ei_data == ElfHeader.Data.ELFDATA2LSB) val = byteSwap(val);
		return val;
	}

	int readInt() throws ElfException {
		int ch1 = readUnsignedByte();
		int ch2 = readUnsignedByte();
		int ch3 = readUnsignedByte();
		int ch4 = readUnsignedByte();
		int val = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));

		if (header.ei_data == ElfHeader.Data.ELFDATA2LSB) val = byteSwap(val);
		return val;
	}

	long readLong() {
		int ch1 = readUnsignedByte();
		int ch2 = readUnsignedByte();
		int ch3 = readUnsignedByte();
		int ch4 = readUnsignedByte();
		int val1 = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
		int ch5 = readUnsignedByte();
		int ch6 = readUnsignedByte();
		int ch7 = readUnsignedByte();
		int ch8 = readUnsignedByte();
		int val2 = ((ch5 << 24) + (ch6 << 16) + (ch7 << 8) + (ch8 << 0));

		long val = ((long) (val1) << 32) + (val2 & 0xFFFFFFFFL);
		if (header.ei_data == ElfHeader.Data.ELFDATA2LSB) val = byteSwap(val);
		return val;
	}

	/** Read four-byte int or eight-byte long depending on if {@link ElfFile#objectSize}. */
	long readIntOrLong() {
		return header.ei_class == ElfHeader.Class.ELFCLASS32 ? readInt() : readLong();
	}

	/** Returns a big-endian unsigned representation of the int. */
	long unsignedByte(int arg) {
		long val;
		if (arg >= 0) {
			val = arg;
		} else {
			val = (unsignedByte((short) (arg >>> 16)) << 16) | ((short) arg);
		}
		return val;
	}

	public int read(byte[] data) throws IOException {
        if (fsFile != null) {
            return fsFile.read(data);
        } else if (mappedByteBuffer != null) {
            mappedByteBuffer.get(data);
            return data.length;
        }
        throw new IOException("No way to read from file or buffer");
	}

}