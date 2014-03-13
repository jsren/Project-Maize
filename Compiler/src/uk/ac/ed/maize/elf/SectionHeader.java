package uk.ac.ed.maize.elf;

public class SectionHeader
{
	String       name;
	SectionType  type;
	SectionFlags flags;
	
	long loadAddress;
	long fileOffset;
	long sectionSize;
	int  linkedSection;
	int  tag;
	long alignment;
	long entrySize;
}
