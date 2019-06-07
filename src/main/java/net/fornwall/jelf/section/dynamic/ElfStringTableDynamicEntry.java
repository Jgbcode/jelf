package net.fornwall.jelf.section.dynamic;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfStringTableSection;

public class ElfStringTableDynamicEntry extends ElfDynamicEntry {

	protected ElfStringTableDynamicEntry(ElfDynamicEntry e) {
		super(e);
	}

	/**
	 * @return Returns the string table this entry points to
	 */
	public ElfStringTableSection getStringTable() {
		ElfSection s = super.getFile().getSectionHeaders().getSectionAtAddr(super.getVal());
		
		if(s instanceof ElfStringTableSection)
			return (ElfStringTableSection)s;
		throw new ElfException("Dynamic string table was not properly instantiated");
	}
	
	@Override
	public String toString() {
		return "'" + getStringTable().getName() + "' at 0x" + Long.toHexString(super.getVal());
	}
}
