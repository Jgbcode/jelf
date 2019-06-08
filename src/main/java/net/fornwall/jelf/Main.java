package net.fornwall.jelf;

import java.io.File;

import net.fornwall.jelf.Table.Align;
import net.fornwall.jelf.section.ElfDynamicSection;
import net.fornwall.jelf.section.ElfNoteSection;
import net.fornwall.jelf.section.ElfRelocationSection;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfStringTableSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;

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
		
		// Print dynamic sections
		for(ElfDynamicSection s : file.getSectionHeaders().getSectionsOfType(ElfDynamicSection.class))
			System.out.println(s);
	}
	
	private static void printSectionMapping(ElfFile file) {
		Table t = new Table("Section to Segment mapping:");
		
		// Column names
		t.add("Segment");
		t.setColAlign(Align.RIGHT);
		
		t.add("Sections...");
		t.setColAlign(Align.LEFT);
		
		for(int i = 0; i < file.getProgramHeaders().size(); i++) {
			t.newRow();
			
			// Segment
			t.add(Integer.toString(i));
			
			// Sections
			String sections = "";
			for(ElfSection s : file.getProgramHeaders().getSegmentByIndex(i).getSections()) {
				if(!s.getName().isEmpty())
					sections += s.getName() + " ";
			}
			
			t.add(sections);
		}
		
		t.printTable();
	}
}
