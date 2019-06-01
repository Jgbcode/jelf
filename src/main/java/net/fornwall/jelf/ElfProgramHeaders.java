package net.fornwall.jelf;

public class ElfProgramHeaders {
	private ElfSegment[] segments;
	
	public ElfProgramHeaders(ElfFile file) {
		ElfHeader h = file.getHeader();
		
		segments = new ElfSegment[h.getProgramHeaderEntryCount()];
		for (int i = 0; i < segments.length; i++) {
			long programHeaderOffset = h.getProgramHeaderOffset() + (i * h.getProgramHeaderEntrySize());
			segments[i] = new ElfSegment(file, programHeaderOffset);
		}
	}
}
