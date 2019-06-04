package net.fornwall.jelf.section;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.section.symbol.ElfSymbol;

public class ElfSymbolTableSection extends ElfSection {
	private ElfSymbol[] symbols;
	
	protected ElfSymbolTableSection(ElfSection s) {
		super(s);
		
		int num_entries = (int) (super.getFileSize() / super.getEntrySize());
		symbols = new ElfSymbol[num_entries];
		for (int i = 0; i < num_entries; i++) {
			final long symbolOffset = super.getFileOffset() + (i * super.getEntrySize());
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
}
