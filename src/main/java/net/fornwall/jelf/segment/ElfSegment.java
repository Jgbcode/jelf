package net.fornwall.jelf.segment;

import java.util.ArrayList;
import java.util.List;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.ElfSection;

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
		/** PT_NULL: Type defining that the array element is unused. Other member values are undefined. */
		public static final int NULL = 0;
		/** PT_LOAD: Type defining that the array element specifies a loadable segment. */
		public static final int LOAD = 1;
		/** PT_DYNAMIC: The array element specifies dynamic linking information. */
		public static final int DYNAMIC = 2;
		/**
		 * PT_INTERP:
		 * The array element specifies the location and size of a null-terminated path name to invoke as an interpreter.
		 * Meaningful only for executable files (though it may occur for shared objects); it may not occur more than once in
		 * a file. If it is present, it must precede any loadable segment entry.
		 */
		public static final int INTERP = 3;
		/** PT_NOTE: The array element specifies the location and size of auxiliary information. */
		public static final int NOTE = 4;
		/** PT_SHLIB: This segment type is reserved but has unspecified semantics. */
		public static final int SHLIB = 5;
		/**
		 * PT_PHDR:
		 * The array element, if present, specifies the location and size of the program header table itself, both in the
		 * file and in the memory image of the program. This segment type may not occur more than once in a file.
		 */
		public static final int PHDR = 6;
		/** PT_TLS: The array element specifies the Thread-Local Storage template. */
		public static final int TLS = 7;
		/** PT_NUM: Number of defined types */
		public static final int NUM = 8;
		/** Lower bound of the range reserved for operating system-specific semantics. */
		public static final int LOOS = 0x60000000;
		/** PT_GNU_EH_FRAME: GCC .eh_frame_hdr segment */
		public static final int GNU_EH_FRAME = 0x6474e550;
		/** PT_GNU_STACK: Indicates stack executability */
		public static final int GNU_STACK = 0x6474e551;
		/** PT_GNU_RELRO: Read-only after relocation */
		public static final int GNU_RELRO = 0x6474e552;
		/** PT_LOSUNW: */
		public static final int LOSUNW = 0x6ffffffa;
		/** PT_SUNWBSS: Sun Specific segment */
		public static final int SUNWBSS = 0x6ffffffa;
		/** PT_SUNWSTACK: Stack segment */
		public static final int SUNWSTACK = 0x6ffffffb;
		/** PT_HISUNW: */
		public static final int HISUNW = 0x6fffffff;
		/** PT_HIOS: Upper bound of the range reserved for operating system-specific semantics. */
		public static final int HIOS = 0x6fffffff;
		/** PT_LOPROC: Lower bound of the range reserved for processor-specific semantics. */
		public static final int LOPROC = 0x70000000;
		/** PT_HIPROC: Upper bound of the range reserved for processor-specific semantics. */
		public static final int HIPROC = 0x7fffffff;
		
		public final int val;
		
		public Type(int val) {
			this.val = val;
		}
		
		/**
		 * @return Returns the name of this type
		 */
		public String name() {
			switch(val) {
			case NULL:
				return "NULL";
			case LOAD:
				return "LOAD";
			case DYNAMIC:
				return "DYNAMIC";
			case INTERP:
				return "INTERP";
			case NOTE:
				return "NOTE";
			case SHLIB:
				return "SHLIB";
			case PHDR:
				return "PHDR";
			case TLS:
				return "TLS";
			case GNU_EH_FRAME:
				return "GNU_EH_FRAME";
			case GNU_STACK:
				return "GNU_STACK";
			case GNU_RELRO:
				return "GNU_RELRO";
			case SUNWBSS:
				return "SUNWBSS";
			case SUNWSTACK:
				return "SUNWSTACK";
			default:
				if(val >= LOOS && val <= HIOS)
					return "OS";
				if(val >= LOPROC && val <= HIPROC)
					return "PROC";
				return "?";
			}
		}
	}
	
	public static class Flag {
		/** PF_X: Executable flag */
		public static final int X = 1 << 0;
		/** PF_W: Writable flag */
		public static final int W = 1 << 1;
		/** PF_R: Readable flag */
		public static final int R = 1 << 2;
		/** PF_MASKOS: OS-Specific flag mask */
		public static final int MASKOS = 0x0ff00000;
		/** PF_MASKPROC: Processor specific flag mask */
		public static final int MASKPROC = 0xf0000000;
		/** Flags in this mask are unknown */
		public static final int MASKUNKNOWN = ~(MASKOS | MASKPROC | (2 * R - 1));
		
		public final int val;
		
		public Flag(int val) {
			this.val = val;
		}
		
		/**
		 * @return Returns a human readable version of the flag
		 */
		public String name() {
			String result = "";
			if((val & X) != 0)
				result += "X";
			if((val & W) != 0)
				result += "W";
			if((val & R) != 0)
				result += "R";
			if((val & MASKOS) != 0)
				result += "o";
			if((val & MASKPROC) != 0)
				result += "p";
			if((val & MASKUNKNOWN) != 0)
				result += "x";
			return result;
		}
	}
	
	private final ElfFile file;
	
	/** Elf{32,64}_Phdr#p_type. Kind of segment this element describes. */
	private final Type type; // Elf32_Word/Elf64_Word - 4 bytes in both.
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
	private final Flag flags; // Elf32_Word
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
			type = new Type(parser.readInt());
			this.offset = parser.readInt();
			virtual_address = parser.readInt();
			physical_address = parser.readInt();
			file_size = parser.readInt();
			mem_size = parser.readInt();
			flags = new Flag(parser.readInt());
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
			type = new Type(parser.readInt());
			flags = new Flag(parser.readInt());
			this.offset = parser.readLong();
			virtual_address = parser.readLong();
			physical_address = parser.readLong();
			file_size = parser.readLong();
			mem_size = parser.readLong();
			alignment = parser.readLong();
		}
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
	
	public static ElfSegment segmentFactory(ElfFile file, long offset) {
		ElfSegment s = new ElfSegment(file, offset);
		
		/*
		 * Create subclasses as needed
		 */
		
		return s;
	} 
	
	/**
	 * @return Returns the file this segment belongs to
	 */
	public ElfFile getFile() {
		return file;
	}
	
	/**
	 * @return Returns the type of this segment
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return Returns offset in the file of the beginning of this segment
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * @return Returns the beginning virtual memory address of this segment
	 */
	public long getVirtualAddress() {
		return virtual_address;
	}

	/**
	 * @return Returns the beginning physical memory address of this segment
	 */
	public long getPhysicalAddress() {
		return physical_address;
	}

	/**
	 * @return Returns the size of this segment in the file
	 */
	public long getFileSize() {
		return file_size;
	}

	/**
	 * @return Returns the size of this segment in memory
	 */
	public long getMemSize() {
		return mem_size;
	}

	/**
	 * @return Returns the flags associated with this segment
	 */
	public Flag getFlags() {
		return flags;
	}

	/**
	 * @return Returns the alignment of this segment
	 */
	public long getAlignment() {
		return alignment;
	}
	
	/**
	 * @return Returns a the list of sections that are contained within this segment
	 */
	public List<ElfSection> getSections() {
		List<ElfSection> result = new ArrayList<ElfSection>();
		
		int i = file.getSectionHeaders().getSectionIndexAtOffset(offset);
		ElfSection cur = file.getSectionHeaders().getSectionByIndex(i);
		while(cur.getFileOffset() + cur.getFileSize() <= offset + file_size) {
			result.add(cur);
			if(++i < file.getSectionHeaders().size())
				cur = file.getSectionHeaders().getSectionByIndex(i);
			else
				break;
		}
		
		return result;
	}
}
