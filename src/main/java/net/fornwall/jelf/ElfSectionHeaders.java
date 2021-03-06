package net.fornwall.jelf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfStringTableSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;

/**
 *	The collection of sections contained in a {@link ElfFile}
 */
public class ElfSectionHeaders {
	private ElfFile file;
	private ElfSection[] sections;
	
	// String to section index
	private HashMap<String, Integer> sectionByName;
	
	// Special section
	private ElfStringTableSection sectionStringTable;
	
	public ElfSectionHeaders(ElfFile file) {
		this.file = file;
		ElfHeader h = file.getHeader();
		
		this.sections = new ElfSection[h.getSectionHeaderEntryCount()];
		for (int i = 0; i < sections.length; i++) {
			long sectionHeaderOffset = h.getSectionHeaderOffset() + (i * h.getSectionHeaderEntrySize());
			sections[i] = ElfSection.sectionFactory(file, sectionHeaderOffset);
		}
		
		// Get section string table
		if(!(sections[h.getSectionHeaderStringTableIndex()] instanceof ElfStringTableSection))
			throw new ElfException("Invalid section header string table");
		
		sectionStringTable = (ElfStringTableSection)sections[h.getSectionHeaderStringTableIndex()];
		
		sectionByName = new HashMap<String, Integer>();
		for(int i = 0; i < sections.length; i++) {
			if(sectionByName.put(sectionStringTable.getString(sections[i].getNameIndex()), i) != null) {
				throw new ElfException("Duplicate section entry: " + 
						sectionStringTable.getString(sections[i].getNameIndex()));
			}
		}
	}
	
	/**
	 * @return Returns the number of sections
	 */
	public int size() {
		return sections.length;
	}
	
	/**
	 * @return Returns the file that contains these section headers
	 */
	public ElfFile getFile() {
		return this.file;
	}
	
	/**
	 * @return Returns the section at the provided index
	 */
	public ElfSection getSectionByIndex(int index) {
		if(index < 0 || index >= sections.length)
			throw new ElfException("Section index out of bounds: " + index);
		return sections[index];
	}
	
	/**
	 * @param c - the section to auto cast to
	 * @return Returns a section that can be casted to the requested type
	 * @throws ElfException if the cast is invalid
	 */
	public <T extends ElfSection> T getSectionByIndex(int index, Class<T> c) {
		ElfSection s = getSectionByIndex(index);
		if(s == null)
			return null;
		
		if(!c.isInstance(s))
			throw new ElfException("Invalid section. " + s.getName() + " is not instance of " + c.getCanonicalName());
		return c.cast(s);
	}
	
	/**
	 * @return Returns the section with the provided name
	 */
	public ElfSection getSectionByName(String name) {
		Integer index = sectionByName.get(name);
		if(index == null)
			throw new ElfException("Unknown section name: " + name);
		return getSectionByIndex(index);
	}
	
	/**
	 * @param c - the section to cast to
	 * @return Returns a section that can be casted to the requested type
	 * @throws ElfException if the cast is invalid
	 */
	public <T extends ElfSection> T getSectionByName(String name, Class<T> c) {
		ElfSection s = getSectionByName(name);
		if(s == null)
			return null;
		
		if(!c.isInstance(s))
			throw new ElfException("Invalid section. " + name + " is not instance of " + c.getCanonicalName());
		return c.cast(s);
	}
	
	/**
	 * @return Returns the index of the requested section or null if it does not exist
	 */
	public Integer getSectionIndexByName(String name) {
		return sectionByName.get(name);
	}
	
	// Special section getters
	
	/**
	 * @return Returns the string table which contains section names
	 */
	public ElfStringTableSection getSectionStringTable() {
		return sectionStringTable;
	}
	
	/**
	 * @return Returns the main string table
	 */
	public ElfStringTableSection getStringTable() {
		return (ElfStringTableSection)this.getSectionByName(".strtab", ElfStringTableSection.class);
	}
	
	/**
	 * @return Returns the main symbol table
	 */
	public ElfSymbolTableSection getSymbolTable() {
		return (ElfSymbolTableSection)this.getSectionByName(".symtab", ElfSymbolTableSection.class);
	}
	
	
	
	/**
	 * @param offset the offset in the file
	 * @return Returns the section which contains the byte located at this specific offset
	 */
	public ElfSection getSectionAtOffset(long offset) {
		return sections[getSectionIndexAtOffset(offset)];
	}
	
	/**
	 * @param offset offset the offset in the file
	 * @return Returns the index of the section which contains the byte located at this specific offset
	 */
	public int getSectionIndexAtOffset(long offset) {
		return getSectionIndexAtOffset(offset, 0, sections.length);
	}
	
