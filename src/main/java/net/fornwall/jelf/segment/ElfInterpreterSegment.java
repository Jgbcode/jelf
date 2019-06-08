package net.fornwall.jelf.segment;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.ElfSection;

public class ElfInterpreterSegment extends ElfSegment {

	private String interpreter;
	
	protected ElfInterpreterSegment(ElfSegment s) {
		super(s);
		
		ElfSection itrp = getInterpreterSection();
		ElfParser p = super.getFile().getParser();
		
		p.seek(itrp.getFileOffset());
		byte[] path = new byte[(int)super.getFileSize()];
		int count = p.read(path);
		
		if(count != super.getFileSize())
			throw new ElfException("Unable to read complete interpreter path");
		
		if(path[path.length - 1] != '\0')
			throw new ElfException("Interpreter path is not null terminated");
		
		this.interpreter = new String(path, 0, path.length - 1);
	}

	/**
	 * @return Returns the section that contains the string defining the 
	 * 	interpreter path
	 */
	public ElfSection getInterpreterSection() {
		ElfSection e = super.getFile().getSectionHeaders().getSectionAtOffset(super.getOffset());
		
		if(e.getFileSize() != super.getFileSize() || e.getFileOffset() != super.getOffset())
			throw new ElfException("Interpreter program header and section header do not match");
		
		return e;
	}
	
	/**
	 * @return Returns the path to the interpreter
	 */
	public String getInterpreter() {
		return interpreter;
	}
}
