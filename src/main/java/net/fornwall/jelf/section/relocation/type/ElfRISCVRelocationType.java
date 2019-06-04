package net.fornwall.jelf.section.relocation.type;

import net.fornwall.jelf.section.relocation.ElfRelocation;

public class ElfRISCVRelocationType extends ElfRelocation.Type {

	/** R_RISCV_NONE:  */
	public static final int RISCV_NONE = 0;
	/** R_RISCV_32:  */
	public static final int RISCV_32 = 1;
	/** R_RISCV_64:  */
	public static final int RISCV_64 = 2;
	/** R_RISCV_RELATIVE:  */
	public static final int RISCV_RELATIVE = 3;
	/** R_RISCV_COPY:  */
	public static final int RISCV_COPY = 4;
	/** R_RISCV_JUMP_SLOT:  */
	public static final int RISCV_JUMP_SLOT = 5;
	/** R_RISCV_TLS_DTPMOD32:  */
	public static final int RISCV_TLS_DTPMOD32 = 6;
	/** R_RISCV_TLS_DTPMOD64:  */
	public static final int RISCV_TLS_DTPMOD64 = 7;
	/** R_RISCV_TLS_DTPREL32:  */
	public static final int RISCV_TLS_DTPREL32 = 8;
	/** R_RISCV_TLS_DTPREL64:  */
	public static final int RISCV_TLS_DTPREL64 = 9;
	/** R_RISCV_TLS_TPREL32:  */
	public static final int RISCV_TLS_TPREL32 = 10;
	/** R_RISCV_TLS_TPREL64:  */
	public static final int RISCV_TLS_TPREL64 = 11;
	
	public ElfRISCVRelocationType(int val) {
		super(val);
	}
	
	@Override
	public String name() {
		switch(super.val) {
		case RISCV_NONE:
			return "RISCV_NONE";
		case RISCV_32:
			return "RISCV_32";
		case RISCV_64:
			return "RISCV_64";
		case RISCV_RELATIVE:
			return "RISCV_RELATIVE";
		case RISCV_COPY:
			return "RISCV_COPY";
		case RISCV_JUMP_SLOT:
			return "RISCV_JUMP_SLOT";
		case RISCV_TLS_DTPMOD32:
			return "RISCV_TLS_DTPMOD32";
		case RISCV_TLS_DTPMOD64:
			return "RISCV_TLS_DTPMOD64";
		case RISCV_TLS_DTPREL32:
			return "RISCV_TLS_DTPREL32";
		case RISCV_TLS_DTPREL64:
			return "RISCV_TLS_DTPREL64";
		case RISCV_TLS_TPREL32:
			return "RISCV_TLS_TPREL32";
		case RISCV_TLS_TPREL64:
			return "RISCV_TLS_TPREL64";
		default:
			return super.name();
		}
	}
}
