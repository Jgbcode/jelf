package net.fornwall.jelf;

import java.util.HashMap;
import java.util.Map;

import net.fornwall.jelf.app.Table;
import net.fornwall.jelf.app.Table.Align;
import net.fornwall.jelf.segment.ElfSegment;

public class ElfHeader {
	/**
	 *	Describes the address size of the ELF file
	 */
	public enum BitClass {
		/** 32-bit objects. */
		ELFCLASS32((byte)1),
		/** 64-bit objects. */
		ELFCLASS64((byte)2);
		
		public final byte val;
		
		private BitClass(byte val) {
			this.val = val;
		}
		
		private static final Map<Byte, BitClass> map = new HashMap<Byte, BitClass>();
		static {
			for(BitClass c : BitClass.values())
				map.put(c.val, c);
		}
		
		public static BitClass fromByte(byte val) throws ElfException {
			BitClass c = map.get(val);
			if(c == null)
				throw new ElfException("Invalid class: " + val);
			return c;
		}
	}
	
	/**
	 * Describes the endianness of the ELF file
	 */
	public enum DataFormat {
		/** Little endian format (LSB in lower address) */
		ELFDATA2LSB((byte)1),
		/** Big endian format (MSB in lower address) */
		ELFDATA2MSB((byte)2);
		
		public final byte val;
		
		private DataFormat(byte val) {
			this.val = val;
		}
		
		private static final Map<Byte, DataFormat> map = new HashMap<Byte, DataFormat>();
		static {
			for(DataFormat d : DataFormat.values())
				map.put(d.val, d);
		}
		
		public static DataFormat fromByte(byte val) throws ElfException {
			DataFormat d = map.get(val);
			if(d == null)
				throw new ElfException("Invalid object size class: " + val);
			return d;
		}
	}
	
	/**
	 * Describes the ELF version of the file. There is currently only one version
	 */
	public enum Version {
		/** Current version */
		EV_CURRENT(1);
		
		public final int val;
		
		private Version(int val) {
			this.val = val;
		}
		
		private static final Map<Integer, Version> map = new HashMap<Integer, Version>();
		static {
			for(Version v : Version.values())
				map.put(v.val, v);
		}
		
		public static Version fromByte(int val) throws ElfException {
			Version v = map.get(val);
			if(v == null)
				throw new ElfException("Invalid version: " + val);
			return v;
		}
	}
	
	/**
	 *	Describes the type of ELF file
	 */
	public enum FileType {
		/** Relocatable file type. A possible value of {@link #file_type}. */
		REL((short)1),
		/** Executable file type. A possible value of {@link #file_type}. */
		EXEC((short)2),
		/** Shared object file type. A possible value of {@link #file_type}. */
		DYN((short)3),
		/** Core file file type. A possible value of {@link #file_type}. */
		CORE((short)4);
		
		public final short val;
		
		private FileType(short val) {
			this.val = val;
		}
		
		private static final Map<Short, FileType> map = new HashMap<Short, FileType>();
		static {
			for(FileType ft : FileType.values())
				map.put(ft.val, ft);
		}
		
		public static FileType fromShort(short val) {
			FileType ft = map.get(val);
			if(ft == null)
				throw new ElfException("Invalid file type: " + val);
			return ft;
		}
	}
	
	/**
	 * 	Describes the target machine
	 */
	public enum Machine {
		/** No architecture type. */
		NONE((short)0),
		/** AT&amp;T architecture type. */
		ATT((short)1),
		/** SPARC architecture type. */
		SPARC((short)2),
		/** Intel 386 architecture type. */
		I386((short)3),
		/** Motorola 68000 architecture type. */
		M68k((short)4),
		/** Motorola 88000 architecture type. */
		M88k((short)5),
		/** Intel 860 architecture type. */
		I860((short)7),
		/** MIPS architecture type. */
		MIPS((short)8),
		/** ARM architecture type */
		ARM((short)0x28),
		/** x86_64 architecture type */
		X64((short)0x3E),
		/** AArch64 architecture type */
		AARCH64((short)0xB7),
		/** RISCV architecture type */
		RISCV((short)0xF3);
		
		private final short val;
		
		private Machine(short val) {
			this.val = val;
		}
		
		private static final Map<Short, Machine> map = new HashMap<Short, Machine>();
		static {
			for(Machine m : Machine.values())
				map.put(m.val, m);
		}
		
