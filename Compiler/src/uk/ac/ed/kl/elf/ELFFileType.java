package uk.ac.ed.kl.elf;

import uk.ac.ed.kl.exceptions.FormatException;

public enum ELFFileType
{
	Relocatable(0x01),
	Executable (0x02),
	Shared     (0x03),
	Core       (0x04),
	/** Environment-Specific */
	OSSpecific (0xFE00),
	/** Processor-Specific */
	CPUSpecific(0xFF00);
	
	private final int value;
	ELFFileType(int value) { this.value = value; }
	
	public int getValue() { return this.value; }
	
	public static ELFFileType fromValue(int value) throws FormatException
	{
		for (ELFFileType type : ELFFileType.values()) {
			if (type.value == value) return type;
		}
		if ((value & CPUSpecific.value) == CPUSpecific.value) {
			return CPUSpecific;
		}
		else if ((value & OSSpecific.value) == OSSpecific.value) {
			return OSSpecific;
		}
		throw new FormatException("Invalid ELF file type");
	}
}
