package net.fornwall.jelf.app;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfHeader;
import net.fornwall.jelf.app.Table.Align;
import net.fornwall.jelf.section.ElfSection;

public class Main {

	public static void main(String[] args) throws Exception {
		if(args.length == 0) {
			Scanner in = new Scanner(System.in);
			System.out.print("Enter a file path: ");
			args = new String[1];
			args[0] = in.nextLine();
			in.close();
		}
		else if (args.length != 1) {
			System.out.println("Usage: java ELFFileParser <elf file>");
			System.exit(0);
		}

		ElfFile file = ElfFile.fromFile(new File(args[0]));
		
		// Similar to readelf -a <elf_file>
		printHeader(file);
	}
	
	private static void printHeader(ElfFile file) {
		ElfHeader h = file.getHeader();
		
		Table t = new Table("ELF Header:");
		
		t.setAlign(Table.Align.LEFT);
		t.setAlign(Table.Align.LEFT);
		t.newRow();
		
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
		t.addCell(Long.toHexString(h.getEntryAddress()));
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
		ElfHeader h = file.getHeader();
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
		
		for(int i = 0; i < h.getSectionHeaderEntryCount(); i++) {
			t.newRow();
			
			ElfSection s = file.getSectionHeaders().getSectionByIndex(i);
			
			// Index
			t.addCell("[" + i + "]");
			
			// Name
			t.addCell(s.getName());
			
			// Size
			t.addCell("0x" + Long.toHexString(s.getSize()));
			
			// Type
			
		}
	}
}
