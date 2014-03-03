package uk.ac.ed.kl.elf;

import uk.ac.ed.kl.exceptions.FormatException;

public enum ELFPlatform
{	
	Other  (0x00),
	HPUX   (0x01),
	NetBSD (0x02),
	Linux  (0x03),
	Solaris(0x06),
	AIX    (0x07),
	IRIX   (0x08),
	FreeBSD(0x09),
	OpenBSD(0x0C);
	
	private final int value;
	ELFPlatform(int value) { this.value = value; }
	
	public int getValue() { return this.value; }
	
	public static ELFPlatform fromValue(int value) throws FormatException
	{
		for (ELFPlatform platform : ELFPlatform.values()) {
			if (platform.value == value) return platform;
		}
		return Other;
	}
}