		public static Machine fromShort(short val) {
			Machine m = map.get(val);
			if(m == null)
				throw new ElfException("Invalid machine architecture: " + val);
			return m;
		}
	}
	
	
	/** Byte identifying the size of objects */
	private BitClass ei_class;
	/**
	 * Returns a byte identifying the data encoding of the processor specific data. This byte will be either
	 * DATA_INVALID, DATA_LSB or DATA_MSB.
	 */
	private DataFormat ei_data;
	/** Version */
	private Version ei_version; // Elf32_Word
	
	
	/** Identifies the object file type. */
	private FileType e_type; // Elf32_Half
	/** The required architecture. One of the ARCH_* constants in the class. */
	private Machine e_machine; // Elf32_Half
	/**
	 * Virtual address to which the system first transfers control. If there is no entry point for the file the value is
	 * 0.
	 */
	private long e_entry; // Elf32_Addr
	/** Program header table offset in bytes. If there is no program header table the value is 0. */
	private long e_phoff; // Elf32_Off
	/** Section header table offset in bytes. If there is no section header table the value is 0. */
	private long e_shoff; // Elf32_Off
	/** Processor specific flags. */
	private int e_flags; // Elf32_Word
	/** ELF header size in bytes. */
	private short e_ehsize; // Elf32_Half
	/** e_phentsize. Size of one entry in the file's program header table in bytes. All entries are the same size. */
	private short e_phentsize; // Elf32_Half
	/** e_phnum. Number of {@link ElfSegment} entries in the program header table, 0 if no entries. */
	private short e_phnum; // Elf32_Half
	/** Section header entry size in bytes. */
	private short e_shentsize; // Elf32_Half
	/** Number of entries in the section header table, 0 if no entries. */
	private short e_shnum; // Elf32_Half
	/**
	 * Elf{32,64}_Ehdr#e_shstrndx. Index into the section header table associated with the section name string table.
	 * SH_UNDEF if there is no section name string table.
	 */
	private short e_shstrndx; // Elf32_Half
	
	private final ElfParser parser;
	
	/**
	 * Constructs the header of the ELF file, this should not be called directly as
	 * {@link ElfParser} will automatically create the header when it is instantiated
	 * 
	 * Note, this only instantiates the header components that are vital for parsing. The
	 * other components must be initialized using {@link #parse()}.
	 * 
	 * @param parser the ELF parser object
	 */
	public ElfHeader(ElfParser parser) {
		this.parser = parser;
		
        byte[] ident = new byte[16];
        int bytesRead = parser.read(ident);
        if (bytesRead != ident.length)
            throw new ElfException("Error reading elf header (read " + bytesRead + "bytes - expected to read " + ident.length + "bytes)");

        if (!(0x7f == ident[0] && 'E' == ident[1] && 'L' == ident[2] && 'F' == ident[3])) throw new ElfException("Bad magic number for file");

        ei_class = BitClass.fromByte(ident[4]);
        ei_data = DataFormat.fromByte(ident[5]);
        
        int elfVersion = ident[6];
        if (elfVersion != 1) throw new ElfException("Invalid elf version: " + elfVersion);
        // ident[7]; // EI_OSABI, target operating system ABI
        // ident[8]; // EI_ABIVERSION, ABI version. Linux kernel (after at least 2.6) has no definition of it.
        // ident[9-15] // EI_PAD, currently unused.
	}
	
	/**
	 * Completes the parsing of non vital header components. This is automatically done by
	 * {@link ElfParser} and should not be called directly.
	 */
	public void parse() {
        e_type = FileType.fromShort(parser.readShort());
        e_machine = Machine.fromShort(parser.readShort());
        ei_version = Version.fromByte(parser.readInt());
        e_entry = parser.readIntOrLong();
        e_phoff = parser.readIntOrLong();
        e_shoff = parser.readIntOrLong();
        e_flags = parser.readInt();
        e_ehsize = parser.readShort();
        e_phentsize = parser.readShort();
        e_phnum = parser.readShort();
        e_shentsize = parser.readShort();
        e_shnum = parser.readShort();
        if (e_shnum == 0) {
            throw new ElfException("e_shnum is SHN_UNDEF(0), which is not supported yet"
                    + " (the actual number of section header table entries is contained in the sh_size field of the section header at index 0)");
        }
        e_shstrndx = parser.readShort();
        if (e_shstrndx == /* SHN_XINDEX= */0xffff) {
            throw new ElfException("e_shstrndx is SHN_XINDEX(0xffff), which is not supported yet"
                    + " (the actual index of the section name string table section is contained in the sh_link field of the section header at index 0)");
        }
	}

