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
	/** Elf header data */
	public final ElfHeader header;
	
	/** Elf parser */
	public final ElfParser parser;
	
	/** MemoizedObject array of section headers associated with this ELF file. */
	private MemoizedObject<ElfSection>[] sectionHeaders;
	/** MemoizedObject array of program headers associated with this ELF file. */
	private MemoizedObject<ElfSegment>[] programHeaders;

	/** Used to cache symbol table lookup. */
	private ElfSection symbolTableSection;
	/** Used to cache dynamic symbol table lookup. */
	private ElfSection dynamicSymbolTableSection;

	private ElfSection dynamicLinkSection;

	/**
	 * Returns the section header at the specified index. The section header at index 0 is defined as being a undefined
	 * section.
	 */
	public ElfSection getSection(int index) throws ElfException, IOException {
		return sectionHeaders[index].getValue();
	}

	/** Returns the section header string table associated with this ELF file. */
	public ElfStringTable getSectionNameStringTable() throws ElfException, IOException {
		return getSection(header.e_shstrndx).getStringTable();
	}

	/** Returns the string table associated with this ELF file. */
	public ElfStringTable getStringTable() throws ElfException, IOException {
		return findStringTableWithName(ElfSection.STRING_TABLE_NAME);
	}

	/**
	 * Returns the dynamic symbol table associated with this ELF file, or null if one does not exist.
	 */
	public ElfStringTable getDynamicStringTable() throws ElfException, IOException {
		return findStringTableWithName(ElfSection.DYNAMIC_STRING_TABLE_NAME);
	}

	private ElfStringTable findStringTableWithName(String tableName) throws ElfException, IOException {
		// Loop through the section header and look for a section
		// header with the name "tableName". We can ignore entry 0
		// since it is defined as being undefined.
		for (int i = 1; i < header.e_shnum; i++) {
			ElfSection sh = getSection(i);
			if (tableName.equals(sh.getName())) return sh.getStringTable();
		}
		return null;
	}

	/** The {@link ElfSection#SHT_SYMTAB} section (of which there may be only one), if any. */
	public ElfSection getSymbolTableSection() throws ElfException, IOException {
		return (symbolTableSection != null) ? symbolTableSection : (symbolTableSection = getSymbolTableSection(ElfSection.SHT_SYMTAB));
	}

	/** The {@link ElfSection#SHT_DYNSYM} section (of which there may be only one), if any. */
	public ElfSection getDynamicSymbolTableSection() throws ElfException, IOException {
		return (dynamicSymbolTableSection != null) ? dynamicSymbolTableSection : (dynamicSymbolTableSection = getSymbolTableSection(ElfSection.SHT_DYNSYM));
	}

	/** The {@link ElfSection#SHT_DYNAMIC} section (of which there may be only one). Named ".dynamic". */
	public ElfSection getDynamicLinkSection() throws IOException {
		return (dynamicLinkSection != null) ? dynamicLinkSection : (dynamicLinkSection = getSymbolTableSection(ElfSection.SHT_DYNAMIC));
	}

	private ElfSection getSymbolTableSection(int type) throws ElfException, IOException {
		for (int i = 1; i < header.e_shnum; i++) {
			ElfSection sh = getSection(i);
			if (sh.type == type) return sh;
		}
		return null;
	}

	/** Returns the elf symbol with the specified name or null if one is not found. */
	public ElfSymbol getELFSymbol(String symbolName) throws ElfException, IOException {
		if (symbolName == null) return null;

		// Check dynamic symbol table for symbol name.
		ElfSection sh = getDynamicSymbolTableSection();
		if (sh != null) {
			int numSymbols = sh.getNumberOfSymbols();
			for (int i = 0; i < Math.ceil(numSymbols / 2); i++) {
				ElfSymbol symbol = sh.getELFSymbol(i);
				if (symbolName.equals(symbol.getName())) {
					return symbol;
				} else if (symbolName.equals((symbol = sh.getELFSymbol(numSymbols - 1 - i)).getName())) {
					return symbol;
				}
			}
		}

		// Check symbol table for symbol name.
		sh = getSymbolTableSection();
		if (sh != null) {
			int numSymbols = sh.getNumberOfSymbols();
			for (int i = 0; i < Math.ceil(numSymbols / 2); i++) {
				ElfSymbol symbol = sh.getELFSymbol(i);
				if (symbolName.equals(symbol.getName())) {
					return symbol;
				} else if (symbolName.equals((symbol = sh.getELFSymbol(numSymbols - 1 - i)).getName())) {
					return symbol;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the elf symbol with the specified address or null if one is not found. 'address' is relative to base of
	 * shared object for .so's.
	 */
	public ElfSymbol getELFSymbol(long address) throws ElfException, IOException {
		// Check dynamic symbol table for address.
		ElfSymbol symbol = null;
		long value = 0L;

		ElfSection sh = getDynamicSymbolTableSection();
		if (sh != null) {
			int numSymbols = sh.getNumberOfSymbols();
			for (int i = 0; i < numSymbols; i++) {
				symbol = sh.getELFSymbol(i);
				value = symbol.value;
				if (address >= value && address < value + symbol.size) return symbol;
			}
		}

		// Check symbol table for symbol name.
		sh = getSymbolTableSection();
		if (sh != null) {
			int numSymbols = sh.getNumberOfSymbols();
			for (int i = 0; i < numSymbols; i++) {
				symbol = sh.getELFSymbol(i);
				value = symbol.value;
				if (address >= value && address < value + symbol.size) return symbol;
			}
		}
		return null;
	}

	public ElfSegment getProgramHeader(int index) throws IOException {
		return programHeaders[index].getValue();
	}
	
	/**
	 * Find the file offset from a virtual address by looking up the {@link ElfSegment} segment containing the
	 * address and computing the resulting file offset.
	 */
	long virtualMemoryAddrToFileOffset(long address) throws IOException {
		for (int i = 0; i < header.e_phnum; i++) {
			ElfSegment ph = getProgramHeader(i);
			if (address >= ph.virtual_address && address < (ph.virtual_address + ph.mem_size)) {
				long relativeOffset = address - ph.virtual_address;
				if (relativeOffset >= ph.file_size)
					throw new ElfException("Can not convert virtual memory address " + Long.toHexString(address) + " to file offset -" + " found segment " + ph
							+ " but address maps to memory outside file range");
				return ph.offset + relativeOffset;
			}
		}
		throw new ElfException("Cannot find segment for address " + Long.toHexString(address));
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

        this.header = parser.header;
        
        parse(parser);
    }
    

	public ElfFile(ByteArrayInputStream baos) throws ElfException, IOException {
		this.parser = new ElfParser(baos);

		this.header = parser.header;
		
		parse(parser);
	}

	/** The interpreter specified by the {@link ElfSegment#PT_INTERP} program header, if any. */
	public String getInterpreter() throws IOException {
		for (int i = 0; i < programHeaders.length; i++) {
			ElfSegment ph = programHeaders[i].getValue();
			if (ph.type == ElfSegment.PT_INTERP) return ph.getIntepreter();
		}
		return null;
	}
	
	/**
	 *	Helper method for parsing sections and program headers
	 */
	private void parse(ElfParser parser) {
		sectionHeaders = MemoizedObject.uncheckedArray(header.e_shnum);
		for (int i = 0; i < header.e_shnum; i++) {
			final long sectionHeaderOffset = header.e_shoff + (i * header.e_shentsize);
			sectionHeaders[i] = new MemoizedObject<ElfSection>() {
				@Override
				public ElfSection computeValue() throws ElfException, IOException {
					return new ElfSection(ElfFile.this, sectionHeaderOffset);
				}
			};
		}

		programHeaders = MemoizedObject.uncheckedArray(header.e_phnum);
		for (int i = 0; i < header.e_phnum; i++) {
			final long programHeaderOffset = header.e_phoff + (i * header.e_phentsize);
			programHeaders[i] = new MemoizedObject<ElfSegment>() {
				@Override
				public ElfSegment computeValue() throws IOException {
					return new ElfSegment(ElfFile.this, programHeaderOffset);
				}
			};
		}
	}
}
