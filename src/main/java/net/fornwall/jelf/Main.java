package net.fornwall.jelf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
	}
	
	private static void printHeader(ElfFile file) {
		Table table = new Table("ELF Header:");
		
		table.addCell("Class:");
		table.addCell();
	}
}
