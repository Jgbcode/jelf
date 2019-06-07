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
import net.fornwall.jelf.section.note.ElfNote;

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
		
		printNoteSections(file);
		printStringTables(file);
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
	
	public static void printNoteSections(ElfFile file) {
		List<ElfNoteSection> notes = file.getSectionHeaders().getSectionsOfType(ElfNoteSection.class);
		for(ElfNoteSection ns : notes) {
			Table t = new Table("Displaying notes found in: " + ns.getName());
			
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
			
			for(int i = 0; i < ns.getNoteCount(); i++) {
				ElfNote n = ns.getNote(i);
				
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
				
			t.printTable();
		}
	}
	
	public static void printStringTables(ElfFile file) {
		List<ElfStringTableSection> strtabs = file.getSectionHeaders().getSectionsOfType(ElfStringTableSection.class);
		
		for(ElfStringTableSection s : strtabs) {
			Table t = new Table("String table section \'" + s.getName() + "\' at offset 0x" + 
					Long.toHexString(s.getFileOffset()) + " contains " + s.getStringCount() + " entries");
			
			// Column names
			t.addCell("Offset");
			t.setColAlign(Align.RIGHT);
			
			t.addCell("Size");
			t.setColAlign(Align.RIGHT);
			
			t.addCell("String");
			t.setColAlign(Align.LEFT);
			
			int offset = 0;
			for(int i = 0; i < s.getStringCount();) {
				String str = s.getString(offset);
				if(str.isEmpty()) {
					offset++;
					continue;
				}
				i++;
				
				t.newRow();
				
				// Offset
				t.addCell("0x" + Integer.toHexString(offset));
				
				// Size
				t.addCell("0x" + Integer.toHexString(str.length()));
				
				// String
				t.addCell(str);
				
				offset += str.length();
			}
			
			t.printTable();
		}
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
