package net.fornwall.jelf.section;

import java.util.ArrayList;
import java.util.List;

import net.fornwall.jelf.ElfException;
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
}
