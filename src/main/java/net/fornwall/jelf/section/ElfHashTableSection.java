package net.fornwall.jelf.section;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.symbol.ElfSymbol;

public class ElfHashTableSection extends ElfSection {
	private int buckets[];
	private int chains[];
	
	protected ElfHashTableSection(ElfSection s) {
		super(s);
		
		ElfParser parser = s.getFile().getParser();
		long offset = s.getFileOffset();
		long length = s.getFileSize();
		
		parser.seek(offset);
		int num_buckets = parser.readInt();
		int num_chains = parser.readInt();

		buckets = new int[num_buckets];
		chains = new int[num_chains];
		
		// Read the bucket data.
		for (int i = 0; i < num_buckets; i++) {
			buckets[i] = parser.readInt();
		}

		// Read the chain data.
		for (int i = 0; i < num_chains; i++) {
			chains[i] = parser.readInt();
		}

		// Make sure that the amount of bytes we were supposed to read
		// was what we actually read.
		int actual = num_buckets * 4 + num_chains * 4 + 8;
		if (length != actual) {
			throw new ElfException("Error reading hash table (read " + actual + "bytes, expected to " + "read " + length + "bytes).");
		}
	}

	/**
	 * Gets the {@link ElfSymbol} with the specified name
	 */
	 public ElfSymbol getSymbol(String symbolName) {
		 if (symbolName == null) {
			 return null;
		 }
		 
		 // Make sure mod is positive
		 int index = (int)(((hash(symbolName) % buckets.length) + buckets.length) % buckets.length);
		 
		 ElfSymbolTableSection symtab = getSymbolTable();
		 
		 ElfSymbol sym = symtab.getSymbol(index);
		 while(index != 0 && !sym.getName().equals(symbolName)) {
			 index = chains[index];
			 sym = symtab.getSymbol(index);
		 }
		 
		 return sym;
	 }
	 
	 /**
	  * @return Returns the index of the associated {@link ElfSymbolTableSection}
	  */
	 public int getSymbolTableIndex() {
		 return super.getLinkIndex();
	 }
	 
	 /**
	  * @return Returns the {@link ElfSymbolTableSection} associated with this hash table
	  */
	 public ElfSymbolTableSection getSymbolTable() {
		 return (ElfSymbolTableSection)super.getLink(ElfSymbolTableSection.class);
	 }
	 
	 private long hash(String str) {
		 long hash = 0;
		 long g = 0;
		
		 for (int i = 0; i < str.length(); i++) {
			 hash = (hash << 4) + str.charAt(i);
			 if ((g = hash & 0xf0000000) != 0) {
				 hash ^= g >>> 24;
			 }
			 hash &= ~g;
		 }
		 
		 return hash;
	 }
}
