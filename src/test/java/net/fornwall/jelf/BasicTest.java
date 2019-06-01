package net.fornwall.jelf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import net.fornwall.jelf.section.ElfSection;

public class BasicTest {

	private ElfFile parseFile(String name) throws ElfException, FileNotFoundException, IOException {
		return ElfFile.fromStream(BasicTest.class.getResourceAsStream("/" + name));
	}

	private static void assertSectionNames(ElfFile file, String... expectedSectionNames) throws IOException {
		for (int i = 0; i < expectedSectionNames.length; i++) {
			String expected = expectedSectionNames[i];
			String actual = file.getSection(i).getName();
			if (expected == null) {
				Assert.assertNull(actual);
			} else {
				Assert.assertEquals(expected, actual);
			}
		}
	}

	@Test
	public void testAndroidArmBinTset() throws ElfException, FileNotFoundException, IOException {
		ElfFile file = parseFile("android_arm_tset");
		ElfHeader h = file.header;
		
		Assert.assertEquals(ElfHeader.BitClass.ELFCLASS32, h.ei_class);
		Assert.assertEquals(ElfHeader.Data.ELFDATA2LSB, h.ei_data);
		Assert.assertEquals(ElfHeader.Type.EXEC, h.e_type);
		Assert.assertEquals(ElfHeader.Machine.ARM, h.e_machine);
		Assert.assertEquals(32, h.e_phentsize);
		Assert.assertEquals(7, h.e_phnum);
		Assert.assertEquals(52, h.e_phoff);
		Assert.assertEquals(40, h.e_shentsize);
		Assert.assertEquals(25, h.e_shnum);
		Assert.assertEquals(15856, h.e_shoff);
		assertSectionNames(file, null, ".interp", ".dynsym", ".dynstr", ".hash", ".rel.dyn", ".rel.plt", ".plt", ".text");

		ElfSection dynamic = file.getDynamicLinkSection();
		Assert.assertNotNull(dynamic);
		// typedef struct {
		// Elf32_Sword d_tag;
		// union {
		// Elf32_Word d_val;
		// Elf32_Addr d_ptr;
		// } d_un;
		// } Elf32_Dyn;
		Assert.assertEquals(8, dynamic.entry_size);
		Assert.assertEquals(248, dynamic.size);

		ElfDynamicStructure ds = dynamic.getDynamicSection();
		Assert.assertEquals(Arrays.asList("libncursesw.so.6", "libc.so", "libdl.so"), ds.getNeededLibraries());

		Assert.assertEquals("/system/bin/linker", file.getInterpreter());

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
		ElfDynamicStructure dynamicStructure = file.getDynamicLinkSection().getDynamicSection();
		Assert.assertEquals(26, dynamicStructure.entries.size());
		Assert.assertEquals(new ElfDynamicStructure.ElfDynamicSectionEntry(3, 0xbf44), dynamicStructure.entries.get(0));
		Assert.assertEquals(new ElfDynamicStructure.ElfDynamicSectionEntry(2, 352), dynamicStructure.entries.get(1));
		Assert.assertEquals(new ElfDynamicStructure.ElfDynamicSectionEntry(0x17, 0x8868), dynamicStructure.entries.get(2));
		Assert.assertEquals(new ElfDynamicStructure.ElfDynamicSectionEntry(0x6ffffffb, 1), dynamicStructure.entries.get(24));
		Assert.assertEquals(new ElfDynamicStructure.ElfDynamicSectionEntry(0, 0), dynamicStructure.entries.get(25));
	}

	@Test
	public void testAndroidArmLibNcurses() throws ElfException, FileNotFoundException, IOException {
		ElfFile file = parseFile("android_arm_libncurses");
		ElfHeader h = file.header;
		
		Assert.assertEquals(ElfHeader.BitClass.ELFCLASS32, h.ei_class);
		Assert.assertEquals(ElfHeader.Data.ELFDATA2LSB, h.ei_data);
		Assert.assertEquals(ElfHeader.Type.DYN, h.e_type);
		Assert.assertEquals(ElfHeader.Machine.ARM, h.e_machine);
		Assert.assertEquals("/system/bin/linker", file.getInterpreter());
	}

	@Test
	public void testLinxAmd64BinDash() throws ElfException, FileNotFoundException, IOException {
		ElfFile file = parseFile("linux_amd64_bindash");
		ElfHeader h = file.header;
		
		Assert.assertEquals(ElfHeader.BitClass.ELFCLASS64, h.ei_class);
		Assert.assertEquals(ElfHeader.Data.ELFDATA2LSB, h.ei_data);
		Assert.assertEquals(ElfHeader.Type.DYN, h.e_type);
		Assert.assertEquals(ElfHeader.Machine.X64, h.e_machine);
		Assert.assertEquals(56, h.e_phentsize);
		Assert.assertEquals(9, h.e_phnum);
		Assert.assertEquals(64, h.e_phoff);
		Assert.assertEquals(64, h.e_shentsize);
		Assert.assertEquals(27, h.e_shnum);
		Assert.assertEquals(119544, h.e_shoff);
		assertSectionNames(file, null, ".interp", ".note.ABI-tag", ".note.gnu.build-id", ".gnu.hash", ".dynsym");

		ElfDynamicStructure ds = file.getDynamicLinkSection().getDynamicSection();
		Assert.assertEquals(Arrays.asList("libc.so.6"), ds.getNeededLibraries());

		Assert.assertEquals("/lib64/ld-linux-x86-64.so.2", file.getInterpreter());
	}

}
