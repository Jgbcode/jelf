package net.fornwall.jelf.section;

import net.fornwall.jelf.section.relocation.ElfRelocation;

public class ElfRelocationSection extends ElfSection {
	
	private ElfRelocation[] relocations;
	
	public ElfRelocationSection(ElfSection s) {
		super(s);
	}
	
	/**
	 * 
	 */
	
	/**
	 * @return Returns the index of the {@link ElfSymbolTableSection} associated with this
	 * 	relocation section
	 */
	public int getSymbolTableIndex() {
		return super.getLinkIndex();
	}
	
	/**
	 * @return Returns the {@link ElfSymbolTableSection} associated with this
	 * 	relocation section
	 */
	public ElfSymbolTableSection getSymbolTable() {
		return (ElfSymbolTableSection)super.getLink(ElfSymbolTableSection.class);
	}
	
	/**
	 * @return Returns the section header index of the {@link ElfSection} to which the 
	 * 	relocation applies
	 */
	public int getSectionIndex() {
		return super.getInfo();
	}
	
	/**
	 * @return Returns the {@link ElfSection} to which the relocation applies
	 */
	public ElfSection getSection() {
		return super.getFile().getSectionHeaders().getSectionByIndex(super.getInfo());
	}
}