	/**
	 * @return Returns a {@link BitClass} which describes the address size (32 / 64 bit)
	 * described by this header.
	 */
	public BitClass getBitClass() {
		return ei_class;
	}

	/**
	 * @return Returns the {@link DataFormat} described by this header.
	 */
	public DataFormat getDataFormat() {
		return ei_data;
	}

	/**
	 * @return Returns the {@link Version} described by this header.
	 */
	public Version getVersion() {
		return ei_version;
	}

	/**
	 * @return Returns the {@link FileType} described by this header.
	 */
	public FileType getFileType() {
		return e_type;
	}

	/**
	 * @return Returns the target {@link Machine} described by this header.
	 */
	public Machine getMachine() {
		return e_machine;
	}

	/**
	 * @return Returns the entry address described by this header.
	 */
	public long getEntryAddress() {
		return e_entry;
	}

	/**
	 * @return Returns the file offset of the program header, the data at this
	 * offset will be automatically parsed into a {@link ElfProgramHeaders} object
	 */
	public long getProgramHeaderOffset() {
		return e_phoff;
	}

	/**
	 * @return Returns the file offset of the section header, the data at this
	 * offset will be automatically parsed into a {@link ElfSectionHeaders} object
	 */
	public long getSectionHeaderOffset() {
		return e_shoff;
	}

	/**
	 * @return Returns the flags described by this header.
	 */
	public int getFlags() {
		return e_flags;
	}

	/**
	 * @return Returns the file size in bytes of this header.
	 */
	public short getSize() {
		return e_ehsize;
	}

	/**
	 * @return Returns the file size in bytes of each entry in the program header
	 */
	public short getProgramHeaderEntrySize() {
		return e_phentsize;
	}

	/**
	 * @return Returns the number of entries in the program header
	 */
	public short getProgramHeaderEntryCount() {
		return e_phnum;
	}

	/**
	 * @return Returns the file size in bytes of each entry in the section header
	 */
	public short getSectionHeaderEntrySize() {
		return e_shentsize;
	}

	/**
	 * @return Returns the number of entries in the section header
	 */
	public short getSectionHeaderEntryCount() {
		return e_shnum;
	}

	/**
	 * @return Returns the index of the section header table that contains the section names
	 */
	public short getSectionHeaderStringTableIndex() {
		return e_shstrndx;
	}
	
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this header.
	 */
	public Table getFormattedTable() {
		Table t = new Table("ELF Header:");
		
		t.addCell("Class:");
		t.setColAlign(Align.LEFT);
		t.addCell(getBitClass().name());
		t.setColAlign(Align.LEFT);
		t.newRow();
		
		t.addCell("Data:");
		t.addCell(getDataFormat().name());
		t.newRow();
		
		t.addCell("Version:");
		t.addCell(getVersion().name());
		t.newRow();
		
		t.addCell("Type:");
		t.addCell(getFileType().name());
		t.newRow();
		
		t.addCell("Machine:");
		t.addCell(getMachine().name());
		t.newRow();
		
		t.addCell("Entry point address:");
		t.addCell("0x" + Long.toHexString(getEntryAddress()));
		t.newRow();
		
		t.addCell("Start of program headers:");
		t.addCell(getProgramHeaderOffset() + " (bytes into file)");
		t.newRow();
		
		t.addCell("Start of section headers:");
		t.addCell(getSectionHeaderOffset() + " (bytes into file)");
		t.newRow();
		
		t.addCell("Flags:");
		t.addCell(Integer.toHexString(getFlags()));
		t.newRow();
		
		t.addCell("Size of this header:");
		t.addCell(getSize() + " (bytes)");
		t.newRow();
		
		t.addCell("Size of program headers:");
		t.addCell(getProgramHeaderEntrySize() + " (bytes)");
		t.newRow();
		
		t.addCell("Number of program headers:");
		t.addCell(Short.toString(getProgramHeaderEntryCount()));
		t.newRow();
		
		t.addCell("Size of section headers:");
		t.addCell(getSectionHeaderEntrySize() + " (bytes)");
		t.newRow();
		
		t.addCell("Number of section headers:");
		t.addCell(Short.toString(getSectionHeaderEntryCount()));
		t.newRow();
		
		t.addCell("Section header string table index:");
		t.addCell(Short.toString(getSectionHeaderStringTableIndex()));
		
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
