package net.fornwall.jelf.section.relocation.type;

import net.fornwall.jelf.section.relocation.ElfRelocation;

public class ElfX64RelocationType extends ElfRelocation.Type {
	/** R_X86_64_NONE: No reloc */
	public static final int X86_64_NONE = 0;
	/** R_X86_64_64: Direct 64 bit */
	public static final int X86_64_64 = 1;
	/** R_X86_64_PC32: PC relative 32 bit signed */
	public static final int X86_64_PC32 = 2;
	/** R_X86_64_GOT32: 32 bit GOT entry */
	public static final int X86_64_GOT32 = 3;
	/** R_X86_64_PLT32: 32 bit PLT address */
	public static final int X86_64_PLT32 = 4;
	/** R_X86_64_COPY: Copy symbol at runtime */
	public static final int X86_64_COPY = 5;
	/** R_X86_64_GLOB_DAT: Create GOT entry */
	public static final int X86_64_GLOB_DAT = 6;
	/** R_X86_64_JUMP_SLOT: Create PLT entry */
	public static final int X86_64_JUMP_SLOT = 7;
	/** R_X86_64_RELATIVE: Adjust by program base */
	public static final int X86_64_RELATIVE = 8;
	/** R_X86_64_GOTPCREL: 32 bit signed PC relative offset to GOT */
	public static final int X86_64_GOTPCREL = 9;
	/** R_X86_64_32: Direct 32 bit zero extended */
	public static final int X86_64_32 = 10;
	/** R_X86_64_32S: Direct 32 bit sign extended */
	public static final int X86_64_32S = 11;
	/** R_X86_64_16: Direct 16 bit zero extended */
	public static final int X86_64_16 = 12;
	/** R_X86_64_PC16: 16 bit sign extended pc relative */
	public static final int X86_64_PC16 = 13;
	/** R_X86_64_8: Direct 8 bit sign extended */
	public static final int X86_64_8 = 14;
	/** R_X86_64_PC8: 8 bit sign extended pc relative */
	public static final int X86_64_PC8 = 15;
	/** R_X86_64_DTPMOD64: ID of module containing symbol */
	public static final int X86_64_DTPMOD64 = 16;
	/** R_X86_64_DTPOFF64: Offset in module's TLS block */
	public static final int X86_64_DTPOFF64 = 17;
	/** R_X86_64_TPOFF64: Offset in initial TLS block */
	public static final int X86_64_TPOFF64 = 18;
	/** R_X86_64_TLSGD: 32 bit signed PC relative offset to two GOT entries for GD symbol */
	public static final int X86_64_TLSGD = 19;
	/** R_X86_64_TLSLD: 32 bit signed PC relative offset to two GOT entries for LD symbol */
	public static final int X86_64_TLSLD = 20;
	/** R_X86_64_DTPOFF32: Offset in TLS block */
	public static final int X86_64_DTPOFF32 = 21;
	/** R_X86_64_GOTTPOFF: 32 bit signed PC relative offset to GOT entry for IE symbol */
	public static final int X86_64_GOTTPOFF = 22;
	/** R_X86_64_TPOFF32: Offset in initial TLS block */
	public static final int X86_64_TPOFF32 = 23;
	/** R_X86_64_PC64: PC relative 64 bit */
	public static final int X86_64_PC64 = 24;
	/** R_X86_64_GOTOFF64: 64 bit offset to GOT */
	public static final int X86_64_GOTOFF64 = 25;
	/** R_X86_64_GOTPC32: 32 bit signed pc relative offset to GOT */
	public static final int X86_64_GOTPC32 = 26;
	/** R_X86_64_GOT64: 64-bit GOT entry offset */
	public static final int X86_64_GOT64 = 27;
	/** R_X86_64_GOTPCREL64: 64-bit PC relative offset to GOT entry */
	public static final int X86_64_GOTPCREL64 = 28;
	/** R_X86_64_GOTPC64: 64-bit PC relative offset to GOT */
	public static final int X86_64_GOTPC64 = 29;
	/** R_X86_64_GOTPLT64: like GOT64, says PLT entry needed */
	public static final int X86_64_GOTPLT64 = 30;
	/** R_X86_64_PLTOFF64: 64-bit GOT relative offset to PLT entry */
	public static final int X86_64_PLTOFF64 = 31;
	/** R_X86_64_SIZE32: Size of symbol plus 32-bit addend */
	public static final int X86_64_SIZE32 = 32;
	/** R_X86_64_SIZE64: Size of symbol plus 64-bit addend */
	public static final int X86_64_SIZE64 = 33;
	/** R_X86_64_GOTPC32_TLSDESC: GOT offset for TLS descriptor. */
	public static final int X86_64_GOTPC32_TLSDESC = 34;
	/** R_X86_64_TLSDESC_CALL: Marker for call through TLS descriptor. */
	public static final int X86_64_TLSDESC_CALL = 35;
	/** R_X86_64_TLSDESC: TLS descriptor. */
	public static final int X86_64_TLSDESC = 36;
	/** R_X86_64_IRELATIVE: Adjust indirectly by program base */
	public static final int X86_64_IRELATIVE = 37;
	/** R_X86_64_RELATIVE64: 64-bit adjust by program base */
	public static final int X86_64_RELATIVE64 = 38;
	/** R_X86_64_GOTPCRELX: Load from 32 bit signed pc relative offset to GOT entry without REX prefix, relaxable. */
	public static final int X86_64_GOTPCRELX = 41;
	/** R_X86_64_REX_GOTPCRELX: Load from 32 bit signed pc relative offset to GOT entry with REX prefix, relaxable. */
	public static final int X86_64_REX_GOTPCRELX = 42;
	/** R_X86_64_NUM:  */
	public static final int X86_64_NUM = 43;
	
