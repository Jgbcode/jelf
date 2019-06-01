package net.fornwall.jelf;

import java.util.HashMap;

import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfStringTableSection;

public class ElfSectionHeaders {
	private ElfFile file;
	private ElfSection[] sections;
	
	// String to section index
	private HashMap<String, Integer> sectionByName;
	
	public ElfSectionHeaders(ElfFile file) {
		this.file = file;
		ElfHeader h = file.getHeader();
		
		this.sections = new ElfSection[h.getSectionHeaderEntryCount()];
		for (int i = 0; i < sections.length; i++) {
			long sectionHeaderOffset = h.getSectionHeaderOffset() + (i * h.getSectionHeaderEntrySize());
			sections[i] = ElfSection.elfSectionFactory(file, sectionHeaderOffset);
		}
		
		if(!(sections[h.getSectionHeaderStringTableIndex()] instanceof ElfStringTableSection))
			throw new ElfException("Invalid section header string table");
		
		ElfStringTableSection strTab = (ElfStringTableSection)sections[h.getSectionHeaderStringTableIndex()];
		
		sectionByName = new HashMap<String, Integer>();
		for(int i = 0; i < sections.length; i++)
			sectionByName.put(strTab.getString(sections[i].getNameIndex()), i);
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
	 * @return Returns the index of the requested section or null if it does not exist
	 */
	public Integer getSectionIndexByName(String name) {
		return sectionByName.get(name);
	}
	
	/**
	 * @return Returns the string table that holds the names for all sections
	 */
	public ElfStringTableSection getNameStringTableSection() {
		return (ElfStringTableSection)getSectionByIndex(file.getHeader().getSectionHeaderStringTableIndex());
	}
}
