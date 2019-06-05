package net.fornwall.jelf.section.relocation;

import net.fornwall.jelf.section.ElfRelocationSection;

public class ElfAddendRelocation extends ElfRelocation {

	private long addend;
	
	protected ElfAddendRelocation(ElfRelocationSection table, long offset, Class<? extends Type> c) {
		super(table, offset, c);
		
		addend = table.getFile().getParser().readIntOrLong();
	}
	
	/**
	 * This member specifies a constant addend used to compute the value to be stored into the
	 * relocatable field.
	 *
	 * @return Returns the addend
	 */
	public long getAddend() {
		return addend;
	}
}
