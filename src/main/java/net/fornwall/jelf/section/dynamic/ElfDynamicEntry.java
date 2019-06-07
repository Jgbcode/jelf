package net.fornwall.jelf.section.dynamic;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.ElfDynamicSection;

/**
 * http://www.sco.com/developers/gabi/latest/ch5.dynamic.html#dynamic_section
 * 
 * "If an object file participates in dynamic linking, its program header table will have an element of type PT_DYNAMIC. This ``segment'' contains the .dynamic
 * section. A special symbol, _DYNAMIC, labels the section, which contains an array of the following structures."
 * 
 * <pre>
 * typedef struct { Elf32_Sword d_tag; union { Elf32_Word d_val; Elf32_Addr d_ptr; } d_un; } Elf32_Dyn;
 * extern Elf32_Dyn _DYNAMIC[];
 * 
 * typedef struct { Elf64_Sxword d_tag; union { Elf64_Xword d_val; Elf64_Addr d_ptr; } d_un; } Elf64_Dyn;
 * extern Elf64_Dyn _DYNAMIC[];
 * </pre>
 * 
 * <pre>
 * http://www.sco.com/developers/gabi/latest/ch5.dynamic.html:
 * 
 * Name	        		Value		d_un		Executable	Shared Object
 * ----------------------------------------------------------------------
 * DT_NULL	    		0			ignored		mandatory	mandatory
 * DT_NEEDED			1			d_val		optional	optional
 * DT_PLTRELSZ			2			d_val		optional	optional
 * DT_PLTGOT			3			d_ptr		optional	optional
 * DT_HASH				4			d_ptr		mandatory	mandatory
 * DT_STRTAB			5			d_ptr		mandatory	mandatory
 * DT_SYMTAB			6			d_ptr		mandatory	mandatory
 * DT_RELA				7			d_ptr		mandatory	optional
 * DT_RELASZ			8			d_val		mandatory	optional
 * DT_RELAENT			9			d_val		mandatory	optional
 * DT_STRSZ				10			d_val		mandatory	mandatory
 * DT_SYMENT			11			d_val		mandatory	mandatory
 * DT_INIT  			12			d_ptr		optional	optional
 * DT_FINI	    		13			d_ptr		optional	optional
 * DT_SONAME			14			d_val		ignored		optional
 * DT_RPATH*			15			d_val		optional	ignored
 * DT_SYMBOLIC*			16			ignored		ignored		optional
 * DT_REL	    		17			d_ptr		mandatory	optional
 * DT_RELSZ	    		18			d_val		mandatory	optional
 * DT_RELENT			19			d_val		mandatory	optional
 * DT_PLTREL			20			d_val		optional	optional
 * DT_DEBUG	    		21			d_ptr		optional	ignored
 * DT_TEXTREL*			22			ignored		optional	optional
 * DT_JMPREL			23			d_ptr		optional	optional
 * DT_BIND_NOW*			24			ignored		optional	optional
 * DT_INIT_ARRAY		25			d_ptr		optional	optional
 * DT_FINI_ARRAY		26			d_ptr		optional	optional
 * DT_INIT_ARRAYSZ		27			d_val		optional	optional
 * DT_FINI_ARRAYSZ		28			d_val		optional	optional
 * DT_RUNPATH			29			d_val		optional	optional
 * DT_FLAGS				30			d_val		optional	optional
 * DT_ENCODING			32			unspecified	unspecified	unspecified
 * DT_PREINIT_ARRAY		32			d_ptr		optional	ignored
 * DT_PREINIT_ARRAYSZ	33			d_val		optional	ignored
 * DT_LOOS				0x6000000D	unspecified	unspecified	unspecified
 * DT_HIOS				0x6ffff000	unspecified	unspecified	unspecified
 * DT_LOPROC			0x70000000	unspecified	unspecified	unspecified
 * DT_HIPROC			0x7fffffff	unspecified	unspecified	unspecified
 * </pre>
 */