	public ElfX64RelocationType(int val) {
		super(val);
	}

	@Override
	public String name() {
		switch(val) {
		case X86_64_NONE:
			return "X86_64_NONE";
		case X86_64_64:
			return "X86_64_64";
		case X86_64_PC32:
			return "X86_64_PC32";
		case X86_64_GOT32:
			return "X86_64_GOT32";
		case X86_64_PLT32:
			return "X86_64_PLT32";
		case X86_64_COPY:
			return "X86_64_COPY";
		case X86_64_GLOB_DAT:
			return "X86_64_GLOB_DAT";
		case X86_64_JUMP_SLOT:
			return "X86_64_JUMP_SLOT";
		case X86_64_RELATIVE:
			return "X86_64_RELATIVE";
		case X86_64_GOTPCREL:
			return "X86_64_GOTPCREL";
		case X86_64_32:
			return "X86_64_32";
		case X86_64_32S:
			return "X86_64_32S";
		case X86_64_16:
			return "X86_64_16";
		case X86_64_PC16:
			return "X86_64_PC16";
		case X86_64_8:
			return "X86_64_8";
		case X86_64_PC8:
			return "X86_64_PC8";
		case X86_64_DTPMOD64:
			return "X86_64_DTPMOD64";
		case X86_64_DTPOFF64:
			return "X86_64_DTPOFF64";
		case X86_64_TPOFF64:
			return "X86_64_TPOFF64";
		case X86_64_TLSGD:
			return "X86_64_TLSGD";
		case X86_64_TLSLD:
			return "X86_64_TLSLD";
		case X86_64_DTPOFF32:
			return "X86_64_DTPOFF32";
		case X86_64_GOTTPOFF:
			return "X86_64_GOTTPOFF";
		case X86_64_TPOFF32:
			return "X86_64_TPOFF32";
		case X86_64_PC64:
			return "X86_64_PC64";
		case X86_64_GOTOFF64:
			return "X86_64_GOTOFF64";
		case X86_64_GOTPC32:
			return "X86_64_GOTPC32";
		case X86_64_GOT64:
			return "X86_64_GOT64";
		case X86_64_GOTPCREL64:
			return "X86_64_GOTPCREL64";
		case X86_64_GOTPC64:
			return "X86_64_GOTPC64";
		case X86_64_GOTPLT64:
			return "X86_64_GOTPLT64";
		case X86_64_PLTOFF64:
			return "X86_64_PLTOFF64";
		case X86_64_SIZE32:
			return "X86_64_SIZE32";
		case X86_64_SIZE64:
			return "X86_64_SIZE64";
		case X86_64_GOTPC32_TLSDESC:
			return "X86_64_GOTPC32_TLSDESC";
		case X86_64_TLSDESC_CALL:
			return "X86_64_TLSDESC_CALL";
		case X86_64_TLSDESC:
			return "X86_64_TLSDESC";
		case X86_64_IRELATIVE:
			return "X86_64_IRELATIVE";
		case X86_64_RELATIVE64:
			return "X86_64_RELATIVE64";
		case X86_64_GOTPCRELX:
			return "X86_64_GOTPCRELX";
		case X86_64_REX_GOTPCRELX:
			return "X86_64_REX_GOTPCRELX";
		case X86_64_NUM:
			return "X86_64_NUM";
		default:
			return super.name();
		}
	}
}
