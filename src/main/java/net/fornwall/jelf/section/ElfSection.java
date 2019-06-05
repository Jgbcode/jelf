package net.fornwall.jelf.section;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfParser;

/**
 * Class corresponding to the Elf32_Shdr/Elf64_Shdr struct.
 * 
 * <p>
 * An object file's section header table lets one locate all the file's sections. The section header table is an array
 * of Elf32_Shdr or Elf64_Shdr structures. A section header table index is a subscript into this array. The ELF header's
 * {@link ElfHeader#e_shoff e_shoff} member gives the byte offset from the beginning of the file to the section header
 * table with each section header entry being {@link ElfHeader#e_shentsize e_shentsize} bytes big.
 * 
 * <p>
 * {@link ElfHeader#e_shnum e_shnum} normally tells how many entries the section header table contains, but if the number
 * of sections is greater than or equal to SHN_LORESERVE (0xff00), e_shnum has the value SHN_UNDEF (0) and the actual
 * number of section header table entries is contained in the sh_size field of the section header at index 0 (otherwise,
 * the sh_size member of the initial entry contains 0).
 * 
 * <p>
 * Some section header table indexes are reserved in contexts where index size is restricted, for example, the st_shndx
 * member of a symbol table entry and the e_shnum and e_shstrndx members of the ELF header. In such contexts, the
 * reserved values do not represent actual sections in the object file. Also in such contexts, an escape value indicates
 * that the actual section index is to be found elsewhere, in a larger field.
 */
public class ElfSection {
	public static final class Type {
		/**
		 * SHT_NULL: 
		 * Marks the section header as inactive; it does not have an associated section. Other members of 
		 * the section header have undefined values.
		 */
		public static final int NULL = 0;
		/** SHT_PROGBITS: Section holds information defined by the program. */
		public static final int PROGBITS = 1;
		/**
		 * SHT_SYMTAB:
		 * Section holds symbol table information for link editing. It may also be used to store symbols for 
		 * dynamic linking. Only one per ELF file. The symtab contains everything, but it is non-allocable, 
		 * can be stripped, and has no runtime cost.
		 */
		public static final int SYMTAB = 2;
		/** SHT_STRTAB: Section holds string table information. */
		public static final int STRTAB = 3;
		/** SHT_RELA: Section holds relocation entries with explicit addends. */
		public static final int RELA = 4;
		/** SHT_HASH: Section holds symbol hash table. */
		public static final int HASH = 5;
		/**
		* SHT_DYNAMIC:
		* Section holds information for dynamic linking. Only one per ELF file. The dynsym is allocable, and 
		* contains the symbols needed to support runtime operation.
		*/
		public static final int DYNAMIC = 6;
		/** SHT_NOTE: Section holds information that marks the file. */
		public static final int NOTE = 7;
		/** SHT_NOBITS: Section occupies no space but resembles {@link Type#PROGBITS PROGBITS}. */
		public static final int NOBITS = 8;
		/** SHT_REL: Section holds relocation entries without explicit addends. */
		public static final int REL = 9;
		/** SHT_SHLIB: Section is reserved but has unspecified semantics. */
		public static final int SHLIB = 10;
		/** SHT_DYNSYM:
		 * This section holds a minimal set of symbols adequate for dynamic linking. See also 
		* {@link Type#SYMTAB SYMTAB}. Currently, an object file may have either a section of 
		* {@link Type#SYMTAB SYMTAB} type or a section of DYNSYM type, but not both. This 
		* restriction may be relaxed in the future. */
		public static final int DYNSYM = 11;
		/** SHT_INIT_ARRAY: Array of constructors */
		public static final int INIT_ARRAY = 14;
		/** SHT_FINI_ARRAY: Array of destructors */
		public static final int FINI_ARRAY = 15;
		/** SHT_PREINIT_ARRAY: Array of pre-constructors */
		public static final int PREINIT_ARRAY = 16;
		/** SHT_GROUP: Section group */
		public static final int GROUP = 17;
		/** SHT_SYMTAB_SHNDX: Extended section indeces */
		public static final int SYMTAB_SHNDX = 18;
		/** SHT_NUM: Number of defined types */
		public static final int NUM = 19;
		/** SHT_LOOS: Lower bound of the range of indexes reserved for operating system-specific semantics. */
		public static final int LOOS = 0x60000000;
		/** SHT_GNU_ATTRIBUTES: Object attributes.  */
		public static final int GNU_ATTRIBUTES = 0x6ffffff5;
		/** SHT_GNU_HASH: GNU-style hash table.  */
		public static final int GNU_HASH = 0x6ffffff6;
		/** SHT_GNU_LIBLIST: Prelink library list */
		public static final int GNU_LIBLIST = 0x6ffffff7;
		/** SHT_CHECKSUM: Checksum for DSO content.  */
		public static final int CHECKSUM = 0x6ffffff8;
		/** SHT_LOSUNW: Sun-specific low bound.  */
		public static final int LOSUNW = 0x6ffffffa;
		public static final int SUNW_MOVE = 0x6ffffffa;
		public static final int SUNW_COMDAT = 0x6ffffffb;
		public static final int SUN_SUNW_SYMINFO = 0x6ffffffc;
		/** SHT_GNU_verfef: Version definition section.  */
		public static final int GNU_VERDEF = 0x6ffffffd;
		/** SHT_GNU_verneed: Version needs section.  */
		public static final int GNU_VERNEED = 0x6ffffffe;
		/** SHT_GUN versym: Version symbol table */
		public static final int GNU_VERSYM = 0x6fffffff;
		/** SHT_HISUNW: */
		public static final int HISUNW = 0x6fffffff;
		/** SHT_HIOS: Upper bound of the range of indexes reserved for operating system-specific semantics. */
		public static final int HIOS = 0x6fffffff;
		/** SHT_LOPROC: Lower bound of the range of indexes reserved for processor-specific semantics. */
		public static final int LOPROC = 0x70000000;
		/** SHT_HIPROC: Upper bound of the range of indexes reserved for processor-specific semantics. */
		public static final int HIPROC = 0x7fffffff;
		/** SHT_LOUSER: Lower bound of the range of indexes reserved for application programs. */
		public static final int LOUSER = 0x80000000;
		/** SHT_HIUSER: Upper bound of the range of indexes reserved for application programs. */
		public static final int HIUSER = 0xffffffff;
		
