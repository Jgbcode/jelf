package net.fornwall.jelf;

import net.fornwall.jelf.segment.ElfSegment;

public class ElfProgramHeaders {
	private ElfSegment[] segments;
	
	public ElfProgramHeaders(ElfFile file) {
		ElfHeader h = file.getHeader();
		
		segments = new ElfSegment[h.getProgramHeaderEntryCount()];
		for (int i = 0; i < segments.length; i++) {
			long programHeaderOffset = h.getProgramHeaderOffset() + (i * h.getProgramHeaderEntrySize());
			segments[i] = ElfSegment.segmentFactory(file, programHeaderOffset);
		}
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
}
