package net.fornwall.jelf;

import java.io.File;
import java.util.Scanner;

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
		ElfHeader h = file.header;
		
		Table t = new Table("ELF Header:");
		
		t.setAlign(Table.Align.LEFT);
		t.setAlign(Table.Align.LEFT);
		t.newRow();
		
		t.addCell("Class:");
		t.addCell(h.ei_class.name());
		t.newRow();
		
		t.addCell("Data:");
		t.addCell(h.ei_data.name());
		t.newRow();
		
		t.addCell("Version:");
		t.addCell(h.ei_version.name());
		t.newRow();
		
		t.addCell("Type:");
		t.addCell(h.e_type.name());
		t.newRow();
		
		t.addCell("Machine:");
		t.addCell(h.e_machine.name());
		t.newRow();
		
		t.addCell("Entry point address:");
		t.addCell(Long.toHexString(h.e_entry));
		t.newRow();
		
		t.addCell("Start of program headers:");
		t.addCell(h.e_phoff + " (bytes into file)");
		t.newRow();
		
		t.addCell("Start of section headers:");
		t.addCell(h.e_shoff + " (bytes into file)");
		t.newRow();
		
		t.addCell("Flags:");
		t.addCell(Integer.toHexString(h.e_flags));
		t.newRow();
		
		t.addCell("Size of this header:");
		t.addCell(h.e_ehsize + " (bytes)");
		t.newRow();
		
		t.addCell("Size of program headers:");
		t.addCell(h.e_phentsize + " (bytes)");
		t.newRow();
		
		t.addCell("Number of program headers:");
		t.addCell(Short.toString(h.e_phnum));
		t.newRow();
		
		t.addCell("Size of section headers:");
		t.addCell(h.e_shentsize + " (bytes)");
		t.newRow();
		
		t.addCell("Number of section headers:");
		t.addCell(Short.toString(h.e_shnum));
		t.newRow();
		
		t.addCell("Section header string table index:");
		t.addCell(Short.toString(h.e_shstrndx));
		
		t.printTable();
	}
}
