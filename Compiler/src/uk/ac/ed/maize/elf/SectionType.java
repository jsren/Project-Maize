package uk.ac.ed.maize.elf;

public enum SectionType
{
	Null         (0x00),
	ProgramData  (0x01),
	SymbolTable  (0x02),
	StringTable  (0x03),
	RelaEntries  (0x04),
	HashTable    (0x05),
	DynamicTable (0x06),
	NoteInfo     (0x07),
	Uninitialised(0x08),
	RelEntries   (0x09),
	DynSymTable  (0x0B),
	
	/** Environment-Specific */
	OSSpecific(0x60000000),
	/** Processor-Specific */
	CPUSpecific(0x70000000);
	
	private final int value;
	SectionType(int value) { this.value = value; }
	
	public int getValue() { return this.value; }
}
