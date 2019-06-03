package net.fornwall.jelf.section;

import java.io.IOException;

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
		int bytesRead;
		try {
			bytesRead = parser.read(data);
		} catch (IOException e) {
			throw new ElfException("Error parsing string table section. " + e.getMessage());
		}
		if (bytesRead != length)
			throw new ElfException("Error reading string table (read " + bytesRead + "bytes - expected to " + "read " + data.length + "bytes)");

		int stringsCount = 0;
		for (int ptr = 0; ptr < data.length; ptr++)
			if (data[ptr] == '\0') stringsCount++;
		numStrings = stringsCount;
	}

	public String getString(int index) {
		int startPtr = index;
		int endPtr = index;
		while (data[endPtr] != '\0')
			endPtr++;
		return new String(data, startPtr, endPtr - startPtr);
	}
	
	public int getStringCount() {
		return numStrings;
	}
}
