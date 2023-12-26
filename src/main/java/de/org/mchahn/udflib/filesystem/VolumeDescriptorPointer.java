package de.org.mchahn.udflib.filesystem;

import de.org.mchahn.baselib.util.BinUtils;
import de.org.mchahn.baselib.util.BytePtr;

public class VolumeDescriptorPointer extends Descriptor {
    public int              volumeDescriptorSequenceNumber;
    public ExtentDescriptor nextVolumeDescriptorSequenceExtent;
    public BytePtr          reserved;

    public static final int RESV_LEN = 484;

    protected VolumeDescriptorPointer(Tag tag, byte[] buf, int ofs) throws UDFException {
        super(tag);

        this.volumeDescriptorSequenceNumber     = BinUtils.readInt32LE(buf, ofs);   ofs += 4;
        this.nextVolumeDescriptorSequenceExtent = ExtentDescriptor.parse(buf, ofs); ofs += ExtentDescriptor.LENGTH;
        this.reserved                           = new BytePtr.Checked(buf, ofs, RESV_LEN);
    }

    public String toString() {
        return String.format("VDP:tag=[%s],vdsn=%s,nvdse=[%s]",
                this.tag,
                this.volumeDescriptorSequenceNumber,
                this.nextVolumeDescriptorSequenceExtent);
    }
}
