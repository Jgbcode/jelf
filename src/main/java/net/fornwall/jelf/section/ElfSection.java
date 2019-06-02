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
		 * Marks the section header as inactive; it does not have an associated section. Other members of 
		 * the section header have undefined values.
		 */
		public static final int NULL = 0;
		/** Section holds information defined by the program. */
		public static final int PROGBITS = 1;
		/**
		 * Section holds symbol table information for link editing. It may also be used to store symbols for 
		 * dynamic linking. Only one per ELF file. The symtab contains everything, but it is non-allocable, 
		 * can be stripped, and has no runtime cost.
		 */
		public static final int SYMTAB = 2;
		/** Section holds string table information. */
		public static final int STRTAB = 3;
		/** Section holds relocation entries with explicit addends. */
		public static final int RELA = 4;
		/** Section holds symbol hash table. */
		public static final int HASH = 5;
		/**
		* Section holds information for dynamic linking. Only one per ELF file. The dynsym is allocable, and 
		* contains the symbols needed to support runtime operation.
		*/
		public static final int DYNAMIC = 6;
		/** Section holds information that marks the file. */
		public static final int NOTE = 7;
		/** Section occupies no space but resembles {@link Type#PROGBITS PROGBITS}. */
		public static final int NOBITS = 8;
		/** Section holds relocation entries without explicit addends. */
		public static final int REL = 9;
		/** Section is reserved but has unspecified semantics. */
		public static final int SHLIB = 10;
		/** This section holds a minimal set of symbols adequate for dynamic linking. See also 
		* {@link Type#SYMTAB SYMTAB}. Currently, an object file may have either a section of 
		* {@link Type#SYMTAB SYMTAB} type or a section of DYNSYM type, but not both. This 
		* restriction may be relaxed in the future. */
		public static final int DYNSYM = 11;
		public static final int INIT_ARRAY = 14;
		public static final int FINI_ARRAY = 15;
		public static final int PREINIT_ARRAY = 16;
		public static final int GROUP = 17;
		public static final int SYMTAB_SHNDX = 18;
		/** Lower bound of the range of indexes reserved for operating system-specific semantics. */
		public static final int LOOS = 0x60000000;
		public static final int GNU_VERDEF = 0x6ffffffd;
		public static final int GNU_VERNEED = 0x6ffffffe;
		public static final int GNU_VERSYM = 0x6fffffff;
		/** Upper bound of the range of indexes reserved for operating system-specific semantics. */
		public static final int HIOS = 0x6fffffff;
		/** Lower bound of the range of indexes reserved for processor-specific semantics. */
		public static final int LOPROC = 0x70000000;
		/** Upper bound of the range of indexes reserved for processor-specific semantics. */
		public static final int HIPROC = 0x7fffffff;
		/** Lower bound of the range of indexes reserved for application programs. */
		public static final int LOUSER = 0x80000000;
		/** Upper bound of the range of indexes reserved for application programs. */
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
			case GNU_VERDEF:
				return "GNU_VERDEF";
			case GNU_VERNEED:
				return "GNU_VERNEED";
			case GNU_VERSYM:
				return "GNU_VERSYM";
			default:
				if(val >= LOOS && val <= HIOS)
					return "OS RESERVED";
				if(val >= LOPROC && val <= HIPROC)
					return "PROC RESERVED";
				return "?";
			}
		}
	}
	
	public static final class Flag {
		/** Flag informing that this section contains data that should be writable during process execution. */
		public static final int WRITE = 0x1;
		/** Flag informing that section occupies memory during process execution. */
		public static final int ALLOC = 0x2;
		/** Flag informing that section contains executable machine instructions. */
		public static final int EXECINSTR = 0x4;
		/** Flag informing that the section might be merged */
		public static final int MERGE = 0x10;
		/** Flag informing that the section contains null terminated strings */
		public static final int STRINGS = 0x20;
		/** sh_info contains SHT index */
		public static final int INFO_LINK = 0x40;
		/** Preserved order after combining */
		public static final int LINK_ORDER = 0x80;
		/** Non-standard OS specific handing required */
		public static final int OS_NONCONFORMING = 0x100;
		/** Section is member of a group */
		public static final int GROUP = 0x200;
		/** Section hold thread-local data */
		public static final int TLS = 0x400;
		/** OS-Specific mask */
		public static final int MASKOS = 0x0ff00000;
		/** Flag informing that all the bits in the mask are reserved for processor specific semantics. */
		public static final int MASKPROC = 0xf0000000;
		
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
			for(int i = 1; i != 0; i <<= 1) {
				switch((int)(val & i)) {
				case 0:
					break;
				case WRITE:
					name += "W";
					break;
				case ALLOC:
					name += "A";
					break;
				case EXECINSTR:
					name += "X";
					break;
				case MERGE:
					name += "M";
					break;
				case STRINGS:
					name += "S";
					break;
				case INFO_LINK:
					name += "I";
					break;
				case LINK_ORDER:
					name += "L";
					break;
				case OS_NONCONFORMING:
					name += "O";
					break;
				case GROUP:
					name += "G";
					break;
				case TLS:
					name += "T";
					break;
				default:
					if((val & i & MASKOS) != 0)
						name += "o";
					else if((val & i & MASKPROC) != 0)
						name += "p";
					else
						name += "x";
				}
			}
			
			return name;
		}
		
		/**
		 * @return Returns a string which displays the meaning of the value returned by name()
		 */
		public static String getNameKey() {
			// Taken from readelf, C (compressed) and E (excluded) are currently not
			// supported by name()
			return   "W (write), A (alloc), X (execute), M (merge), S (strings), I (info),\n" +
					 "L (link order), O (extra OS processing required), G (group), T (TLS),\n" +
					 "C (compressed), x (unknown), o (OS specific), E (exclude),\n" +
					 "p (processor specific)\n";
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
		//case Type.DYNAMIC:
		// case Type.NOTE:
		default:
			return s;
		}
	}
	
	/**
	 * @return Returns an index to the string in the .shstrtab section which
	 * 	holds the name of this section
	 */
	public int getNameIndex() {
		return name_ndx;
	}

	/**
	 * @return Returns the type of this section, see {@link Type Type}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return Returns the flags of this section, 
	 */
	public Flag getFlags() {
		return flags;
	}

	/**
	 * @return Memory address of this section if it is loadable
	 */
	public long getAddress() {
		return address;
	}

	/**
	 * @return Returns the offset of the section in the file image
	 */
	public long getFileOffset() {
		return section_offset;
	}

	/**
	 * @return Size of the section in the file image
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * @return Returns the section index of an associated section. This field is used for several purposes, 
	 * 	depending on the type of section
	 */
	public int getLinkIndex() {
		return link;
	}

	/**
	 * @return Returns extra information about the section. This field is used for several purposes,
	 *  depending on the type of section.
	 */
	public int getInfo() {
		return info;
	}

	/**
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
	 * @return Returns the file this section is associated with
	 */
	public ElfFile getFile() {
		return file;
	}

	// Convenience methods
	
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
