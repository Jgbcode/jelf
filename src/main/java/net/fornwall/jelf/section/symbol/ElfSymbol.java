package net.fornwall.jelf.section.symbol;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;

/**
 * Class corresponding to the Elf32_Sym/Elf64_Sym struct.
 */
public class ElfSymbol {
	public static final class Binding {
		/** STB_LOCAL: Binding specifying that local symbols are not visible outside the object file that contains its definition. */
		public static final int LOCAL = 0;
		/** STB_GLOBAL: Binding specifying that global symbols are visible to all object files being combined. */
		public static final int GLOBAL = 1;
		/** STB_WEAK: Binding specifying that the symbol resembles a global symbol, but has a lower precedence. */
		public static final int WEAK = 2;
		/** STB_NUM: Number of defined types.  */
		public static final int NUM = 3;
		/** STB_LOOS: Start of OS-specific */
		public static final int LOOS = 10;
		/** STB_GNU_UNIQUE: Unique symbol.  */
		public static final int GNU_UNIQUE = 10;
		/** STB_HIOS: End of OS-specific */
		public static final int HIOS = 12;
		/** STB_LOPROC: Lower bound binding values reserved for processor specific semantics. */
		public static final int LOPROC = 13;
		/** STB_HIPROC: Upper bound binding values reserved for processor specific semantics. */
		public static final int HIPROC = 15;
		
		public final int val;
		
		public Binding(int val) {
			this.val = val;
		}
		
		public String name() {
			switch (val) {
			case LOCAL:
				return "LOCAL";
			case GLOBAL:
				return "GLOBAL";
			case WEAK:
				return "WEAK";
			case NUM:
				return "NUM";
			case GNU_UNIQUE:
				return "GNU_UNIQUE";
			default:
				if(val >= LOOS && val <= HIOS)
					return "OS";
				if(val >= LOPROC && val <= HIPROC)
					return "PROC";
				return "?";
			}
		}
	}

	public static final class Type {
		/** STT_NOTYPE: Type specifying that the symbol is unspecified. */
		public static final byte NOTYPE = 0;
		/** STT_OBJECT: Type specifying that the symbol is associated with an object. */
		public static final byte OBJECT = 1;
		/** STT_FUNC: Type specifying that the symbol is associated with a function or other executable code. */
		public static final byte FUNC = 2;
		/**
		 * STT_SECTION:
		 * Type specifying that the symbol is associated with a section. Symbol table entries of this type exist for
		 * relocation and normally have the binding BINDING_LOCAL.
		 */
		public static final byte SECTION = 3;
		/** STT_FILE: Type defining that the symbol is associated with a file. */
		public static final byte FILE = 4;
		/** STT_COMMON: The symbol labels an uninitialized common block. */
		public static final byte COMMON = 5;
		/** STT_TLS: The symbol specifies a Thread-Local Storage entity. */
		public static final byte TLS = 6;
		/** STT_NUM: Number of defined types.  */
		public static final byte NUM = 7;
		/** STT_LOOS: Lower bound for range reserved for operating system-specific semantics. */
		public static final byte LOOS = 10;
		/** STT_GNU_IFUNC: Symbol is indirect code object */
		public static final byte GNU_IFUNC = 10;
		/** STT_HIOS: Upper bound for range reserved for operating system-specific semantics. */
		public static final byte HIOS = 12;
		/** STT_LOPROC: Lower bound for range reserved for processor-specific semantics. */
		public static final byte LOPROC = 13;
		/** STT_HIPROC: Upper bound for range reserved for processor-specific semantics. */
		public static final byte HIPROC = 15;
		
		public final int val;
		
		public Type(int val) {
			this.val = val;
		}
		
		public String name() {
			switch (val) {
			case NOTYPE:
				return "NONE";
			case OBJECT:
				return "OBJECT";
			case FUNC:
				return "FUNCTION";
			case SECTION:
				return "SECTION";
			case FILE:
				return "FILE";
			case COMMON:
				return "COMMON";
			case TLS:
				return "TLS";
			case NUM:
				return "NUM";
			case GNU_IFUNC:
				return "GNU_IFUNC";
			default:
				if(val >= LOOS && val <= HIOS)
					return "OS";
				if(val >= LOPROC && val <= HIPROC)
					return "PROC";
				return "?";
			}
		}
	}
	