public class ElfDynamicEntry {
	public static class Type {
		/** DT_NULL: Marks end of dynamic section */
		public static final int NULL = 0;
		/** DT_NEEDED: Name of needed library */
		public static final int NEEDED = 1;
		/** DT_PLTRELSZ: Size in bytes of PLT relocs */
		public static final int PLTRELSZ = 2;
		/** DT_PLTGOT: Processor defined value */
		public static final int PLTGOT = 3;
		/** DT_HASH: Address of symbol hash table */
		public static final int HASH = 4;
		/** DT_STRTAB: Address of string table */
		public static final int STRTAB = 5;
		/** DT_SYMTAB: Address of symbol table */
		public static final int SYMTAB = 6;
		/** DT_RELA: Address of Rela relocs */
		public static final int RELA = 7;
		/** DT_RELASZ: Total size of Rela relocs */
		public static final int RELASZ = 8;
		/** DT_RELAENT: Size of one Rela reloc */
		public static final int RELAENT = 9;
		/** DT_STRSZ: Size of string table */
		public static final int STRSZ = 10;
		/** DT_SYMENT: Size of one symbol table entry */
		public static final int SYMENT = 11;
		/** DT_INIT: Address of init function */
		public static final int INIT = 12;
		/** DT_FINI: Address of termination function */
		public static final int FINI = 13;
		/** DT_SONAME: Name of shared object */
		public static final int SONAME = 14;
		/** DT_RPATH: Library search path (deprecated) */
		public static final int RPATH = 15;
		/** DT_SYMBOLIC: Start symbol search here */
		public static final int SYMBOLIC = 16;
		/** DT_REL: Address of Rel relocs */
		public static final int REL = 17;
		/** DT_RELSZ: Total size of Rel relocs */
		public static final int RELSZ = 18;
		/** DT_RELENT: Size of one Rel reloc */
		public static final int RELENT = 19;
		/** DT_PLTREL: Type of reloc in PLT */
		public static final int PLTREL = 20;
		/** DT_DEBUG: For debugging; unspecified */
		public static final int DEBUG = 21;
		/** DT_TEXTREL: Reloc might modify .text */
		public static final int TEXTREL = 22;
		/** DT_JMPREL: Address of PLT relocs */
		public static final int JMPREL = 23;
		/** DT_BIND_NOW: Process relocations of object */
		public static final int BIND_NOW = 24;
		/** DT_INIT_ARRAY: Array with addresses of init fct */
		public static final int INIT_ARRAY = 25;
		/** DT_FINI_ARRAY: Array with addresses of fini fct */
		public static final int FINI_ARRAY = 26;
		/** DT_INIT_ARRAYSZ: Size in bytes of DT_INIT_ARRAY */
		public static final int INIT_ARRAYSZ = 27;
		/** DT_FINI_ARRAYSZ: Size in bytes of DT_FINI_ARRAY */
		public static final int FINI_ARRAYSZ = 28;
		/** DT_RUNPATH: Library search path */
		public static final int RUNPATH = 29;
		/** DT_FLAGS: Flags for the object being loaded */
		public static final int FLAGS = 30;
		/** DT_ENCODING: Start of encoded range */
		public static final int ENCODING = 32;
		/** DT_PREINIT_ARRAY: Array with addresses of preinit fct */
		public static final int PREINIT_ARRAY = 32;
		/** DT_PREINIT_ARRAYSZ: size in bytes of DT_PREINIT_ARRAY */
		public static final int PREINIT_ARRAYSZ = 33;
		/** DT_NUM: Number used */
		public static final int NUM = 34;
		/** DT_LOOS: Start of OS-specific */
		public static final int LOOS = 0x6000000d;
		/** DT_HIOS: End of OS-specific */
		public static final int HIOS = 0x6ffff000;
		/** DT_LOPROC: Start of processor-specific */
		public static final int LOPROC = 0x70000000;
		/** DT_HIPROC: End of processor-specific */
		public static final int HIPROC = 0x7fffffff;
		
		public final int val;
		
		public Type(int val) {
			this.val = val;
		}
		
		public String name() {
			switch(val) {
			case NULL:
				return "NULL";
			case NEEDED:
				return "NEEDED";
			case PLTRELSZ:
				return "PLTRELSZ";
			case PLTGOT:
				return "PLTGOT";
			case HASH:
				return "HASH";
			case STRTAB:
				return "STRTAB";
			case SYMTAB:
				return "SYMTAB";
			case RELA:
				return "RELA";
			case RELASZ:
				return "RELASZ";
			case RELAENT:
				return "RELAENT";
			case STRSZ:
				return "STRSZ";
			case SYMENT:
				return "SYMENT";
			case INIT:
				return "INIT";
			case FINI:
				return "FINI";
			case SONAME:
				return "SONAME";
			case RPATH:
				return "RPATH";
			case SYMBOLIC:
				return "SYMBOLIC";
			case REL:
				return "REL";
			case RELSZ:
				return "RELSZ";
			case RELENT:
				return "RELENT";
			case PLTREL:
				return "PLTREL";
			case DEBUG:
				return "DEBUG";
			case TEXTREL:
				return "TEXTREL";
			case JMPREL:
				return "JMPREL";
			case BIND_NOW:
				return "BIND_NOW";
			case INIT_ARRAY:
				return "INIT_ARRAY";
			case FINI_ARRAY:
				return "FINI_ARRAY";
			case INIT_ARRAYSZ:
				return "INIT_ARRAYSZ";
			case FINI_ARRAYSZ:
				return "FINI_ARRAYSZ";
			case RUNPATH:
				return "RUNPATH";
			case FLAGS:
				return "FLAGS";
			case PREINIT_ARRAY:
				return "PREINIT_ARRAY";
			case PREINIT_ARRAYSZ:
				return "PREINIT_ARRAYSZ";
			default:
				if(val >= LOOS && val <= HIOS)
					return "OS";
				if(val >= LOPROC && val <= HIPROC)
					return "PROC";
				return "?";
			}
		}
	}
	
	private Type type;
	private long addr;
	
	private ElfDynamicSection section;
	
	private ElfDynamicEntry(ElfDynamicSection section, long offset) {
		this.section = section;
		
		ElfParser parser = section.getFile().getParser();
		
		parser.seek(offset);
		type = new Type((int)parser.readIntOrLong());
		addr = parser.readIntOrLong();
	}
	
	public static ElfDynamicEntry dynamicEntryFactory(ElfDynamicSection section, long offset) {
		ElfDynamicEntry e = new ElfDynamicEntry(section, offset);
		
		/*
		 *	TODO: Return subtypes for type specific handling 
		 */
		
		return e;
	}
	
	/**
	 * @return Returns the file that contains this dynamic entry
	 */
	public ElfFile getFile() {
		return section.getFile();
	}
	
	/**
	 * @return Returns the dynamic section that contains this dynamic entry
	 */
	public ElfDynamicSection getSection() {
		return section;
	}
	
	/**
	 * @return Returns the type of this dynamic entry
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return Returns the address of offset associated with this entry
	 */
	public long getAddr() {
		return addr;
	}
}
