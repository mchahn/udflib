package de.org.mchahn.udflib.filesystem;

import de.org.mchahn.baselib.util.BinUtils;
import de.org.mchahn.baselib.util.BytePtr;

public class LogicalVolumeHeaderDescriptor {
    public long    uniqueID;
    public BytePtr reserved;

    public static final int RESV_LEN = 24;
    public static final int LENGTH   = 32;

    public LogicalVolumeHeaderDescriptor() {
    }

    public LogicalVolumeHeaderDescriptor(byte[] buf, int ofs) {
        this.uniqueID = BinUtils.readInt64LE(buf, ofs);             ofs += 8;
        this.reserved = new BytePtr.Checked (buf, ofs, RESV_LEN);
    }

    public void write(byte[] buf, int ofs) {
        BinUtils     .writeInt64LE(this.uniqueID, buf, ofs); ofs += 8;
        this.reserved.write       (               buf, ofs);
    }

    public BytePtr data() {
        byte[] result = new byte[LENGTH];
        write(result, 0);
        return new BytePtr(result);
    }

    public String toString() {
        return String.format("LVHD:uid=%d", this.uniqueID);
    }
}