	/**
	 * Encodes symbol visibility
	 */
	public static final class Other {
		/** STV_DEFAULT: Default symbol visibility rules */
		public static final short DEFAULT = 0;
		/** STV_INTERAL: Processor specific hidden class */
		public static final short INTERNAL = 1;
		/** STV_HIDDEN: Sym unavailable in other modules */
		public static final short HIDDEN = 2;
		/** STV_PROTECTED: Not preemptible, not exported */
		public static final short PROTECTED = 3;
		
		public final short val;
		
		public Other(short val) {
			this.val = val;
		}
		
		/**
		 * @return Returns a human readable name of what this encodes
		 */
		public String name() {
			switch(val) {
			case DEFAULT:
				return "DEFAULT";
			case INTERNAL:
				return "INTERNAL";
			case HIDDEN:
				return "HIDDEN";
			case PROTECTED:
				return "PROTECTED";
			default:
				return "?";
			}
		}
	}
	
	public static class SectionIndex {
		/** SHN_UNDEF: Undefined section */
		public static final short UNDEF = 0;
		/** SHN_LORESERVE: Start of reserved indices */
		public static final short LORESERVE = (short)0xff00;
		/** SHN_LOPROC: Start of processor-specific */
		public static final short LOPROC = (short)0xff00;
		/** SHN_BEFORE: Order section before all others (Solaris).  */
		public static final short BEFORE = (short)0xff00;
		/** SHN_AFTER: Order section after all others (Solaris).  */
		public static final short AFTER = (short)0xff01;
		/** SHN_HIPROC: End of processor-specific */
		public static final short HIPROC = (short)0xff1f;
		/** SHN_LOOS: Start of OS-specific */
		public static final short LOOS = (short)0xff20;
		/** SHN_HIOS: End of OS-specific */
		public static final short HIOS = (short)0xff3f;
		/** SHN_ABS: Associated symbol is absolute */
		public static final short ABS = (short)0xfff1;
		/** SHN_COMMON: Associated symbol is common */
		public static final short COMMON = (short)0xfff2;
		/** SHN_XINDEX: Index is in extra table.  */
		public static final short XINDEX = (short)0xffff;
		/** SHN_HIRESERVE: End of reserved indices */
		public static final short HIRESERVE = (short)0xffff;
		
		public final short val;
		
		public SectionIndex(short val) {
			this.val = val;
		}
		
		/**
		 * @return Returns the name of this symbol index if it has a special
		 *  value. Otherwise, the index is returned as a string encoded integer.
		 */
		public String name() {
			switch(val) {
			case UNDEF:
				return "UNDEF";
			case BEFORE:
				return "BEFORE";
			case AFTER:
				return "AFTER";
			case ABS:
				return "ABS";
			case COMMON:
				return "COMMON";
			case XINDEX:
				return "XINDEX";
			default:
				if(val >= LOPROC && val <= HIPROC)
					return "PROC";
				if(val >= LOOS && val <= HIOS)
					return "OS";
				if(val >= LORESERVE && val <= HIRESERVE)
					return "RESERVED";
				return Short.toString(val);
			}
		}
		
		/**
		 * @return Returns true if this index is not standard
		 */
		public boolean isReserved() {
			return val >= LORESERVE && val <= HIRESERVE;
		}
	}
		
	/**
	 * Index into the symbol string table that holds the character representation of the symbols. 0 means the symbol has
	 * no character name.
	 */
	private final int name_ndx; // Elf32_Word
	/** Value of the associated symbol. This may be a relative address for .so or absolute address for other ELFs. */
	private final long value; // Elf32_Addr
	/** Size of the symbol. 0 if the symbol has no size or the size is unknown. */
	private final long size; // Elf32_Word
	/** Specifies the symbol type and binding attributes. */
	private final short info; // unsigned char
	/** Currently holds visibility information */
	private final Other other; // unsigned char
	/**
	 * Index to the associated section header. This value will need to be read as an unsigned short if we compare it to
	 * ELFSectionHeader.NDX_LORESERVE and ELFSectionHeader.NDX_HIRESERVE.
	 */
	private final SectionIndex section_header_ndx; // Elf32_Half
	