		public final int val;
		
		public Type(int val) {
			this.val = val;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Type))
				return false;
			return ((Type)o).val == this.val;
		}
		
		/**
		 * @return Returns the name of this type or "?" if unknown type
		 */
		public String name() {
			switch(val) {
			case NULL:
				return "NULL";
			case PROGBITS:
				return "PROGBITS";
			case SYMTAB:
				return "SYMTAB";
			case STRTAB:
				return "STRTAB";
			case RELA:
				return "RELA";
			case HASH:
				return "HASH";
			case DYNAMIC:
				return "DYNAMIC";
			case NOTE:
				return "NOTE";
			case NOBITS:
				return "NOBITS";
			case REL:
				return "REL";
			case SHLIB:
				return "SHLIB";
			case DYNSYM:
				return "DYNSYM";
			case  INIT_ARRAY:
				return "INIT_ARRAY";
			case FINI_ARRAY:
				return "FINI_ARRAY";
			case PREINIT_ARRAY:
				return "PREINIT_ARRAY";
			case GROUP:
				return "GROUP";
			case SYMTAB_SHNDX:
				return "SYMTAB_SHNDX";
			case GNU_ATTRIBUTES:
				return "GNU_ATTRIBUTES";
			case GNU_HASH:
				return "GNU_HASH";
			case GNU_LIBLIST:
				return "GNU_LIBLIST";
			case CHECKSUM:
				return "CHECKSUM";
			case SUNW_MOVE:
				return "SUNW_MOVE";
			case SUNW_COMDAT:
				return "SUNW_COMDAT";
			case SUN_SUNW_SYMINFO:
				return "SUNW_SYMINFO";
			case GNU_VERDEF:
				return "GNU_VERDEF";
			case GNU_VERNEED:
				return "GNU_VERNEED";
			case GNU_VERSYM:
				return "GNU_VERSYM";
			default:
				if(val >= LOOS && val <= HIOS)
					return "OS";
				if(val >= LOPROC && val <= HIPROC)
					return "PROC";
				return "?";
			}
		}
	}
	
	public static final class Flag {
		/** SHF_WRITE: Flag informing that this section contains data that should be writable during process execution. */
		public static final int WRITE = 1 << 0;
		/** SHF_ALLOC: Flag informing that section occupies memory during process execution. */
		public static final int ALLOC = 1 << 1;
		/** SHF_EXECINSTR: Flag informing that section contains executable machine instructions. */
		public static final int EXECINSTR = 1 << 2;
		/** SHF_MERGE: Flag informing that the section might be merged */
		public static final int MERGE = 1 << 4;
		/** SHF_STRINGS: Flag informing that the section contains null terminated strings */
		public static final int STRINGS = 1 << 5;
		/** SHF_INFO_LINK: sh_info contains SHT index */
		public static final int INFO_LINK = 1 << 6;
		/** SHF_LINK_ORDER: Preserved order after combining */
		public static final int LINK_ORDER = 1 << 7;
		/** SHF_OS_NONCONFORMING: Non-standard OS specific handing required */
		public static final int OS_NONCONFORMING = 1 << 8;
		/** SHF_GROUP: Section is member of a group */
		public static final int GROUP = 1 << 9;
		/** SHF_TLS: Section hold thread-local data */
		public static final int TLS = 1 << 10;
		/** SHF_COMPRESSED: Section with compressed data */
		public static final int COMPRESSED = 1 << 11;
		/** SHF_MAKSOS: OS-Specific mask */
		public static final int MASKOS = 0x0ff00000;
		/** SHF_MASKPROC: Flag informing that all the bits in the mask are reserved for processor specific semantics. */
		public static final int MASKPROC = 0xf0000000;
		/** Flags in this mask are unknown flags */
		public static final int MASKUNKNOWN = ~(MASKOS | MASKPROC | (2 * COMPRESSED - 1));
		
		public final long val;
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Flag))
				return false;
			return ((Flag)o).val == this.val;
		}
		
		/**
		 * @param f - the flags to test for
		 * @return Returns true only if all of the flags in f are also set in this Flag object
		 */
		public boolean test(Flag f) {
			return (this.val & f.val) == f.val;
		}
		
		public Flag(long val) {
			this.val = val;
		}
		
		/**
		 * Example: A flag with WRITE and ALLOC set would return "WA"
		 * 
		 * @return Returns the short hand name of this flag.
		 */
		public String name() {
			String name = "";
			if((val & WRITE) != 0)
				name += "W";
			if((val & ALLOC) != 0)
				name += "A";
			if((val & EXECINSTR) != 0)
				name += "X";
			if((val & MERGE) != 0)
				name += "M";
			if((val & STRINGS) != 0)
				name += "S";
			if((val & INFO_LINK) != 0)
				name += "I";
			if((val & LINK_ORDER) != 0)
				name += "L";
			if((val & OS_NONCONFORMING) != 0)
				name += "O";
			if((val & GROUP) != 0)
				name += "G";
			if((val & TLS) != 0)
				name += "T";
			if((val & MASKUNKNOWN) != 0)
				name += "x";
			if((val & MASKOS) != 0)
				name += "o";
			if((val & MASKPROC) != 0)
				name += "p";
			
			return name;
		}
		
		/**
		 * @return Returns a string which displays the meaning of the value returned by name()
		 */
		public static String getNameKey() {
			// Taken from readelf
			return   "W (write), A (alloc), X (execute), M (merge), S (strings), I (info),\n" +
					 "L (link order), O (extra OS processing required), G (group), T (TLS),\n" +
					 "C (compressed), x (unknown), o (OS specific), p (processor specific)\n";
		}
	}
	
	/** Index into the section header string table which gives the name of the section. */
	private final int name_ndx; // Elf32_Word or Elf64_Word - 4 bytes in both.
	
	/** Section content and semantics. */
	private final Type type; // Elf32_Word or Elf64_Word - 4 bytes in both.
	
	/** Flags. */
	private final Flag flags; // Elf32_Word or Elf64_Xword.
	
	/**
	 * sh_addr. If the section will be in the memory image of a process this will be the address at which the first byte
	 * of section will be loaded. Otherwise, this value is 0.
	 */
	private final long address; // Elf32_Addr
	
	/** Offset from beginning of file to first byte of the section. */
	private final long section_offset; // Elf32_Off
	
	/** Size in bytes of the section. TYPE_NOBITS is a special case. */
	private final long size; // Elf32_Word
	
	/** Section header table index link. */
	private final int link; // Elf32_Word or Elf64_Word - 4 bytes in both.
	
	/** Extra information determined by the section type. */
	private final int info; // Elf32_Word or Elf64_Word - 4 bytes in both.
	
	/** Address alignment constraints for the section. */
	private final long address_alignment; // Elf32_Word
	
	/** Size of a fixed-size entry, 0 if none. */
	private final long entry_size; // Elf32_Word

	private final ElfFile file;

	/** Reads the section header information located at offset. */
	private ElfSection(final ElfFile file, long offset) {
		this.file = file;
		ElfParser parser = file.getParser();
		
		parser.seek(offset);

		name_ndx = parser.readInt();
		type = new Type(parser.readInt());
		flags = new Flag(parser.readIntOrLong());
		address = parser.readIntOrLong();
		section_offset = parser.readIntOrLong();
		size = parser.readIntOrLong();
		link = parser.readInt();
		info = parser.readInt();
		address_alignment = parser.readIntOrLong();
		entry_size = parser.readIntOrLong();
	}
	
	/** Only to be called by subclasses */
	protected ElfSection(ElfSection s) {
		this.file = s.file;
		
		this.name_ndx = s.name_ndx;
		this.type = s.type;
		this.flags = s.flags;
		this.address = s.address;
		this.section_offset = s.section_offset;
		this.size = s.size;
		this.link = s.link;
		this.info = s.info;
		this.address_alignment = s.address_alignment;
		this.entry_size = s.entry_size;
	}
	
	public static ElfSection sectionFactory(final ElfFile file, long offset) {
		ElfSection s = new ElfSection(file, offset);
		
		switch (s.type.val) {
		case Type.SYMTAB:
		case Type.DYNSYM:
			return new ElfSymbolTableSection(s);
		case Type.STRTAB:
			return new ElfStringTableSection(s);
		case Type.HASH:
			return new ElfHashTableSection(s);
		case Type.REL:
		case Type.RELA:
			return new ElfRelocationSection(s);
		//case Type.DYNAMIC:
		case Type.NOTE:
			return new ElfNoteSection(s);
		case Type.NOBITS:
			return new ElfNoBitsSection(s);
		default:
			return s;
		}
	}
	
	/**
	 * This member specifies the name of the section. Its value is an index into the section
	 * header string table section
	 * 
	 * @return Returns an index to the string in the .shstrtab section which
	 * 	holds the name of this section
	 */
	public int getNameIndex() {
		return name_ndx;
	}

	/**
	 * This member categorizes the section’s contents and semantics, see {@link Type}
	 * 
	 * @return Returns the type of this section
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sections support 1-bit flags that describe miscellaneous attributes, see {@link Flag}
	 * 
	 * @return Returns the flags of this section, 
	 */
	public Flag getFlags() {
		return flags;
	}

	/**
	 * If the section will appear in the memory image of a process, this member gives the
	 * address at which the section’s first byte should reside. Otherwise, the member contains 0
	 * 
	 * @return Memory address of this section if it is loadable
	 */
	public long getAddress() {
		return address;
	}

	/**
	 * This member’s value gives the byte offset from the beginning of the file to the first
	 * byte in the section. One section type, {@link Type#NOBITS} described below, occupies no
	 * space in the file, and its file offset member locates the conceptual placement in the
	 * file
	 * 
	 * @return Returns the offset of the section in the file image
	 */
	public long getFileOffset() {
		return section_offset;
	}

	/**
	 * This member gives the section’s size in bytes.
	 * 
	 * @return Size of the section in the file image
	 */
	public long getFileSize() {
		return size;
	}
	
	/**
	 * This member holds a section header table index link, whose interpretation depends
	 * on the section type
	 * 
	 * @return Returns the section index of an associated section. This field is used for several purposes, 
	 * 	depending on the type of section
	 */
	public int getLinkIndex() {
		return link;
	}

	/**
	 * This member holds extra information, whose interpretation depends on the section
	 * type. 
	 * 
	 * @return Returns extra information about the section.
	 */
	public int getInfo() {
		return info;
	}

	/**
	 * Some sections have address alignment constraints. For example, if a section holds a
	 * doubleword, the system must ensure doubleword alignment for the entire section.
	 * That is, the value of {@link #getAddress()} must be congruent to 0, modulo the value of
	 * {@link #getAlignment()}. Currently, only 0 and positive integral powers of two are allowed.
	 * Values 0 and 1 mean the section has no alignment constraints.
	 * 
	 * @return Returns the required alignment of the section. This field must be a power of two.
	 */
	public long getAlignment() {
		return address_alignment;
	}

	/**
	 * @return Returns the size, in bytes, of each entry, for sections that contain fixed-size entries. 
	 * 	Otherwise, this field contains zero.
	 */
	public long getEntrySize() {
		return entry_size;
	}
	
	/**
	 * Some sections hold a table of fixed-size entries, such as a symbol table. For such a section, 
	 * this member gives the size in bytes of each entry. The member contains 0 if the
	 * section does not hold a table of fixed-size entries
	 * 
	 * @return Returns the file this section is associated with
	 */
	public ElfFile getFile() {
		return file;
	}
	
	/** 
	 * @return Returns the name of the section, an empty string is returned if the section has no name
	 */
	public String getName() {
		if (name_ndx == 0) return "";
		return file.getSectionHeaders().getSectionStringTable().getString(name_ndx);
	}
	
	/**
	 * @return Returns the section which is linked by this section
	 */
	public ElfSection getLink() {
		return file.getSectionHeaders().getSectionByIndex(link);
	}
	
	/**
	 * @return Returns the section which is linked by this section after casting to the provided type
	 * @throws ElfException if the cast is invalid
	 */
	public ElfSection getLink(Class<? extends ElfSection> c) {
		return file.getSectionHeaders().getSectionByIndex(link, c);
	}
}
