package net.fornwall.jelf.section;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfParser;

public class ElfStringTableSection extends ElfSection {
	/** The string table data. */
	private final byte data[];
	private final int numStrings;
	
	protected ElfStringTableSection(ElfSection s) {
		super(s);
		
		ElfParser parser = s.getFile().getParser();
		long offset = s.getFileOffset();
		long length = s.getFileSize();
		
		parser.seek(offset);
		data = new byte[(int)length];
		int bytesRead = parser.read(data);
		if (bytesRead != length)
			throw new ElfException("Error reading string table (read " + bytesRead + "bytes - expected to " + "read " + data.length + "bytes)");

		int stringsCount = 0;
		
		// Remove leading zeros
		int ptr = 0;
		while(ptr < data.length && data[ptr] == '\0') ptr++;
		
		while(ptr < data.length) {
			if (data[ptr] == '\0') stringsCount++;
			
			// Remove padding
			while(ptr < data.length && data[ptr++] == '\0');
		}
		
		numStrings = stringsCount;
	}

	/**
	 * @param offset the offset of the string in the string table
	 * @return Returns the string at the provided offset
	 */
	public String getString(int offset) {
		int startPtr = offset;
		int endPtr = offset;
		while (data[endPtr] != '\0')
			endPtr++;
		return new String(data, startPtr, endPtr - startPtr);
	}
	
	/**
	 * @return Returns the number of string in the string table
	 */
	public int getStringCount() {
		return numStrings;
	}
}
