package net.fornwall.jelf.segment;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfParser;

/**
 * Class corresponding to the Elf32_Phdr/Elf64_Phdr struct.
 * 
 * An executable or shared object file's program header table is an array of structures, each describing a segment or
 * other information the system needs to prepare the program for execution. An object file segment contains one or more
 * sections. Program headers are meaningful only for executable and shared object files. A file specifies its own
 * program header size with the ELF header's {@link ElfFile#ph_entry_size e_phentsize} and {@link ElfFile#num_ph
 * e_phnum} members.
 * 
 * http://www.sco.com/developers/gabi/latest/ch5.pheader.html#p_type
 * http://stackoverflow.com/questions/22612735/how-can-i-find-the-dynamic-libraries-required-by-an-elf-binary-in-c
 */
public class ElfSegment {

	public static final class Type {
		private Type() {};
		
		/** Type defining that the array element is unused. Other member values are undefined. */
		public static final int NULL = 0;
		/** Type defining that the array element specifies a loadable segment. */
		public static final int LOAD = 1;
		/** The array element specifies dynamic linking information. */
		public static final int DYNAMIC = 2;
		/**
		 * The array element specifies the location and size of a null-terminated path name to invoke as an interpreter.
		 * Meaningful only for executable files (though it may occur for shared objects); it may not occur more than once in
		 * a file. If it is present, it must precede any loadable segment entry.
		 */
		public static final int INTERP = 3;
		/** The array element specifies the location and size of auxiliary information. */
		public static final int NOTE = 4;
		/** This segment type is reserved but has unspecified semantics. */
		public static final int SHLIB = 5;
		/**
		 * The array element, if present, specifies the location and size of the program header table itself, both in the
		 * file and in the memory image of the program. This segment type may not occur more than once in a file.
		 */
		public static final int PHDR = 6;
		/** The array element specifies the Thread-Local Storage template. */
		public static final int TLS = 7;
	
		/** Lower bound of the range reserved for operating system-specific semantics. */
		public static final int LOOS = 0x60000000;
		/** Upper bound of the range reserved for operating system-specific semantics. */
		public static final int HIOS = 0x6fffffff;
		/** Lower bound of the range reserved for processor-specific semantics. */
		public static final int LOPROC = 0x70000000;
		/** Upper bound of the range reserved for processor-specific semantics. */
		public static final int HIPROC = 0x7fffffff;
	}
	
	private final ElfFile file;
	
	/** Elf{32,64}_Phdr#p_type. Kind of segment this element describes. */
	private final int type; // Elf32_Word/Elf64_Word - 4 bytes in both.
	/** Elf{32,64}_Phdr#p_offset. File offset at which the first byte of the segment resides. */
	private final long offset; // Elf32_Off/Elf64_Off - 4 or 8 bytes.
	/** Elf{32,64}_Phdr#p_vaddr. Virtual address at which the first byte of the segment resides in memory. */
	private final long virtual_address; // Elf32_Addr/Elf64_Addr - 4 or 8 bytes.
	/** Reserved for the physical address of the segment on systems where physical addressing is relevant. */
	private final long physical_address; // Elf32_addr/Elf64_Addr - 4 or 8 bytes.

	/** Elf{32,64}_Phdr#p_filesz. File image size of segment in bytes, may be 0. */
	private final long file_size; // Elf32_Word/Elf64_Xword -
	/** Elf{32,64}_Phdr#p_memsz. Memory image size of segment in bytes, may be 0. */
	private final long mem_size; // Elf32_Word
	/**
	 * Flags relevant to this segment. Values for flags are defined in ELFSectionHeader.
	 */
	private final int flags; // Elf32_Word
	private final long alignment; // Elf32_Word

	private ElfSegment(final ElfFile file, long offset) {
		this.file = file;
		ElfParser parser = file.getParser();
		ElfHeader header = file.getHeader();
		
		parser.seek(offset);
		if (header.getBitClass() == ElfHeader.BitClass.ELFCLASS32) {
			// typedef struct {
			// Elf32_Word p_type;
			// Elf32_Off p_offset;
			// Elf32_Addr p_vaddr;
			// Elf32_Addr p_paddr;
			// Elf32_Word p_filesz;
			// Elf32_Word p_memsz;
			// Elf32_Word p_flags;
			// Elf32_Word p_align;
			// } Elf32_Phdr;
			type = parser.readInt();
			this.offset = parser.readInt();
			virtual_address = parser.readInt();
			physical_address = parser.readInt();
			file_size = parser.readInt();
			mem_size = parser.readInt();
			flags = parser.readInt();
			alignment = parser.readInt();
		} else {
			// typedef struct {
			// Elf64_Word p_type;
			// Elf64_Word p_flags;
			// Elf64_Off p_offset;
			// Elf64_Addr p_vaddr;
			// Elf64_Addr p_paddr;
			// Elf64_Xword p_filesz;
			// Elf64_Xword p_memsz;
			// Elf64_Xword p_align;
			// } Elf64_Phdr;
			type = parser.readInt();
			flags = parser.readInt();
			this.offset = parser.readLong();
			virtual_address = parser.readLong();
			physical_address = parser.readLong();
			file_size = parser.readLong();
			mem_size = parser.readLong();
			alignment = parser.readLong();
		}
	}
	
	public static ElfSegment segmentFactory(ElfFile file, long offset) {
		ElfSegment s = new ElfSegment(file, offset);
		
		/*
		 * Create subclasses as needed
		 */
		
		return s;
	}
	
	protected ElfSegment(ElfSegment s) {
		this.file = s.file;
		this.type = s.type;
		this.flags = s.flags;
		this.offset = s.offset;
		this.virtual_address = s.virtual_address;
		this.physical_address = s.physical_address;
		this.file_size = s.file_size;
		this.mem_size = s.mem_size;
		this.alignment = s.alignment;
	}

	public ElfFile getFile() {
		return file;
	}
	
	public int getType() {
		return type;
	}

	public long getOffset() {
		return offset;
	}

	public long getVirtualAddress() {
		return virtual_address;
	}

	public long getPhysicalAddress() {
		return physical_address;
	}

	public long getFileSize() {
		return file_size;
	}

	public long getMemSize() {
		return mem_size;
	}

	public int getFlags() {
		return flags;
	}

	public long getAlignment() {
		return alignment;
	}
	
	
}
