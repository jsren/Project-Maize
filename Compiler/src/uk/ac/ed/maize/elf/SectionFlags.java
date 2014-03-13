package uk.ac.ed.maize.elf;

public enum SectionFlags
{
	Writable  (0x01),
	Allocated (0x02),
	Executable(0x03),
	
	/** Environment-Specific */
	OSSpecific (0x0F000000),
	/** Processor-Specific */
	CPUSpecific(0xF0000000);
	
	private final int value;
	SectionFlags(int value) { this.value = value; }
	
	public int getValue() { return this.value; }
}
