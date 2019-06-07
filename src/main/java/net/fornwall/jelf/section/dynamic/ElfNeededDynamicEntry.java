package net.fornwall.jelf.section.dynamic;

public class ElfNeededDynamicEntry extends ElfDynamicEntry {

	protected ElfNeededDynamicEntry(ElfDynamicEntry e) {
		super(e);
	}

	/**
	 * @return Returns the name of the needed library specified by this entry
	 */
	public String getLib() {
		return super.getSection().getStringTable().getString((int)super.getVal());
	}
	
	@Override
	public String toString() {
		return "Shared library: [" + getLib() + "]";
	}
}
