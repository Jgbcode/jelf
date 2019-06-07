package net.fornwall.jelf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.fornwall.jelf.section.ElfDynamicSection;
import net.fornwall.jelf.section.dynamic.ElfNeededDynamicEntry;

public class BasicTest {
	private static void assertSectionNames(ElfFile file, String... expectedSectionNames) throws IOException {
		for (int i = 0; i < expectedSectionNames.length; i++) {
			String expected = expectedSectionNames[i];
			String actual = file.getSectionHeaders().getSectionByIndex(i).getName();
			Assert.assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndroidArmBinTset() throws ElfException, FileNotFoundException, IOException {
		File resource = new File(BasicTest.class.getResource("/android_arm_tset").getPath());
		ElfFile file = new ElfFile(resource);
		
		ElfHeader h = file.getHeader();
		Assert.assertEquals(ElfHeader.BitClass.ELFCLASS32, h.getBitClass());
		Assert.assertEquals(ElfHeader.DataFormat.ELFDATA2LSB, h.getDataFormat());
		Assert.assertEquals(ElfHeader.FileType.EXEC, h.getFileType());
		Assert.assertEquals(ElfHeader.Machine.ARM, h.getMachine());
		Assert.assertEquals(32, h.getProgramHeaderEntrySize());
		Assert.assertEquals(7, h.getProgramHeaderEntryCount());
		Assert.assertEquals(52, h.getProgramHeaderOffset());
		Assert.assertEquals(40, h.getSectionHeaderEntrySize());
		Assert.assertEquals(25, h.getSectionHeaderEntryCount());
		Assert.assertEquals(15856, h.getSectionHeaderOffset());
		assertSectionNames(file, "", ".interp", ".dynsym", ".dynstr", ".hash", ".rel.dyn", ".rel.plt", ".plt", ".text");

		ElfDynamicSection dynamic = file.getSectionHeaders().getSectionByName(".dynamic", ElfDynamicSection.class);
		Assert.assertNotNull(dynamic);
		// typedef struct {
		// Elf32_Sword d_tag;
		// union {
		// Elf32_Word d_val;
		// Elf32_Addr d_ptr;
		// } d_un;
		// } Elf32_Dyn;
		Assert.assertEquals(8, dynamic.getEntrySize());
		Assert.assertEquals(248, dynamic.getFileSize());
		
		List<String> libs = new ArrayList<String>();
		for(ElfNeededDynamicEntry e : dynamic.getEntriesOfType(ElfNeededDynamicEntry.class))
			libs.add(e.getLib());
		
		Assert.assertEquals(Arrays.asList("libncursesw.so.6", "libc.so", "libdl.so"), libs);

		// Assert.assertEquals("/system/bin/linker", file.getInterpreter());

		// Dynamic section at offset 0x2e14 contains 26 entries:
		// Tag Type Name/Value
		// 0x00000003 (PLTGOT) 0xbf44
		// 0x00000002 (PLTRELSZ) 352 (bytes)
		// 0x00000017 (JMPREL) 0x8868
		// 0x00000014 (PLTREL) REL
		// 0x00000011 (REL) 0x8828
		// 0x00000012 (RELSZ) 64 (bytes)
		// 0x00000013 (RELENT) 8 (bytes)
		// 0x00000015 (DEBUG) 0x0
		// 0x00000006 (SYMTAB) 0x8128
		// 0x0000000b (SYMENT) 16 (bytes)
		// 0x00000005 (STRTAB) 0x84a8
		// 0x0000000a (STRSZ) 513 (bytes)
		// 0x00000004 (HASH) 0x86ac
		// 0x00000001 (NEEDED) Shared library: [libncursesw.so.6]
		// 0x00000001 (NEEDED) Shared library: [libc.so]
		// 0x00000001 (NEEDED) Shared library: [libdl.so]
		// 0x0000001a (FINI_ARRAY) 0xbdf4
		// 0x0000001c (FINI_ARRAYSZ) 8 (bytes)
		// 0x00000019 (INIT_ARRAY) 0xbdfc
		// 0x0000001b (INIT_ARRAYSZ) 16 (bytes)
		// 0x00000020 (PREINIT_ARRAY) 0xbe0c
		// 0x00000021 (PREINIT_ARRAYSZ) 0x8
		// 0x0000001d (RUNPATH) Library runpath: [/data/data/com.termux/files/usr/lib]
		// 0x0000001e (FLAGS) BIND_NOW
		// 0x6ffffffb (FLAGS_1) Flags: NOW
		// 0x00000000 (NULL) 0x0
		Assert.assertEquals(26, dynamic.getEntryCount());
		Assert.assertEquals(0xbf44, dynamic.getEntry(0).getVal());
		Assert.assertEquals(352, dynamic.getEntry(1).getVal());
		Assert.assertEquals(0x8868, dynamic.getEntry(2).getVal());
		Assert.assertEquals(1, dynamic.getEntry(24).getVal());
		Assert.assertEquals(0, dynamic.getEntry(25).getVal());
	}

	@Test
	public void testAndroidArmLibNcurses() throws ElfException, FileNotFoundException, IOException {
		File resource = new File(BasicTest.class.getResource("/android_arm_libncurses").getPath());
		ElfFile file = new ElfFile(resource);
		ElfHeader h = file.getHeader();
		
		Assert.assertEquals(ElfHeader.BitClass.ELFCLASS32, h.getBitClass());
		Assert.assertEquals(ElfHeader.DataFormat.ELFDATA2LSB, h.getDataFormat());
		Assert.assertEquals(ElfHeader.FileType.DYN, h.getFileType());
		Assert.assertEquals(ElfHeader.Machine.ARM, h.getMachine());
		// Assert.assertEquals("/system/bin/linker", file.getInterpreter());
	}

	@Test
	public void testLinxAmd64BinDash() throws ElfException, FileNotFoundException, IOException {
		File resource = new File(BasicTest.class.getResource("/linux_amd64_bindash").getPath());
		ElfFile file = new ElfFile(resource);
		ElfHeader h = file.getHeader();
		
		Assert.assertEquals(ElfHeader.BitClass.ELFCLASS64, h.getBitClass());
		Assert.assertEquals(ElfHeader.DataFormat.ELFDATA2LSB, h.getDataFormat());
		Assert.assertEquals(ElfHeader.FileType.DYN, h.getFileType());
		Assert.assertEquals(ElfHeader.Machine.X64, h.getMachine());
		Assert.assertEquals(56, h.getProgramHeaderEntrySize());
		Assert.assertEquals(9, h.getProgramHeaderEntryCount());
		Assert.assertEquals(64, h.getProgramHeaderOffset());
		Assert.assertEquals(64, h.getSectionHeaderEntrySize());
		Assert.assertEquals(27, h.getSectionHeaderEntryCount());
		Assert.assertEquals(119544, h.getSectionHeaderOffset());
		assertSectionNames(file, "", ".interp", ".note.ABI-tag", ".note.gnu.build-id", ".gnu.hash", ".dynsym");

		ElfDynamicSection ds = file.getSectionHeaders().getSectionByName(".dynamic", ElfDynamicSection.class);
				
		List<String> libs = new ArrayList<String>();
		for(ElfNeededDynamicEntry e : ds.getEntriesOfType(ElfNeededDynamicEntry.class))
			libs.add(e.getLib());
		
		Assert.assertEquals(Arrays.asList("libc.so.6"), libs);

		// Assert.assertEquals("/lib64/ld-linux-x86-64.so.2", file.getInterpreter());
	}

}
