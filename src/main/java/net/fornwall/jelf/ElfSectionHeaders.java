package net.fornwall.jelf;

import java.util.HashMap;

import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfStringTableSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;

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
			sections[i] = ElfSection.elfSectionFactory(file, sectionHeaderOffset);
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
	 * @return Returns the file these section headers are related to
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
	 * @return Returns the section with the provided name
	 */
	public ElfSection getSectionByName(String name) {
		Integer index = sectionByName.get(name);
		if(index == null)
			throw new ElfException("Unknown section name: " + name);
		return getSectionByIndex(index);
	}
	
	/**
	 * @param c - the section to auto cast to
	 * @return Returns a section that can be casted to the requested type
	 */
	public ElfSection getSectionByName(String name, Class<? extends ElfSection> c) {
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
}
