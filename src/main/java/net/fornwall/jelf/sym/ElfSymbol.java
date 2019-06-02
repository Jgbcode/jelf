package net.fornwall.jelf.sym;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfParser;

/**
 * Class corresponding to the Elf32_Sym/Elf64_Sym struct.
 */
public class ElfSymbol {
	public static final class Binding {
		private Binding() {};
		
		/** Binding specifying that local symbols are not visible outside the object file that contains its definition. */
		public static final int LOCAL = 0;
		/** Binding specifying that global symbols are visible to all object files being combined. */
		public static final int GLOBAL = 1;
		/** Binding specifying that the symbol resembles a global symbol, but has a lower precedence. */
		public static final int WEAK = 2;
		/** Lower bound binding values reserved for processor specific semantics. */
		public static final int LOPROC = 13;
		/** Upper bound binding values reserved for processor specific semantics. */
		public static final int HIPROC = 15;
		
		public static final String string(int type) {
			switch (type) {
			case LOCAL:
				return "local";
			case GLOBAL:
				return "global";
			case WEAK:
				return "weak";
			default:
				if(type >= LOPROC && type <= HIPROC)
					return "proc reserved";
				return "?";
			}
		}
	}

	public static final class Type {
		private Type() {};
		
		/** Type specifying that the symbol is unspecified. */
		public static final byte NOTYPE = 0;
		/** Type specifying that the symbol is associated with an object. */
		public static final byte OBJECT = 1;
		/** Type specifying that the symbol is associated with a function or other executable code. */
		public static final byte FUNC = 2;
		/**
		 * Type specifying that the symbol is associated with a section. Symbol table entries of this type exist for
		 * relocation and normally have the binding BINDING_LOCAL.
		 */
		public static final byte SECTION = 3;
		/** Type defining that the symbol is associated with a file. */
		public static final byte FILE = 4;
		/** The symbol labels an uninitialized common block. */
		public static final byte OMMON = 5;
		/** The symbol specifies a Thread-Local Storage entity. */
		public static final byte TLS = 6;
	
		/** Lower bound for range reserved for operating system-specific semantics. */
		public static final byte LOOS = 10;
		/** Upper bound for range reserved for operating system-specific semantics. */
		public static final byte HIOS = 12;
		/** Lower bound for range reserved for processor-specific semantics. */
		public static final byte LOPROC = 13;
		/** Upper bound for range reserved for processor-specific semantics. */
		public static final byte HIPROC = 15;
		
		public static final String string(int type) {
			switch (type) {
			case NOTYPE:
				return "none";
			case OBJECT:
				return "object";
			case FUNC:
				return "function";
			case SECTION:
				return "section";
			case FILE:
				return "file";
			default:
				if(type >= LOPROC && type <= HIPROC)
					return "proc reserved";
				return "?";
			}
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
	/** Currently holds the value of 0 and has no meaning. */
	private final short other; // unsigned char
	/**
	 * Index to the associated section header. This value will need to be read as an unsigned short if we compare it to
	 * ELFSectionHeader.NDX_LORESERVE and ELFSectionHeader.NDX_HIRESERVE.
	 */
	private final short section_header_ndx; // Elf32_Half

	private final ElfFile file;
	
	private ElfSymbol(ElfFile file, long offset) {
		this.file = file;
		ElfParser parser = file.getParser();
		ElfHeader header = file.getHeader();
		
		parser.seek(offset);
		if (header.getBitClass() == ElfHeader.BitClass.ELFCLASS32) {
			name_ndx = parser.readInt();
			value = parser.readInt();
			size = parser.readInt();
			info = parser.readUnsignedByte();
			other = parser.readUnsignedByte();
			section_header_ndx = parser.readShort();
		} else {
			name_ndx = parser.readInt();
			info = parser.readUnsignedByte();
			other = parser.readUnsignedByte();
			section_header_ndx = parser.readShort();
			value = parser.readLong();
			size = parser.readLong();
		}
	}
	
	protected ElfSymbol(ElfSymbol sym) {
		this.file = sym.file;
		this.name_ndx = sym.name_ndx;
		this.value = sym.value;
		this.size = sym.size;
		this.info = sym.info;
		this.other = sym.other;
		this.section_header_ndx = sym.section_header_ndx;
	}
	
	public static ElfSymbol symbolFactory(ElfFile file, long offset) {
		ElfSymbol s = new ElfSymbol(file, offset);
		
		/*
		 * Return subclass if needed
		 */
		
		return s;
	}
	
	public int getNameIndex() {
		return name_ndx;
	}

	public long getValue() {
		return value;
	}

	public long getSize() {
		return size;
	}

	public short getInfo() {
		return info;
	}

	public short getOther() {
		return other;
	}

	public short getSectionHeaderIndex() {
		return section_header_ndx;
	}

	public ElfFile getFile() {
		return file;
	}

	/** Returns the binding for this symbol. */
	public int getBinding() {
		return info >> 4;
	}

	/** Returns the symbol type. */
	public int getType() {
		return info & 0x0F;
	}

	/** Returns the name of the symbol */
	public String getName() {
		return file.getSectionHeaders().getStringTable().getString(name_ndx);
	}
}
