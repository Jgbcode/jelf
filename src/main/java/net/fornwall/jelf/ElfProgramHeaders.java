package net.fornwall.jelf;

import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.segment.ElfSegment;

public class ElfProgramHeaders {
	private ElfFile file;
	private ElfSegment[] segments;
	
	public ElfProgramHeaders(ElfFile file) {
		this.file = file;
		ElfHeader h = file.getHeader();
		
		segments = new ElfSegment[h.getProgramHeaderEntryCount()];
		for (int i = 0; i < segments.length; i++) {
			long programHeaderOffset = h.getProgramHeaderOffset() + (i * h.getProgramHeaderEntrySize());
			segments[i] = ElfSegment.segmentFactory(file, programHeaderOffset);
		}
	}
	
	/**
	 * @return Returns the file that contains these program headers
	 */
	public ElfFile getFile() {
		return this.file;
	}
	
	/**
	 * @return Returns the number of segments
	 */
	public int size() {
		return segments.length;
	}
	
	/**
	 * @param index the index of the segment to fetch
	 * @return Returns the requested segment
	 */
	public ElfSegment getSegmentByIndex(int index) {
		if(index < 0 || index >= segments.length)
			throw new ElfException("Segment index out of bounds: " + index);
		return segments[index];
	}
	
	/**
	 * @param type the type of segment to get
	 * @return Returns a the segment of the provided type
	 * @throws ElfException if there does not exist exactly one entry
	 * 	of the provided type
	 */
	public ElfSegment getUniqueSegmentOfType(int type) {
		return getUniqueSegmentOfType(new ElfSegment.Type(type));
	}
	
	/**
	 * @param type the type of segment to get
	 * @return Returns a the segment of the provided type
	 * @throws ElfException if there does not exist exactly one entry
	 * 	of the provided type
	 */
	public ElfSegment getUniqueSegmentOfType(ElfSegment.Type type) {
		ElfSegment unique = null;
		for(ElfSegment s : segments) {
			if(s.getType().equals(type)) {
				if(unique != null)
					throw new ElfException("Duplicatation of unique segment entry: " + type.name());
				unique = s;
			}
		}
		
		if(unique == null)
			throw new ElfException("Unique segment entry '" + type.name() + "' does not exist");
		return unique;
	}
	
	/**
	 * @param c the class of segment to get
	 * @return Returns a the segment of the provided type
	 * @throws ElfException if there does not exist exactly one entry
	 * 	of the provided type
	 */
	public <T extends ElfSegment> T getUniqueSegmentOfType(Class<T> c) {
		T unique = null;
		for(ElfSegment s : segments) {
			if(c.isInstance(s)) {
				if(unique != null)
					throw new ElfException("Duplicatation of unique segment entry: " + c.getSimpleName());
				unique = c.cast(s);
			}
		}
		
		if(unique == null)
			throw new ElfException("Unique segment entry '" + c.getSimpleName() + "' does not exist");
		return unique;
	}
	
	/**
	 * @param type the type of segment to get
	 * @param c the class of segment to get
	 * @return Returns a the segment of the provided type
	 * @throws ElfException if there does not exist exactly one entry
	 * 	of the provided type
	 */
	public <T extends ElfSegment> T getUniqueSegmentOfType(int type, Class<T> c) {
		return getUniqueSegmentOfType(new ElfSegment.Type(type), c);
	}
	
	/**
	 * @param type the type of segment to get
	 * @param c the class of segment to get
	 * @return Returns a the segment of the provided type
	 * @throws ElfException if there does not exist exactly one entry
	 * 	of the provided type
	 */
	public <T extends ElfSegment> T getUniqueSegmentOfType(ElfSegment.Type type, Class<T> c) {
		T unique = getUniqueSegmentOfType(c);
		if(unique.getType().equals(type))
			return unique;
		throw new ElfException("No segment entry of both type " + type.name() + " and " + c.getSimpleName());
	}
	
	/**
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this object.
	 */
	public Table getFormattedTable() {
		Table t = new Table("Program Headers");
		
		// Column names
		t.add("Type");
		t.setColAlign(Align.LEFT);
		
		t.add("Offset");
		t.setColAlign(Align.RIGHT);
		
		t.add("VirtAddr");
		t.setColAlign(Align.RIGHT);
		
		t.add("PhysAddr");
		t.setColAlign(Align.RIGHT);
		
		t.add("FileSize");
		t.setColAlign(Align.RIGHT);
		
		t.add("MemSize");
		t.setColAlign(Align.RIGHT);
		
		t.add("Flags");
		t.setColAlign(Align.LEFT);
		
		t.add("Align");
		t.setColAlign(Align.RIGHT);
		
		for(int i = 0; i < file.getProgramHeaders().size(); i++) {
			t.newRow();
			
			ElfSegment s = file.getProgramHeaders().getSegmentByIndex(i);
			
			// Type
			t.add(s.getType().name());
			
			// Offset
			t.add("0x" + Long.toHexString(s.getOffset()));
			
			// Virtual Address
			t.add("0x" + Long.toHexString(s.getVirtualAddress()));
			
			// Physical Address
			t.add("0x" + Long.toHexString(s.getPhysicalAddress()));
			
			// File Size
			t.add("0x" + Long.toHexString(s.getFileSize()));
			
			// MemSize
			t.add("0x" + Long.toHexString(s.getMemSize()));
			
			// Flags
			t.add(s.getFlags().name());
			
			// Align
			t.add("0x" + Long.toHexString(s.getAlignment()));
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
