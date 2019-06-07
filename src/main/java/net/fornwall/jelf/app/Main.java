package net.fornwall.jelf.app;

import java.io.File;
import java.util.List;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.app.Table.Align;
import net.fornwall.jelf.section.ElfDynamicSection;
import net.fornwall.jelf.section.ElfNoteSection;
import net.fornwall.jelf.section.ElfRelocationSection;
import net.fornwall.jelf.section.ElfSection;
import net.fornwall.jelf.section.ElfStringTableSection;
import net.fornwall.jelf.section.ElfSymbolTableSection;
import net.fornwall.jelf.section.dynamic.ElfDynamicEntry;
import net.fornwall.jelf.section.note.ElfNote;
import net.fornwall.jelf.section.relocation.ElfAddendRelocation;
import net.fornwall.jelf.section.relocation.ElfRelocation;
import net.fornwall.jelf.section.symbol.ElfSymbol;
import net.fornwall.jelf.segment.ElfSegment;

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

		ElfFile file = ElfFile.fromFile(new File(args[0]));

		// Similar to readelf -a <elf_file>
		printHeader(file);
		printSectionHeaders(file);
		printProgramHeaders(file);
		printSectionMapping(file);
		printSymbolTables(file);
		printRelocationSections(file);
		printNoteSections(file);
		printStringTables(file);
		printDynamicSections(file);
	}
	
	private static void printHeader(ElfFile file) {
		Table t = new Table("ELF Header:");
		
		t.setAlign(Table.Align.LEFT);
		t.setAlign(Table.Align.LEFT);
		t.newRow();
		
		ElfHeader h = file.getHeader();
		
		t.addCell("Class:");
		t.addCell(h.getBitClass().name());
		t.newRow();
		
		t.addCell("Data:");
		t.addCell(h.getDataFormat().name());
		t.newRow();
		
		t.addCell("Version:");
		t.addCell(h.getVersion().name());
		t.newRow();
		
		t.addCell("Type:");
		t.addCell(h.getFileType().name());
		t.newRow();
		
		t.addCell("Machine:");
		t.addCell(h.getMachine().name());
		t.newRow();
		
		t.addCell("Entry point address:");
		t.addCell("0x" + Long.toHexString(h.getEntryAddress()));
		t.newRow();
		
		t.addCell("Start of program headers:");
		t.addCell(h.getProgramHeaderOffset() + " (bytes into file)");
		t.newRow();
		
		t.addCell("Start of section headers:");
		t.addCell(h.getSectionHeaderOffset() + " (bytes into file)");
		t.newRow();
		
		t.addCell("Flags:");
		t.addCell(Integer.toHexString(h.getFlags()));
		t.newRow();
		
		t.addCell("Size of this header:");
		t.addCell(h.getSize() + " (bytes)");
		t.newRow();
		
		t.addCell("Size of program headers:");
		t.addCell(h.getProgramHeaderEntrySize() + " (bytes)");
		t.newRow();
		
		t.addCell("Number of program headers:");
		t.addCell(Short.toString(h.getProgramHeaderEntryCount()));
		t.newRow();
		
		t.addCell("Size of section headers:");
		t.addCell(h.getSectionHeaderEntrySize() + " (bytes)");
		t.newRow();
		
		t.addCell("Number of section headers:");
		t.addCell(Short.toString(h.getSectionHeaderEntryCount()));
		t.newRow();
		
		t.addCell("Section header string table index:");
		t.addCell(Short.toString(h.getSectionHeaderStringTableIndex()));
		
		t.printTable();
	}
	
	private static void printSectionHeaders(ElfFile file) {
		Table t = new Table("Section Headers:");
		t.newRow();
		
		// Column names
		t.addCell("[Nr]");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Name");
		t.setAlign(Align.LEFT);
		
		t.addCell("Size");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Type");
		t.setAlign(Align.LEFT);
		
		t.addCell("EntSize");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Address");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Flags");
		t.setAlign(Align.LEFT);
		
		t.addCell("Link");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Info");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Offset");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Align");
		t.setAlign(Align.RIGHT);
		
		for(int i = 0; i < file.getSectionHeaders().size(); i++) {
			t.newRow();
			
			ElfSection s = file.getSectionHeaders().getSectionByIndex(i);
			
			// Index
			t.addCell("[" + i + "]");
			
			// Name
			t.addCell(s.getName());
			
			// Size
			t.addCell("0x" + Long.toHexString(s.getFileSize()));
			
			// Type
			t.addCell(s.getType().name());
			
			// EntSize
			t.addCell("0x" + Long.toHexString(s.getEntrySize()));
			
			// Address
			t.addCell("0x" + Long.toHexString(s.getAddress()));
			
			// Flags
			t.addCell(s.getFlags().name());
			
			// Link
			t.addCell(Integer.toString(s.getLinkIndex()));
			
			// Info
			t.addCell(Integer.toString(s.getInfo()));
			
			// Offset
			t.addCell("0x" + Long.toHexString(s.getFileOffset()));
			
			// Align
			t.addCell(Long.toString(s.getAlignment()));
		}
		
		t.printTable();
		
		// Print flag key
		System.out.println("Key to Flags:");
		for(String s : ElfSection.Flag.getNameKey().split("\n"))
			System.out.println(" " + s);
	}
	
	private static void printProgramHeaders(ElfFile file) {
		Table t = new Table("Program Headers");
		t.newRow();
		
		// Column names
		t.addCell("Type");
		t.setAlign(Align.LEFT);
		
		t.addCell("Offset");
		t.setAlign(Align.RIGHT);
		
		t.addCell("VirtAddr");
		t.setAlign(Align.RIGHT);
		
		t.addCell("PhysAddr");
		t.setAlign(Align.RIGHT);
		
		t.addCell("FileSize");
		t.setAlign(Align.RIGHT);
		
		t.addCell("MemSize");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Flags");
		t.setAlign(Align.LEFT);
		
		t.addCell("Align");
		t.setAlign(Align.RIGHT);
		
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
		
		t.printTable();
	}
	
	public static void printSectionMapping(ElfFile file) {
		Table t = new Table("Section to Segment mapping:");
		t.newRow();
		
		// Column names
		t.addCell("Segment");
		t.setAlign(Align.RIGHT);
		
		t.addCell("Sections...");
		t.setAlign(Align.LEFT);
		
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
	
	public static void printSymbolTables(ElfFile file) {
		List<ElfSymbolTableSection> sym = file.getSectionHeaders().getSectionsOfType(ElfSymbolTableSection.class);
		for(ElfSymbolTableSection s : sym) {
			Table t = new Table("Symbol table '" + s.getName() + "' contains " + s.getSymbolCount() + " entries:");
			t.newRow();
			
			// Column names
			t.addCell("Num:");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Value");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Size");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Type");
			t.setAlign(Align.LEFT);
			
			t.addCell("Bind");
			t.setAlign(Align.LEFT);
			
			t.addCell("Vis");
			t.setAlign(Align.LEFT);
			
			t.addCell("Ndx");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Name");
			t.setAlign(Align.LEFT);
			
			for(int i = 0; i < s.getSymbolCount(); i++) {
				t.newRow();
				
				ElfSymbol e = s.getSymbol(i);
				
				// Number
				t.addCell(i + ":");
				
				// Value
				t.addCell("0x" + Long.toHexString(e.getValue()));
				
				// Size
				t.addCell(Long.toString(e.getSize()));
				
				// Type
				t.addCell(e.getType().name());
				
				// Bind
				t.addCell(e.getBinding().name());
				
				// Visibility
				t.addCell(e.getOther().name());
				
				// Section Index
				t.addCell(e.getSectionHeaderIndex().name());
				
				// Name
				t.addCell(e.getName());
			}
			
			t.printTable();
			System.out.println();
		}
	}
	
	public static void printRelocationSections(ElfFile file) {
		List<ElfRelocationSection> reloc = file.getSectionHeaders().getSectionsOfType(ElfRelocationSection.class);
		for(ElfRelocationSection r : reloc) {
			Table t = new Table("Relocation section '" + r.getName() + "' at offset 0x" + 
					Long.toHexString(r.getFileOffset()) + " contains " + r.getRelocationCount() + " entries:");
			t.newRow();
			
			// Column names
			t.addCell("Offset");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Info");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Type");
			t.setAlign(Align.LEFT);
			
			t.addCell("SymValue");
			t.setAlign(Align.RIGHT);
			
			t.addCell("SymName");
			t.setAlign(Align.LEFT);
			
			if(r.getType().val == ElfSection.Type.RELA) {
				t.addCell("Addend");
				t.setAlign(Align.RIGHT);
			}
			
			for(int i = 0; i < r.getRelocationCount(); i++) {
				t.newRow();
				
				ElfRelocation re = r.getRelocation(i);
				
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
			
			t.printTable();
			System.out.println();
		}
	}
	
	public static void printNoteSections(ElfFile file) {
		List<ElfNoteSection> notes = file.getSectionHeaders().getSectionsOfType(ElfNoteSection.class);
		for(ElfNoteSection ns : notes) {
			Table t = new Table("Displaying notes found in: " + ns.getName());
			t.newRow();
			
			// Column names
			t.addCell("Owner");
			t.setAlign(Align.LEFT);
			
			t.addCell("DataSize");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Description");
			t.setAlign(Align.LEFT);
			
			t.addCell("Note");
			t.setAlign(Align.LEFT);
			
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
			System.out.println();
		}
	}
	
	public static void printStringTables(ElfFile file) {
		List<ElfStringTableSection> strtabs = file.getSectionHeaders().getSectionsOfType(ElfStringTableSection.class);
		
		for(ElfStringTableSection s : strtabs) {
			Table t = new Table("String table section \'" + s.getName() + "\' at offset 0x" + 
					Long.toHexString(s.getFileOffset()) + " contains " + s.getStringCount() + " entries");
			t.newRow();
			
			// Column names
			t.addCell("Offset");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Size");
			t.setAlign(Align.RIGHT);
			
			t.addCell("String");
			t.setAlign(Align.LEFT);
			
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
			System.out.println();
		}
	}
	
	public static void printDynamicSections(ElfFile file) {
		List<ElfDynamicSection> dyns = file.getSectionHeaders().getSectionsOfType(ElfDynamicSection.class);
		
		for(ElfDynamicSection d : dyns) {
			Table t = new Table("Dynamic section at offset " + d.getFileOffset() + " contains " + 
					d.getEntryCount() + " entries:");
			t.newRow();
			
			// Column names
			t.addCell("Tag");
			t.setAlign(Align.RIGHT);
			
			t.addCell("Type");
			t.setAlign(Align.LEFT);
			
			t.addCell("Name / Value");
			t.setAlign(Align.LEFT);
			
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
			System.out.println();
		}
	}
}
