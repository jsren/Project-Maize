package uk.ac.ed.kl.elf;

public enum ELFArchitecture
{
	SPARC  (0x02),
	x86    (0x03),
	PowerPC(0x14),
	ARM    (0x28),
	IA64   (0x32),
	x86_64 (0x3E),
	AArch64(0xB7),
	Other  (0xFF);
	
	private final int value;
	ELFArchitecture(int value) { this.value = value; }
	
	public int getValue() { return this.value; }
	
	public static ELFArchitecture fromValue(int value)
	{
		for (ELFArchitecture arch : ELFArchitecture.values()) {
			if (arch.value == value) return arch;
		}
		return Other;
	}
}
