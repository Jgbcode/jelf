package net.fornwall.jelf.section;

import java.util.ArrayList;
import java.util.List;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.Table;
import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.section.note.ElfNote;

/**
 *	A collection of notes from a specific section
 */
public class ElfNoteSection extends ElfSection {
	private List<ElfNote> notes;
    
    protected ElfNoteSection(ElfSection s) {
    	super(s);
    	
    	notes = new ArrayList<ElfNote>();
    	
    	long offset = super.getFileOffset();
    	while(offset - super.getFileOffset() < super.getFileSize()) {
    		ElfNote n = ElfNote.noteFactory(this, offset);
    		notes.add(n);
    		offset += n.getFileSize();
    	}
    }
    
    /**
     * @param index the index of the note to get
     * @return Returns the note at the requested index
     */
    public ElfNote getNote(int index) {
    	try {
    		return notes.get(index);
    	}
    	catch(IndexOutOfBoundsException e) {
    		throw new ElfException("Note index out of bounds: " + e.getMessage());
    	}
    }
    
    /**
     * @return Returns the number of notes in this section
     */
    public int getNoteCount() {
    	return notes.size();
    }
    
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this header.
	 */
	public Table getFormattedTable() {
		Table t = new Table("Displaying notes found in: " + getName());
		
		// Column names
		t.addCell("Owner");
		t.setColAlign(Align.LEFT);
		
		t.addCell("DataSize");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Description");
		t.setColAlign(Align.LEFT);
		
		t.addCell("Note");
		t.setColAlign(Align.LEFT);
		
		t.newRow();
		
		for(int i = 0; i < getNoteCount(); i++) {
			ElfNote n = getNote(i);
			
			// Owner
			t.addCell(n.getNoteName());
			
			// Data size
			t.addCell("0x" + Integer.toHexString(n.getDescSize()));
			
			// Description
			t.addCell(Integer.toString(n.getNoteType()));
			
			// Note
			String str = "";
			for(int j = 0; j < n.getDescSize(); j++) {
				str = Integer.toHexString(n.getDescByte(i) & 0xf) + str;
				str = Integer.toHexString(n.getDescByte(i) >> 4) + str;
			}
			t.addCell(str);
			
			// Most notes don't seem to contain string data
			// t.addCell(n.getDescString());
		}
			
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
