package net.fornwall.jelf.section.dynamic;

import net.fornwall.jelf.section.ElfStringTableSection;

public class ElfStringTableDynamicEntry extends ElfDynamicEntry {

	protected ElfStringTableDynamicEntry(ElfDynamicEntry e) {
		super(e);
	}

	/**
	 * @return Returns the string table this entry points to
	 */
	public ElfStringTableSection getStringTable() {
		return super.getFile().getSectionHeaders().getSectionByIndex((int)super.getAddr(), 
				ElfStringTableSection.class);
	}
	
	@Override
	public String toString() {
		return "'" + getStringTable().getName() + "'";
	}
}
