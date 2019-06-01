package net.fornwall.jelf.section;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.fornwall.jelf.ElfDynamicStructure;
import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHashTable;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfNote;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.ElfStringTable;
import net.fornwall.jelf.ElfSymbol;
import net.fornwall.jelf.MemoizedObject;

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

	public enum Type {
		/**
		 * Marks the section header as inactive; it does not have an associated section. Other members of 
		 * the section header have undefined values.
		 */
		NULL(0),
		/** Section holds information defined by the program. */
		PROGBITS(1),
		/**
		 * Section holds symbol table information for link editing. It may also be used to store symbols for 
		 * dynamic linking. Only one per ELF file. The symtab contains everything, but it is non-allocable, 
		 * can be stripped, and has no runtime cost.
		 */
		SYMTAB(2),
		/** Section holds string table information. */
		STRTAB(3),
		/** Section holds relocation entries with explicit addends. */
		RELA(4),
		/** Section holds symbol hash table. */
		HASH(5),
		/**
		 * Section holds information for dynamic linking. Only one per ELF file. The dynsym is allocable, and 
		 * contains the symbols needed to support runtime operation.
		 */
		DYNAMIC(6),
		/** Section holds information that marks the file. */
		NOTE(7),
		/** Section occupies no space but resembles {@link Type#PROGBITS PROGBITS}. */
		NOBITS(8),
		/** Section holds relocation entries without explicit addends. */
		REL(9),
		/** Section is reserved but has unspecified semantics. */
		SHLIB(10),
		/** This section holds a minimal set of symbols adequate for dynamic linking. See also 
		 * {@link Type#SYMTAB SYMTAB}. Currently, an object file may have either a section of 
		 * {@link Type#SYMTAB SYMTAB} type or a section of DYNSYM type, but not both. This 
		 * restriction may be relaxed in the future. */
		DYNSYM(11),
		INIT_ARRAY(14),
		FINI_ARRAY(15),
		PREINIT_ARRAY(16),
		GROUP(17),
		SYMTAB_SHNDX(18),
		/** Lower bound of the range of indexes reserved for operating system-specific semantics. */
		LOOS(0x60000000),
		GNU_VERDEF(0x6ffffffd),
		GNU_VERNEED(0x6ffffffe),
		GNU_VERSYM(0x6fffffff),
		/** Upper bound of the range of indexes reserved for operating system-specific semantics. */
		HIOS(0x6fffffff),
		/** Lower bound of the range of indexes reserved for processor-specific semantics. */
		LOPROC(0x70000000),
		/** Upper bound of the range of indexes reserved for processor-specific semantics. */
		HIPROC(0x7fffffff),
		/** Lower bound of the range of indexes reserved for application programs. */
		LOUSER(0x80000000),
		/** Upper bound of the range of indexes reserved for application programs. */
		HIUSER(0xffffffff);
		
		public final int val;
		
		private Type(int val) {
			this.val = val;
		}
		
		private static Map<Integer, Type> map = new HashMap<Integer, Type>();
		static {
			for(Type t : Type.values())
				map.put(t.val, t);
		}
		
		public static Type fromInt(int val) throws ElfException {
			Type t = map.get(val);
			if(t == null)
				throw new ElfException("Invalid section type: " + val);
			return t;
		}
	}

	
	/** Flag informing that this section contains data that should be writable during process execution. */
	public static final int F_WRITE = 0x1;
	/** Flag informing that section occupies memory during process execution. */
	public static final int F_ALLOC = 0x2;
	/** Flag informing that section contains executable machine instructions. */
	public static final int F_EXECINSTR = 0x4;
	/** Flag informing that the section might be merged */
	public static final int F_MERGE = 0x10;
	/** Flag informing that the section contains null terminated strings */
	public static final int F_STRINGS = 0x20;
	/** sh_info contains SHT index */
	public static final int F_INFO_LINK = 0x40;
	/** Preserved order after combining */
	public static final int F_LINK_ORDER = 0x80;
	/** Non-standard OS specific handing required */
	public static final int F_OS_NONCONFORMING = 0x100;
	/** Section is member of a group */
	public static final int F_GROUP = 0x200;
	/** Section hold thread-local data */
	public static final int F_TLS = 0x400;
	/** OS-Specific mask */
	public static final int F_MASKOS = 0x0ff00000;
	/** Flag informing that all the bits in the mask are reserved for processor specific semantics. */
	public static final int F_MASKPROC = 0xf0000000;

	/** Section header name identifying the section as a string table. */
	public static final String STRING_TABLE_NAME = ".strtab";
	/** Section header name identifying the section as a dynamic string table. */
	public static final String DYNAMIC_STRING_TABLE_NAME = ".dynstr";

	
	/** Index into the section header string table which gives the name of the section. */
	private final int name_ndx; // Elf32_Word or Elf64_Word - 4 bytes in both.
	
	/** Section content and semantics. */
	private final Type type; // Elf32_Word or Elf64_Word - 4 bytes in both.
	
	/** Flags. */
	private final long flags; // Elf32_Word or Elf64_Xword.
	
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
		type = Type.fromInt(parser.readInt());
		flags = parser.readIntOrLong();
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
	
	public static ElfSection elfSectionFactory(final ElfFile file, long offset) {
		ElfSection s = new ElfSection(file, offset);
		
		switch (s.type) {
		case SYMTAB:
		case DYNSYM:
			return new ElfSymbolTableSection(s);
		case STRTAB:
			return new ElfStringTableSection(s);
		case HASH:
//			hashTable = new MemoizedObject<ElfHashTable>() {
//				@Override
//				public ElfHashTable computeValue() throws IOException {
//					return new ElfHashTable(parser, section_offset, (int) size);
//				}
//			};
			break;
		case DYNAMIC:
//			dynamicStructure = new MemoizedObject<ElfDynamicStructure>() {
//				@Override
//				protected ElfDynamicStructure computeValue() throws ElfException, IOException {
//					return new ElfDynamicStructure(file, section_offset, (int) size);
//				}
//			};
			break;
		case NOTE:
//		    note = new MemoizedObject<ElfNote>() {
//                @Override
//                protected ElfNote computeValue() throws ElfException, IOException {
//                    return new ElfNote(parser, section_offset, (int)size);
//                }
//            };
			break;
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
	public long getFlags() {
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
		return file.getSectionHeaders().getNameStringTableSection().getString(name_ndx);
	}
	
	/**
	 * @return Returns the section which is linked by this section
	 */
	public ElfSection getLink() {
		return file.getSectionHeaders().getSectionByIndex(link);
	}

	@Override
	public String toString() {
		return "ElfSectionHeader[name=" + getName() + ", type=" + type.name() + "]";
	}
}
