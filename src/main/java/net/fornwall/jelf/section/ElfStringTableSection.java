package net.fornwall.jelf.section;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.Table;
import net.fornwall.jelf.Table.Align;

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
	
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this section.
	 */
	public Table getFormattedTable() {
		Table t = new Table("String table section \'" + getName() + "\' at offset 0x" + 
				Long.toHexString(getFileOffset()) + " contains " + getStringCount() + " entries");
		
		// Column names
		t.add("Offset");
		t.setColAlign(Align.RIGHT);
		
		t.add("Size");
		t.setColAlign(Align.RIGHT);
		
		t.add("String");
		t.setColAlign(Align.LEFT);
		
		int offset = 0;
		for(int i = 0; i < getStringCount();) {
			String str = getString(offset);
			if(str.isEmpty()) {
				offset++;
				continue;
			}
			i++;
			
			t.newRow();
			
			// Offset
			t.add("0x" + Integer.toHexString(offset));
			
			// Size
			t.add("0x" + Integer.toHexString(str.length()));
			
			// String
			t.add(str);
			
			offset += str.length();
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
