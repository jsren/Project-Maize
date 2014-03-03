package uk.ac.ed.kl.elf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import uk.ac.ed.kl.exceptions.FormatException;

public class ELFHeader
{
	long entryPoint;
	long programHeaderRef;
	long sectionHeaderRef;
	int  flags;
	
	ByteOrder endianness;
	boolean   bits64;
	
	short programHeaderEntrySize;
	short sectionHeaderEntrySize;
	short programHeaderEntryCount;
	short sectionHeaderEntryCount;
	short sectionHeaderNamesIndex;
	
	ELFPlatform     platform;
	ELFFileType     type;
	ELFArchitecture architecture;
	
	/* ======= static constants ======= */
	public  static final int    size32 = 52;
	public  static final int    size64 = 64;
	private static final byte[] magic  = {0x7F, 'E', 'L', 'F'};
	/* ================================ */
	
	public ELFHeader(boolean bits64, boolean bigEndian, long ep, long pHeadPtr, long sHeadPtr,
			int flags, short szPHEntry, short szSHEntry, short noPHEntry, short noSHEntry,
			short namesIndex, ELFPlatform platform, ELFFileType type, ELFArchitecture arch)
	{
		this.bits64       = bits64;
		this.entryPoint   = ep;
		this.flags        = flags;
		this.platform     = platform;
		this.type         = type;
		this.architecture = arch;
		this.endianness   = bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		
		this.programHeaderRef        = pHeadPtr;
		this.sectionHeaderRef        = sHeadPtr;
		this.programHeaderEntrySize  = szPHEntry;
		this.programHeaderEntryCount = noPHEntry;
		this.sectionHeaderEntrySize  = szSHEntry;
		this.sectionHeaderEntryCount = noSHEntry;
		this.sectionHeaderNamesIndex = namesIndex;
	}
	
	public byte[] toByteArray()
	{
		byte[] array = new byte[this.bits64 ? size64 : size32];
		this.toByteArray(array);
		
		return array;
	}
	public void toByteArray(byte[] array)
	{
		ByteBuffer buffer = ByteBuffer.wrap(array);
		buffer.order(this.endianness);
		
		// Write magic
		for (byte b : magic) { buffer.put(b); }
		
		// Write EL_CLASS
		buffer.put(this.bits64 ? (byte)2 : (byte)1);
		// Write EL_DATA
		buffer.put(this.endianness == ByteOrder.LITTLE_ENDIAN ? (byte)1 : (byte)2);
		// Write EL_VERSION
		buffer.put((byte)1);
		// Write EL_OSABI
		buffer.put((byte)this.platform.getValue());
		// Write EL_ABIVERSION & EL_PAD
		for (int i = 0; i < 8; ++i) { buffer.put((byte)0); }
		// Write e_type
		buffer.putShort((short)this.type.getValue());
		// Write e_machine
		buffer.putShort((short)this.architecture.getValue());
		// Write e_version
		buffer.putInt((int)1);
		
		if (this.bits64)
		{
			buffer.putLong(this.entryPoint);       // Write e_entry
			buffer.putLong(this.programHeaderRef); // Write e_phoff
			buffer.putLong(this.sectionHeaderRef); // Write e_shoff
		}
		else
		{
			buffer.putInt((int)this.entryPoint);       // Write e_entry
			buffer.putInt((int)this.programHeaderRef); // Write e_phoff
			buffer.putInt((int)this.sectionHeaderRef); // Write e_shoff
		}
		// Write e_flags
		buffer.putInt(this.flags);
		// Write e_ehsize
		buffer.putShort(this.bits64 ? (short)64 : (short)52);
		// Write e_phentsize
		buffer.putShort(this.programHeaderEntrySize);
		// Write e_phnum
		buffer.putShort(this.programHeaderEntryCount);
		// Write e_shentsize
		buffer.putShort(this.sectionHeaderEntrySize);
		// Write e_shnum
		buffer.putShort(this.sectionHeaderEntryCount);
		// Write e_shstrndx
		buffer.putShort(this.sectionHeaderNamesIndex);
	}
	
	// There are some values which we don't need to store
	@SuppressWarnings("unused")
	public static ELFHeader fromBuffer(ByteBuffer buffer) throws FormatException
	{
		// e_ident is little-endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// Assert valid ELF magic, don't proceed otherwise
		for (int i = 0; i < 4; ++i)
		{
			if (buffer.get() != magic[i]) {
				throw new FormatException("Invalid magic in ELF header");
			}
		}
		
		// Get the separate e_ident values
		// (NB: the spec. uses an EI prefix, not EL)
		// Intentional Byte-Int promotion
		int EL_CLASS      = buffer.get();
		int EL_DATA       = buffer.get();
		int EL_VERSION    = buffer.get();
		int EL_OSABI      = buffer.get();
		int EL_ABIVERSION = buffer.get();
		
		// Get if the header is in 64-bit format
		boolean x64 = EL_CLASS == 0x2;
		// Get if the header is in big endian
		boolean BIG = EL_DATA  == 0x2;
		// ...and change the buffer order as needed
		if (BIG) buffer.order(ByteOrder.BIG_ENDIAN);
		
		// Get the rest of the header values
		buffer.position(0x10);             // Skip padding bytes
		int e_type    = buffer.getShort(); // Intentional Short->Int promotion
		int e_machine = buffer.getShort();
		int e_version = buffer.getInt();
		
		//TODO Warn on version > 1
		
		// These values differ in width when x64
		long e_entry, e_phoff, e_shoff;
		if (x64)
		{
			e_entry = buffer.getLong();
			e_phoff = buffer.getLong();
			e_shoff = buffer.getLong();
		}
		else
		{
			e_entry = buffer.getInt();
			e_phoff = buffer.getInt();
			e_shoff = buffer.getInt();
		}
		// Now the same width
		int   e_flags     = buffer.getInt();
		short e_ehsize    = buffer.getShort();
		short e_phentsize = buffer.getShort();
		short e_phnum     = buffer.getShort();
		short e_shentsize = buffer.getShort();
		short e_shnum     = buffer.getShort();
		short e_shstrndx  = buffer.getShort();
		
		// Now fill fields:
		return new ELFHeader
		(
				x64, BIG, e_entry, e_phoff, e_shoff, e_flags, 
				e_phentsize, e_shentsize, e_phnum, e_shnum, e_shstrndx,
				ELFPlatform    .fromValue(EL_OSABI), 
				ELFFileType    .fromValue(e_type),
				ELFArchitecture.fromValue(e_machine)
		);
	}
	
}