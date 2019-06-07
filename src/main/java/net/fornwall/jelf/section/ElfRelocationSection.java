package net.fornwall.jelf.section;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.Table;
import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.section.relocation.ElfAddendRelocation;
import net.fornwall.jelf.section.relocation.ElfRelocation;

public class ElfRelocationSection extends ElfSection {
	
	private ElfRelocation[] relocations;
	
	protected ElfRelocationSection(ElfSection s) {
		super(s);
		
		int size = (int) (super.getFileSize() / super.getEntrySize());
		relocations = new ElfRelocation[size];
		
		for(int i = 0; i < size; i++) {
			long offset = super.getFileOffset() + (i * super.getEntrySize());
			relocations[i] = ElfRelocation.relocationFactory(s.getFile(), this, offset);
		}
	}
	
	/**
	 * @return Returns the number of relocations in this relocation section
	 */
	public int getRelocationCount() {
		return relocations.length;
	}
	
	/**
	 * @param index the index of the relocation in the section
	 * @return Returns the relocation at the provied index
	 */
	public ElfRelocation getRelocation(int index) {
		if(index >= 0 && index < relocations.length)
			return relocations[index];
		throw new ElfException("Relocation index out of bounds");
	}
	
	/**
	 * @return Returns the index of the {@link ElfSymbolTableSection} associated with this
	 * 	relocation section
	 */
	public int getSymbolTableIndex() {
		return super.getLinkIndex();
	}
	
	/**
	 * @return Returns the {@link ElfSymbolTableSection} associated with this
	 * 	relocation section
	 */
	public ElfSymbolTableSection getSymbolTable() {
		return (ElfSymbolTableSection)super.getLink(ElfSymbolTableSection.class);
	}
	
	/**
	 * @return Returns the section header index of the {@link ElfSection} to which the 
	 * 	relocation applies
	 */
	public int getSectionIndex() {
		return super.getInfo();
	}
	
	/**
	 * @return Returns the {@link ElfSection} to which the relocation applies
	 */
	public ElfSection getSection() {
		return super.getFile().getSectionHeaders().getSectionByIndex(super.getInfo());
	}
	
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this section.
	 */
	public Table getFormattedTable() {
		Table t = new Table("Relocation section '" + getName() + "' at offset 0x" + 
				Long.toHexString(getFileOffset()) + " contains " + getRelocationCount() + " entries:");
		
		// Column names
		t.addCell("Offset");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Info");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Type");
		t.setColAlign(Align.LEFT);
		
		t.addCell("SymValue");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("SymName");
		t.setColAlign(Align.LEFT);
		
		if(getType().val == ElfSection.Type.RELA) {
			t.addCell("Addend");
			t.setColAlign(Align.RIGHT);
		}
		
		for(int i = 0; i < getRelocationCount(); i++) {
			t.newRow();
			
			ElfRelocation re = getRelocation(i);
			
			// Offset
			t.addCell("0x" + Long.toHexString(re.getOffset()));
			
			// Info
			t.addCell("0x" + Long.toHexString(re.getInfo()));
			
			// Type
			t.addCell(re.getType().name());
			
			// Symbol value
			t.addCell(Long.toHexString(re.getSymbol().getValue()));
			
			// Symbol name
			t.addCell(re.getSymbol().getName());
			
			if(re instanceof ElfAddendRelocation) {
				t.addCell("0x" + Long.toHexString(((ElfAddendRelocation)re).getAddend()));
			}
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
