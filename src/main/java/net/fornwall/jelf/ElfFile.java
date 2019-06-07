package net.fornwall.jelf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.file.Files;

/**
 * An ELF (Executable and Linkable Format) file can be a relocatable, executable, shared or core file.
 * 
 * <pre>
 * http://man7.org/linux/man-pages/man5/elf.5.html
 * http://en.wikipedia.org/wiki/Executable_and_Linkable_Format
 * http://www.ibm.com/developerworks/library/l-dynamic-libraries/
 * http://downloads.openwatcom.org/ftp/devel/docs/elf-64-gen.pdf
 * 
 * Elf64_Addr, Elf64_Off, Elf64_Xword, Elf64_Sxword: 8 bytes
 * Elf64_Word, Elf64_Sword: 4 bytes
 * Elf64_Half: 2 bytes
 * </pre>
 */
public final class ElfFile {
	/** Elf parser */
	private ElfParser parser;
	
	/** Elf header data */
	private ElfHeader header;
	
	/** Elf section headers */
	private ElfSectionHeaders sectionHeaders;
	
	/** Elf program headers */
	private ElfProgramHeaders programHeaders;

	public ElfFile(File file) throws IOException {
		this(Files.readAllBytes(file.toPath()));
	}

	public ElfFile(byte[] buffer) {
		this(new ByteArrayInputStream(buffer));
	}
	
    public ElfFile(MappedByteBuffer buffer, long startPosition) {
        this.parser = new ElfParser(buffer, startPosition);
		this.header = parser.getHeader();
		sectionHeaders = new ElfSectionHeaders(this);
		programHeaders = new ElfProgramHeaders(this);
    }

	public ElfFile(ByteArrayInputStream baos) {
		this.parser = new ElfParser(baos);
		this.header = parser.getHeader();
		sectionHeaders = new ElfSectionHeaders(this);
		programHeaders = new ElfProgramHeaders(this);
	}
	
	/**
	 * @return Returns the {@link ElfParser} associated with this file
	 */
	public ElfParser getParser() {
		return parser;
	}

	/**
	 * @return Returns the {@link ElfHeader} associated with this file
	 */
	public ElfHeader getHeader() {
		return header;
	}

	/**
	 * @return Returns the {@link ElfSectionHeaders} associated with this file
	 */
	public ElfSectionHeaders getSectionHeaders() {
		return sectionHeaders;
	}
	
	/**
	 * @return Returns the {@link ElfProgramHeaders} associated with this file
	 */
	public ElfProgramHeaders getProgramHeaders() {
		return programHeaders;
	}
}
