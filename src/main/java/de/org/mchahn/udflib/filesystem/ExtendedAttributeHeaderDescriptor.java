package de.org.mchahn.udflib.filesystem;

import de.org.mchahn.baselib.util.BinUtils;

public class ExtendedAttributeHeaderDescriptor extends Descriptor {
    public int implementationAttributesLocation;
    public int applicationAttributesLocation;

    public static final int LENGTH = Tag.LENGTH + 8;

    public ExtendedAttributeHeaderDescriptor(Tag tag, byte[] buf, int ofs) {
        super(tag);

        this.implementationAttributesLocation = BinUtils.readInt32LE(buf, ofs); ofs += 4;
        this.applicationAttributesLocation    = BinUtils.readInt32LE(buf, ofs);
    }

    public String toString() {
        return String.format("EAHD:tag=[%s],ial=%d,aal=%d",     // (ial and aal can be negative?)
                this.tag,
                this.implementationAttributesLocation,
                this.applicationAttributesLocation);
    }
}
