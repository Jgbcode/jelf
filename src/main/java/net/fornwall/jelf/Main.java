package net.fornwall.jelf;

import java.io.File;
import java.util.List;

import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.section.ElfDynamicSection;
import net.fornwall.jelf.section.ElfNoteSection;
import net.fornwall.jelf.section.ElfRelocationSection;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfStringTableSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;
import net.fornwall.jelf.section.dynamic.ElfDynamicEntry;

public class Main {

	public static void main(String[] args) throws Exception {
		if(args.length == 0) {
			args = new String[1];
			
			// Default testing file
			args[0] = "src/test/resources/hello_riscv.out";
		}
		else if (args.length != 1) {
			System.out.println("Usage: java ELFFileParser <elf file>");
			System.exit(0);
		}

		ElfFile file = new ElfFile(new File(args[0]));

		// Similar output to readelf -a <elf_file>
		
		// Print header data
		System.out.println(file.getHeader());
		
		// Print section data
		System.out.println(file.getSectionHeaders());
		
		// Print segment data
		System.out.println(file.getProgramHeaders());
		
		printSectionMapping(file);
		
		// Print symbol table sections
		for(ElfSymbolTableSection s : file.getSectionHeaders().getSectionsOfType(ElfSymbolTableSection.class))
			System.out.println(s);
		
		// Print relocation sections
		for(ElfRelocationSection s : file.getSectionHeaders().getSectionsOfType(ElfRelocationSection.class))
			System.out.println(s);
		
		// Print note sections
		for(ElfNoteSection s : file.getSectionHeaders().getSectionsOfType(ElfNoteSection.class))
			System.out.println(s);
		
		// Print string table sections
		for(ElfStringTableSection s : file.getSectionHeaders().getSectionsOfType(ElfStringTableSection.class))
			System.out.println(s);
		
		
		printDynamicSections(file);
	}
	
	public static void printSectionMapping(ElfFile file) {
		Table t = new Table("Section to Segment mapping:");
		
		// Column names
		t.addCell("Segment");
		t.setColAlign(Align.RIGHT);
		
		t.addCell("Sections...");
		t.setColAlign(Align.LEFT);
		
		for(int i = 0; i < file.getProgramHeaders().size(); i++) {
			t.newRow();
			
			// Segment
			t.addCell(Integer.toString(i));
			
			// Sections
			String sections = "";
			for(ElfSection s : file.getProgramHeaders().getSegmentByIndex(i).getSections()) {
				if(!s.getName().isEmpty())
					sections += s.getName() + " ";
			}
			
			t.addCell(sections);
		}
		
		t.printTable();
	}
	
	public static void printDynamicSections(ElfFile file) {
		List<ElfDynamicSection> dyns = file.getSectionHeaders().getSectionsOfType(ElfDynamicSection.class);
		
		for(ElfDynamicSection d : dyns) {
			Table t = new Table("Dynamic section at offset " + d.getFileOffset() + " contains " + 
					d.getEntryCount() + " entries:");
			
			// Column names
			t.addCell("Tag");
			t.setColAlign(Align.RIGHT);
			
			t.addCell("Type");
			t.setColAlign(Align.LEFT);
			
			t.addCell("Name / Value");
			t.setColAlign(Align.LEFT);
			
			for(int i = 0; i < d.getEntryCount(); i++) {
				t.newRow();
				
				ElfDynamicEntry e = d.getEntry(i);
				
				// Tag
				t.addCell("0x" + Long.toHexString(e.getType().val));
				
				// Type
				t.addCell("(" + e.getType().name() + ")");
				
				t.addCell("0x" + e.getAddr());
			}
			
			t.printTable();
		}
	}
}
