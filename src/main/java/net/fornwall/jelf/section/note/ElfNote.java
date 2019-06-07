package net.fornwall.jelf.section.note;

import net.fornwall.jelf.ElfException;
import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfParser;
import net.fornwall.jelf.section.ElfNoteSection;

public class ElfNote {
    private int type;
    private String name;
    private byte[] note;
    
    private ElfNoteSection section;
    
    private ElfNote(ElfNoteSection section, long offset) {
    	this.section = section;
    	ElfParser parser = section.getFile().getParser();
    	
        parser.seek(offset);
        int nameSize = parser.readInt();
        int descSize = parser.readInt();
        
        type = parser.readInt();
        byte nameBytes[] = new byte[nameSize];
        note = new byte[descSize];
        int bytesRead = parser.read(nameBytes);
        
        if (bytesRead != nameSize) {
            throw new ElfException("Error reading note (read " + bytesRead + "bytes - expected to " + "read " + nameSize + "bytes)");
        }
        
        while (bytesRead % 4 != 0) { // finish reading the padding to the nearest 4 bytes
            parser.readUnsignedByte();
            bytesRead += 1;
        }
        
        // Check for null terminator
        if(nameSize > 0 && nameBytes[nameSize - 1] != '\0')
        	throw new ElfException("Illegally formatted note");
        
        bytesRead = parser.read(note);
        if (bytesRead != descSize) {
            throw new ElfException("Error reading note (read " + bytesRead + "bytes - expected to " + "read " + descSize + "bytes)");
        }
        
        while (bytesRead % 4 != 0) { // finish reading the padding to the nearest 4 bytes
            parser.readUnsignedByte();
            bytesRead += 1;
        }
        
        name = new String(nameBytes, 0, nameSize-1); // unnecessary trailing '\0'
    }
    
    public static ElfNote noteFactory(ElfNoteSection section, long offset) {
    	return new ElfNote(section, offset);
    }
    
    /**
     * @return Returns the file that contains this note
     */
    public ElfFile getFile() {
    	return section.getFile();
    }
    
    /**
     * @return Returns the {@link ElfNoteSection} that contains this note
     */
    public ElfNoteSection getSection() {
    	return section;
    }
    
    /**
     * @return Returns the name of this note
     */
    public String getNoteName() {
    	return name;
    }
    
    /**
     * @return Returns the description of this note formated as a string
     */
    public String getDescString() {
    	return new String(note);
    }
    
    /**
     * @param index the index of the byte in the description to access
     * @return Returns the byte at the provided index
     */
    public byte getDescByte(int index) {
    	if(index < 0 || index >= note.length)
    		throw new ElfException("Note description index out of bounds");
    	return note[index];
    }
    
    /**
     * @param index the index of the short in the description to access
     * @return Returns the short at the provided index
     */
    public short getDescShort(int index) {
    	return (short)((((short)getDescByte(2 * index)) << 8) + getDescByte(2 * index + 1));
    }
    
    /**
     * @param index the index of the int in the description to access
     * @return Returns the int at the provided index
     */
    public int getDescInt(int index) {
    	return (int)((((int)getDescShort(2 * index)) << 16) + getDescShort(2 * index + 1));
    }
    
    /**
     * @param index the index of the long in the description to access
     * @return Returns the long at the provided index
     */
    public long getDescLong(int index) {
    	return (long)((((long)getDescShort(2 * index)) << 16) + getDescShort(2 * index + 1));
    }
    
    /**
     * @return Returns the size in bytes of the note description
     */
    public int getDescSize() {
    	return note.length;
    }
    
    /**
     * @return Returns the type of note
     */
    public int getNoteType() {
    	return type;
    }
    
    /**
     * @return Returns the size of the note name including extra characters and padding
     */
    public int getNameRawSize() {
    	if(name.length() % 4 == 0)
    		return name.length();
    	return name.length() + 4 - (name.length() % 4);
    }
    
    /**
     * @return Returns the size of the note description including extra characters and padding
     */
    public int getDescRawSize() {
    	if(note.length % 4 == 0)
    		return note.length;
    	return note.length + 4 - (note.length % 4);
    }
    
    /**
     * @return Returns the total size this note consumes on the file
     */
    public int getFileSize() {
    	return 12 + getNameRawSize() + getDescRawSize();
    }
}