	// Calculated values
	private final Binding binding;
	private final Type type;

	private final ElfSymbolTableSection table;
	
	private ElfSymbol(ElfSymbolTableSection table, long offset) {
		this.table = table;
		ElfParser parser = table.getFile().getParser();
		ElfHeader header = table.getFile().getHeader();
		
		parser.seek(offset);
		if (header.getBitClass() == ElfHeader.BitClass.ELFCLASS32) {
			name_ndx = parser.readInt();
			value = parser.readInt();
			size = parser.readInt();
			info = parser.readUnsignedByte();
			other = new Other(parser.readUnsignedByte());
			section_header_ndx = new SectionIndex(parser.readShort());
		} else {
			name_ndx = parser.readInt();
			info = parser.readUnsignedByte();
			other = new Other(parser.readUnsignedByte());
			section_header_ndx = new SectionIndex(parser.readShort());
			value = parser.readLong();
			size = parser.readLong();
		}
		
		binding = new Binding(info >> 4);
		type = new Type(info & 0xf);
	}
	
	protected ElfSymbol(ElfSymbol sym) {
		this.table = sym.table;
		this.name_ndx = sym.name_ndx;
		this.value = sym.value;
		this.size = sym.size;
		this.info = sym.info;
		this.other = sym.other;
		this.section_header_ndx = sym.section_header_ndx;
		this.binding = sym.binding;
		this.type = sym.type;
	}
	
	public static ElfSymbol symbolFactory(ElfFile file, ElfSymbolTableSection table, long offset) {
		ElfSymbol s = new ElfSymbol(table, offset);
		
		/*
		 * TODO: Return subclass if needed
		 */
		
		return s;
	}
	
	/** 
	 * This member holds an index into the object file’s symbol string table, which holds the
	 * character representations of the symbol names. If the value is non-zero, it represents a
	 * string table index that gives the symbol name. Otherwise, the symbol table entry has no
	 * name
	 * 
	 * @return Gets the index of this symbols name in the associated string table 
	 */
	public int getNameIndex() {
		return name_ndx;
	}

	/**
	 * This member gives the value of the associated symbol. Depending on the context, this
	 * may be an absolute value, an address, etc
	 * 
	 * @return Returns the value associated with this symbol
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Many symbols have associated sizes. For example, a data object’s size is the number of
	 * bytes contained in the object. This member holds 0 if the symbol has no size or an
	 * unknown size.
	 * 
	 * @return Returns the associated size of this symbol
	 */
	public long getSize() {
		return size;
	}

	/**
	 * This member specifies the symbol’s type and binding attributes. See {@link #getBinding} and
	 * 	{@link #getType} for methods which further parse this attribute
	 * 
	 * @return Returns the info associated with this symbol
	 */
	public short getInfo() {
		return info;
	}

	/**
	 * See {@link Other}
	 * 
	 * @return Returns the other attribute associated with this symbol
	 */
	public Other getOther() {
		return other;
	}

	/** @return Returns the index of the section this symbol is associated with */
	public SectionIndex getSectionHeaderIndex() {
		return section_header_ndx;
	}
	
	/** @return Returns the file this symbol is associated with */
	public ElfFile getFile() {
		return table.getFile();
	}

	/** @return Returns the binding for this symbol. */
	public Binding getBinding() {
		return binding;
	}

	/** @return Returns the symbol type. */
	public Type getType() {
		return type;
	}

	/** @return Returns the name of the symbol */
	public String getName() {
		return table.getStringTable().getString(name_ndx);
	}
	
	/** @return Returns the section associated with this symbol */
	public ElfSection getSection() {
		if(section_header_ndx.isReserved())
			throw new ElfException("Attempting to access reserved section: " + section_header_ndx.name());
		return table.getFile().getSectionHeaders().getSectionByIndex(section_header_ndx.val);
	}
	
	/** @return Returns the section associated with the symbol and insures it is of a certion type c */
	public ElfSection getSection(Class<? extends ElfSection> c) {
		if(section_header_ndx.isReserved())
			throw new ElfException("Attempting to access reserved section: " + section_header_ndx.name());
		return table.getFile().getSectionHeaders().getSectionByIndex(section_header_ndx.val, c);
	}
}
