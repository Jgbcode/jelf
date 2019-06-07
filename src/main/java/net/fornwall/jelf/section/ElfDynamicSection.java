package net.fornwall.jelf.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.Table;
import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.section.dynamic.ElfDynamicEntry;

public class ElfDynamicSection extends ElfSection {

	// List of entries
	private List<ElfDynamicEntry> entries;
	
	// Map to entries of certain types
	private Map<Integer, List<ElfDynamicEntry>> map;
	
	protected ElfDynamicSection(ElfSection s) {
		super(s);
		
		// List of entries
		this.entries = new ArrayList<ElfDynamicEntry>();
		
		// Map of entry type to a list of entries of that type
		this.map = new HashMap<Integer, List<ElfDynamicEntry>>();
		
		long offset = super.getFileOffset();
		ElfDynamicEntry e = ElfDynamicEntry.dynamicEntryFactory(this, offset);
		while(e.getType().val != ElfDynamicEntry.Type.NULL) {
			entries.add(e);
			
			if(!map.containsKey(e.getType().val))
				map.put(e.getType().val, new ArrayList<ElfDynamicEntry>());
			map.get(e.getType().val).add(e);
			
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
		if(!map.containsKey(type.val))
			return new ArrayList<ElfDynamicEntry>();
		
		List<ElfDynamicEntry> result = new ArrayList<ElfDynamicEntry>();
		for(ElfDynamicEntry e : map.get(type.val))
			result.add(e);
		return result;
	}
	
	/**
	 * @param c the class of entries to fetch
	 * @return Returns a list of all entries in the dynamic section which are of the provided class
	 */
	public <T extends ElfDynamicEntry> List<T> getEntriesOfType(Class<T> c) {
		List<T> result = new ArrayList<T>();
		for(ElfDynamicEntry e : entries) {
			if(c.isInstance(e))
				result.add(c.cast(e));
		}
		return result;
	}
	
	/**
	 * @param type the type of entries to fetch
	 * @param c the class of entries to fetch
	 * @return Returns a list of all entries in the dynamic section which are of both the
	 * 	provided type and class
	 */
	public <T extends ElfDynamicEntry> List<T> getEntriesOfType(ElfDynamicEntry.Type type, Class<T> c) {
		List<T> result = new ArrayList<T>();
		for(ElfDynamicEntry e : this.getEntriesOfType(type)) {
			if(c.isInstance(e))
				result.add(c.cast(e));
		}
		return result;
	}
	
	public ElfHashTableSection getHashTable() {
		if(!map.containsKey(ElfDynamicEntry.Type.HASH))
			throw new ElfException("No dynamic hash table found");
		
		
	}
	
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this section.
	 */
	public Table getFormattedTable() {
		Table t = new Table("Dynamic section at offset " + getFileOffset() + " contains " + 
				getEntryCount() + " entries:");
		
		// Column names
		t.addCell("Tag");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Type");
		t.setColAlign(Align.LEFT);
		
		t.addCell("Name / Value");
		t.setColAlign(Align.LEFT);
		
		for(int i = 0; i < getEntryCount(); i++) {
			t.newRow();
			
			ElfDynamicEntry e = getEntry(i);
			
			// Tag
			t.addCell("0x" + Long.toHexString(e.getType().val));
			
			// Type
			t.addCell("(" + e.getType().name() + ")");
			
			// Value
			t.addCell(e.toString());
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
