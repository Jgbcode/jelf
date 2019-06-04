package net.fornwall.jelf.section.relocation;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.ElfRelocationSection;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;
import net.fornwall.jelf.section.relocation.type.ElfRISCVRelocationType;
import net.fornwall.jelf.section.symbol.ElfSymbol;

/**
 *	Defines a relocation which determines how to adjust a specific
 *	references.
 */
public class ElfRelocation {
	
	/**
	 *	Default type, must be extended by ElfRelocation subclasses to
	 *	provided processor specific functionality
	 */
	public static class Type {
		public final int val;
		
		public Type(int val) {
			this.val = val;
		}
		
		// Must be overwritten by processor implementation defining subclasses
		public String name() {
			return "UNKNOWN";
		}
	}
	
	private final ElfFile file;
	private final ElfRelocationSection table;
	
	private final long offset;
	private final long info;
	
	private int sym_ndx;
	private Type type;
	
	protected ElfRelocation(ElfFile file, ElfRelocationSection table, long offset, Class<? extends Type> c) {
		this.file = file;
		this.table = table;
		
		ElfParser p = file.getParser();
		p.seek(offset);
		
		this.offset = p.readIntOrLong();
		info = p.readIntOrLong();
		
		// Type section and type
		if(file.getHeader().getBitClass() == ElfHeader.BitClass.ELFCLASS32) {
			this.sym_ndx = (int)(info >> 8);
			try {
				this.type = c.getDeclaredConstructor(Integer.TYPE).newInstance((int)(info & 0xff));
			} catch (Exception e) {
				throw new ElfException(e.getMessage());
			}
		}
		else {
			this.sym_ndx = (int)(info >> 32);
			try {
				this.type = c.getDeclaredConstructor(Integer.TYPE).newInstance((int)info);
			} catch (Exception e) {
				throw new ElfException(e.getMessage());
			}
		}
	}
	
	public static ElfRelocation relocationFactory(ElfFile file, ElfRelocationSection table, long offset) {
		Class<? extends Type> type;
		
		// Register type
		switch(file.getHeader().getMachine()) {
		case RISCV:
			type = ElfRISCVRelocationType.class;
			break;
		default:
			type = Type.class;
		}
		
		if(table.getType().val == ElfSection.Type.REL) {
			return new ElfRelocation(file, table, offset, type);
		}
		else if(table.getType().val == ElfSection.Type.RELA) {
			return new ElfAddendRelocation(file, table, offset, type);
		}
		else {
			throw new ElfException("Unknown relocation type: " + table.getType().name());
		}
	}

	/**
	 * @return Returns the file this relocation is associated with
	 */
	public ElfFile getFile() {
		return file;
	}

	/**
	 * @return Returns the relocation section which contains this relocation
	 */
	public ElfRelocationSection getRelocationTable() {
		return table;
	}

	/**
	 * This member gives the location at which to apply the relocation action. For a relocatable
	 * file, the value is the byte offset from the beginning of the section to the storage unit affected
	 * by the relocation. For an executable file or a shared object, the value is the virtual address of
	 * the storage unit affected by the relocation
	 * 
	 * @return Returns the offset at which to apply the relocation
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * @return Returns the info associated with this relocation. The info is
	 * 	not parsed. See {@link #getSymbolTableIndex} and {@link #getType} for parsed
	 * 	versions of the info.
	 */
	public long getInfo() {
		return info;
	}

	/**
	 * See {@link #getSymbol()} to get the symbol directly
	 * 
	 * @return Returns the index of the symbol this relocation is associated with
	 */
	public int getSymbolIndex() {
		return sym_ndx;
	}

	/**
	 * @return Returns the symbol this relocation is associated with
	 */
	public ElfSymbol getSymbol() {
		return getSymbolTable().getSymbol(sym_ndx);
	}
	
	/** 
	 * @return Returns the type of this relocation. This type may vary depending
	 * 	on the target processor.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * This method is short for {@link ElfRelocation#getRelocationTable() getRelocationTable()}.
	 * 	{@link ElfRelocationSection#getSymbolTableIndex() getSymbolTableIndex()}.
	 * 
	 * See {@link #getSymbolTable()} to get the symbol table directly
	 * 
	 * @return Returns the index of the symbol table section that contains the
	 * 	symbol associated with this relocation
	 */
	public int getSymbolTableIndex() {
		return table.getSymbolTableIndex();
	}
	
	/**
	 * This method is short for {@link ElfRelocation#getRelocationTable() getRelocationTable()}.
	 * 	{@link ElfRelocationSection#getSectionIndex() getSectionIndex()}.
	 * 
	 * See {@link #getSection()} to get the section directly
	 * 
	 * @return Returns the section header index of the {@link ElfSection} to which the 
	 * 	relocation applies
	 */
	public int getSectionIndex() {
		return table.getSectionIndex();
	}
	
	/**
	 * This method is short for {@link ElfRelocation#getRelocationTable() getRelocationTable()}.
	 * 	{@link ElfRelocationSection#getSymbolTable() getSymbolTable()}
	 * 
	 * @return Returns the section header index of the {@link ElfSection} to which the 
	 * 	relocation applies
	 */
	public ElfSymbolTableSection getSymbolTable() {
		return table.getSymbolTable();
	}
	
	/**
	 * This method is short for {@link ElfRelocation#getRelocationTable() getRelocationTable()}.
	 * 	{@link ElfRelocationSection#getSection() getSection()}
	 * 
	 * @return Returns the {@link ElfSection} to which the relocation applies
	 */
	public ElfSection getSection() {
		return table.getSection();
	}
}
