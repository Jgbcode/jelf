package net.fornwall.jelf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;

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
	
	public ElfParser getParser() {
		return parser;
	}

	public ElfHeader getHeader() {
		return header;
	}

	public ElfSectionHeaders getSectionHeaders() {
		return sectionHeaders;
	}
	
	public ElfProgramHeaders getProgramHeaders() {
		return programHeaders;
	}
 
	public static ElfFile fromStream(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int totalRead = 0;
		byte[] buffer = new byte[8096];
		boolean firstRead = true;
		while (true) {
			int readNow = in.read(buffer, totalRead, buffer.length - totalRead);
			if (readNow == -1) {
				return fromBytes(baos.toByteArray());
			} else {
				if (firstRead) {
					// Abort early.
					if (readNow < 4) {
						throw new ElfException("Bad first read");
					} else {
						if (!(0x7f == buffer[0] && 'E' == buffer[1] && 'L' == buffer[2] && 'F' == buffer[3]))
							throw new ElfException("Bad magic number for file");
					}
					firstRead = false;
				}
				baos.write(buffer, 0, readNow);
			}
		}
	}

	public static ElfFile fromFile(File file) throws ElfException, IOException {
		byte[] buffer = new byte[(int) file.length()];
		try (FileInputStream in = new FileInputStream(file)) {
			int totalRead = 0;
			while (totalRead < buffer.length) {
				int readNow = in.read(buffer, totalRead, buffer.length - totalRead);
				if (readNow == -1) {
					throw new ElfException("Premature end of file");
				} else {
					totalRead += readNow;
				}
			}
		}
		return new ElfFile(new ByteArrayInputStream(buffer));
	}

	public static ElfFile fromBytes(byte[] buffer) throws ElfException, IOException {
		return new ElfFile(new ByteArrayInputStream(buffer));
	}
    public ElfFile(MappedByteBuffer buffer, long startPosition) throws ElfException, IOException {
        this.parser = new ElfParser(buffer, startPosition);
        parse(parser);
    }
    

	public ElfFile(ByteArrayInputStream baos) throws ElfException, IOException {
		this.parser = new ElfParser(baos);
		parse(parser);
	}
	
	/**
	 *	Helper method for parsing sections and program headers
	 */
	private void parse(ElfParser parser) {
		this.header = parser.getHeader();
		sectionHeaders = new ElfSectionHeaders(this);
		programHeaders = new ElfProgramHeaders(this);
	}
}
