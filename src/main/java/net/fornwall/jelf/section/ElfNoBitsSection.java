package net.fornwall.jelf.section;

public class ElfNoBitsSection extends ElfSection {

	protected ElfNoBitsSection(ElfSection s) {
		super(s);
	}

	/**
	 * @returns NOBITS sections always have a file size of 0.
	 */
	@Override
	public long getFileSize() {
		return 0;
	}
	
	/**
	 * @return Returns the memory size of this NOBITS section
	 */
	public long getMemSize() {
		return super.getFileSize();
	}
}
