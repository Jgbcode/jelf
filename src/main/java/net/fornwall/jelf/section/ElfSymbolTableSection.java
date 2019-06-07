package net.fornwall.jelf.section;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.Table;
import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.section.symbol.ElfSymbol;

public class ElfSymbolTableSection extends ElfSection {
	private ElfSymbol[] symbols;
	
	protected ElfSymbolTableSection(ElfSection s) {
		super(s);
		
		int num_entries = (int) (super.getFileSize() / super.getEntrySize());
		symbols = new ElfSymbol[num_entries];
		for (int i = 0; i < num_entries; i++) {
			long symbolOffset = super.getFileOffset() + (i * super.getEntrySize());
			symbols[i] = ElfSymbol.symbolFactory(ElfSymbolTableSection.super.getFile(), this, symbolOffset);
		}
	}
	
	/**
	 * @param index The index of the symbol to get
	 * @return Returns the symbol at the provided index
	 */
	public ElfSymbol getSymbol(int index) {
		if(index > symbols.length || index < 0) {
			throw new ElfException("Symbol index out of bounds. Number of symbols is " 
					+ symbols.length + " but " + index + " was provided");
		}
		return symbols[index];
	}
	
	/**
	 * @return Returns the number of symbols in this symbol table
	 */
	public int getSymbolCount() {
		return symbols.length;
	}
	
	/**
	 * @return Returns the index of the associated {@link ElfStringTableSection}
	 */
	public int getStringTableIndex() {
		return super.getLinkIndex();
	}
	
	/**
	 * @return Returns the string table that holds the names of the contained symbols
	 */
	public ElfStringTableSection getStringTable() {
		return (ElfStringTableSection)super.getLink(ElfStringTableSection.class);
	}
	
	/**
	 * @return Returns the index of the first non local symbol
	 */
	public int getFirstNonLocal() {
		return super.getInfo();
	}
	
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this header.
	 */
	public Table getFormattedTable() {
		Table t = new Table("Symbol table '" + getName() + "' contains " + getSymbolCount() + " entries:");
		
		// Column names
		t.addCell("Num:");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Value");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Size");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Type");
		t.setColAlign(Align.LEFT);
		
		t.addCell("Bind");
		t.setColAlign(Align.LEFT);
		
		t.addCell("Vis");
		t.setColAlign(Align.LEFT);
		
		t.addCell("Ndx");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Name");
		t.setColAlign(Align.LEFT);
		
		for(int i = 0; i < getSymbolCount(); i++) {
			t.newRow();
			
			ElfSymbol e = getSymbol(i);
			
			// Number
			t.addCell(i + ":");
			
			// Value
			t.addCell("0x" + Long.toHexString(e.getValue()));
			
			// Size
			t.addCell(Long.toString(e.getSize()));
			
			// Type
			t.addCell(e.getType().name());
			
			// Bind
			t.addCell(e.getBinding().name());
			
			// Visibility
			t.addCell(e.getOther().name());
			
			// Section Index
			t.addCell(e.getSectionHeaderIndex().name());
			
			// Name
			t.addCell(e.getName());
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
