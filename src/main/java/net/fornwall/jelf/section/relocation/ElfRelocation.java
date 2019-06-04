package net.fornwall.jelf.section.relocation;

import java.util.HashMap;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.ElfRelocationSection;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;

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
		private final HashMap<Integer, String> mapS;
		
		public final int val;
		
		public Type() {
			this.val = 0;
			
			mapS = new HashMap<Integer, String>();
		}
		
		protected Type(int val, Integer[] vals, String[] names) {
			this.val = val;
			
			mapS = new HashMap<Integer, String>();
			for(int i = 0; i < vals.length; i++) {
				mapS.put(vals[i], names[i]);
			}
		}
		
		public Type(int val, final Type t) {
			this.val = val;
			
			mapS = t.mapS;
		}
		
		public String name() {
			String s;
			if((s = mapS.get(val)) == null)
				return "?";
			return s;
		}
	}
	
	private ElfFile file;
	private ElfRelocationSection table;
	
	private long offset;
	private long info;
	
	private int sym_ndx;
	private Type type;
	
	protected ElfRelocation(ElfFile file, ElfRelocationSection table, long offset, Type type) {
		this.file = file;
		this.table = table;
		
		ElfParser p = file.getParser();
		offset = p.readIntOrLong();
		info = p.readIntOrLong();
		
		// Type section and type
		if(file.getHeader().getBitClass() == ElfHeader.BitClass.ELFCLASS32) {
			this.sym_ndx = (int)(info >> 8);
			try {
				this.type = new Type((int)(info & 0xff), type);
			} catch (Exception e) {
				throw new ElfException(e.getMessage());
			}
		}
		else {
			this.sym_ndx = (int)(info >> 32);
			try {
				this.type = new Type((int)info, type);
			} catch (Exception e) {
				throw new ElfException(e.getMessage());
			}
		}
	}
	
	public static ElfRelocation relocationFactory(ElfFile file, ElfRelocationSection table, long offset) {
		switch(file.getHeader().getMachine()) {
			default:
				return new ElfRelocation(file, table, offset, new Type());
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
	 * @return Returns the index of the symbol this relocation is associated with
	 */
	public int getSymbolIndex() {
		return sym_ndx;
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
	 * 	{@link ElfRelocationSection#getSymbolTableIndex() getSymbolTableIndex()}
	 * 
	 * @return Returns the index of the symbol table section that contains the
	 * 	symbol associated with this relocation
	 */
	public int getSymbolTableIndex() {
		return table.getSymbolTableIndex();
	}
	
	/**
	 * This method is short for {@link ElfRelocation#getRelocationTable() getRelocationTable()}.
	 * 	{@link ElfRelocationSection#getSectionIndex() getSectionIndex()}
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
