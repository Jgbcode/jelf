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
	 * See {@link #toString()} to get the formatted string directly
	 * 
	 * @return Returns a {@link Table} object that contains the formatted contents of this header.
	 */
	public Table getFormattedTable() {
		Table t = new Table("Program Headers");
		
		// Column names
		t.addCell("Type");
		t.setColAlign(Align.LEFT);
		
		t.addCell("Offset");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("VirtAddr");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("PhysAddr");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("FileSize");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("MemSize");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Flags");
		t.setColAlign(Align.LEFT);
		
		t.addCell("Align");
		t.setColAlign(Align.RIGHT);
		
		for(int i = 0; i < file.getProgramHeaders().size(); i++) {
			t.newRow();
			
			ElfSegment s = file.getProgramHeaders().getSegmentByIndex(i);
			
			// Type
			t.addCell(s.getType().name());
			
			// Offset
			t.addCell("0x" + Long.toHexString(s.getOffset()));
			
			// Virtual Address
			t.addCell("0x" + Long.toHexString(s.getVirtualAddress()));
			
			// Physical Address
			t.addCell("0x" + Long.toHexString(s.getPhysicalAddress()));
			
			// File Size
			t.addCell("0x" + Long.toHexString(s.getFileSize()));
			
			// MemSize
			t.addCell("0x" + Long.toHexString(s.getMemSize()));
			
			// Flags
			t.addCell(s.getFlags().name());
			
			// Align
			t.addCell("0x" + Long.toHexString(s.getAlignment()));
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		return this.getFormattedTable().toString();
	}
}
