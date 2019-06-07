package net.fornwall.jelf.section;

public class ElfNoBitsSection extends ElfSection {

	protected ElfNoBitsSection(ElfSection s) {
		super(s);
	}

	/**
	 * 	NOBITS sections always have a file size of 0. See {@link #getMemSize()}
	 * 	for the memory size of this section.
	 * 
	 * 	@return Returns 0
	 */
	@Override
	public long getFileSize() {
		return 0;
	}
	
	/**
	 * @return Returns the memory size of this NOBITS section
	 */
	@Override
	public long getMemSize() {
		return super.getFileSize();
	}
}
