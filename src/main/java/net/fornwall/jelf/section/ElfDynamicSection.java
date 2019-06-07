package net.fornwall.jelf.section;

import java.util.ArrayList;
import java.util.List;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.section.dynamic.ElfDynamicEntry;

public class ElfDynamicSection extends ElfSection {

	private List<ElfDynamicEntry> entries;
	
	protected ElfDynamicSection(ElfSection s) {
		super(s);
		
		this.entries = new ArrayList<ElfDynamicEntry>();
		long offset = super.getFileOffset();
		ElfDynamicEntry e = ElfDynamicEntry.dynamicEntryFactory(this, offset);
		while(e.getType().val != ElfDynamicEntry.Type.NULL) {
			entries.add(e);
			e = ElfDynamicEntry.dynamicEntryFactory(this, offset += super.getEntrySize());
		}
		
		// Add NULL element
		entries.add(e);
	}
	
	/**
	 * @return Returns the number of dynamic entries contained by this dynamic section
	 */
	public int getEntryCount() {
		return entries.size();
	}
	
	/**
	 * 
	 * @param index the index of the entry to fetch
	 * @return Returns the entry at the provided index
	 */
	public ElfDynamicEntry getEntry(int index) {
		try {
			return entries.get(index);
		}
		catch(IndexOutOfBoundsException e) {
			throw new ElfException("Dynamic entry index out of bounds: " + e.getMessage());
		}
	}
	
	/**
	 * @param type the type of entries to fetch
	 * @return Returns a list of all entries in the dynamic section which are of the provided type
	 */
	public List<ElfDynamicEntry> getEntriesOfType(ElfDynamicEntry.Type type) {
		List<ElfDynamicEntry> result = new ArrayList<ElfDynamicEntry>();
		for(ElfDynamicEntry e : entries) {
			if(e.getType().val == type.val)
				result.add(e);
		}
		return result;
	}
}