	// Helper
	private int getSectionIndexAtOffset(long offset, int start, int end) {
		// Assumes sections are ordered according to file offset in this.sections
		if(start >= end) {
			// Check for NULL section
			if(offset >= 0 && sections.length > 1 && offset < sections[1].getFileOffset())
				return 0;
			
			throw new ElfException("No section found at offset " + offset);
		}
		
		int mid = (start + end) / 2;
		ElfSection s = sections[mid];
		if(offset >= s.getFileOffset() && offset < s.getFileOffset() + s.getFileSize())
			return mid;
		else if(offset < s.getFileOffset())
			return getSectionIndexAtOffset(offset, start, mid);
		else
			return getSectionIndexAtOffset(offset, mid + 1, end);
	}
	
	/**
	 * @param addr the address to look at
	 * @return Returns the section index of the section at the provided address
	 * @throws ElfException if the no such section exists
	 */
	public int getSectionIndexAtAddr(long addr) {
		for(int i = 0; i < sections.length; i++) {
			if(addr >= sections[i].getAddress() && addr < sections[i].getAddress() + sections[i].getMemSize())
				return i;
		}
		
		throw new ElfException("No section exists at address 0x" + Long.toHexString(addr));
	}
	
	/**
	 * @param addr the address to look at
	 * @return Returns the section at the provided address
	 * @throws ElfException if the no such section exists
	 */
	public ElfSection getSectionAtAddr(long addr) {
		return sections[getSectionIndexAtAddr(addr)];
	}
	
	/**
	 * @param c the type of section to fetch
	 * @return Returns a list of sections that are an instance of the provided class
	 */
	public <T extends ElfSection> List<T> getSectionsOfType(Class<T> c) {
		List<T> result = new ArrayList<T>();
		for(ElfSection s : sections) {
			if(c.isInstance(s))
				result.add(c.cast(s));
		}
		return result;
	}
	
	/**
	 * @param t the type of the section to fetch
	 * @return Returns a list of sections that are of the provided type
	 */
	public List<ElfSection> getSectionsOfType(ElfSection.Type t) {
		List<ElfSection> result = new ArrayList<ElfSection>();
		for(ElfSection s : sections) {
			if(s.getType().equals(t))
				result.add(s);
		}
		return result;
	}
	
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this object.
	 */
	public Table getFormattedTable() {
		Table t = new Table("Section Headers:");
		
		// Column names
		t.add("[Nr]");
		t.setColAlign(Align.RIGHT);
		
		t.add("Name");
		t.setColAlign(Align.LEFT);
		
		t.add("Size");
		t.setColAlign(Align.RIGHT);
		
		t.add("Type");
		t.setColAlign(Align.LEFT);
		
		t.add("EntSize");
		t.setColAlign(Align.RIGHT);
		
		t.add("Address");
		t.setColAlign(Align.RIGHT);
		
		t.add("Flags");
		t.setColAlign(Align.LEFT);
		
		t.add("Link");
		t.setColAlign(Align.RIGHT);
		
		t.add("Info");
		t.setColAlign(Align.RIGHT);
		
		t.add("Offset");
		t.setColAlign(Align.RIGHT);
		
		t.add("Align");
		t.setColAlign(Align.RIGHT);
		
		for(int i = 0; i < file.getSectionHeaders().size(); i++) {
			t.newRow();
			
			ElfSection s = file.getSectionHeaders().getSectionByIndex(i);
			
			// Index
			t.add("[" + i + "]");
			
			// Name
			t.add(s.getName());
			
			// Size
			t.add("0x" + Long.toHexString(s.getFileSize()));
			
			// Type
			t.add(s.getType().name());
			
			// EntSize
			t.add("0x" + Long.toHexString(s.getEntrySize()));
			
			// Address
			t.add("0x" + Long.toHexString(s.getAddress()));
			
			// Flags
			t.add(s.getFlags().name());
			
			// Link
			t.add(Integer.toString(s.getLinkIndex()));
			
			// Info
			t.add(Integer.toString(s.getInfo()));
			
			// Offset
			t.add("0x" + Long.toHexString(s.getFileOffset()));
			
			// Align
			t.add(Long.toString(s.getAlignment()));
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		// Add a key for the flags
		String flagKey = "Key to Flags:\n";
		for(String s : ElfSection.Flag.getNameKey().split("\n"))
			flagKey += " " + s + "\n";
		return this.getFormattedTable().toString() + flagKey;
	}
}
