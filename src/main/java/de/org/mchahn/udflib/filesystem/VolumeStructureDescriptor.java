package de.org.mchahn.udflib.filesystem;

import de.org.mchahn.baselib.util.BinUtils;
import de.org.mchahn.baselib.util.BytePtr;


public class VolumeStructureDescriptor {
    public static final String BEA01 = "BEA01";
    public static final String TEA01 = "TEA01";
    public static final String NSR02 = "NSR02";
    public static final String NSR03 = "NSR03";

    public static final int STD_ID_LEN = 5;

    public int     structureType;
    public String  standardIdentifier;
    public int     structureVersion;
    public BytePtr data;

    public VolumeStructureDescriptor() {
        this.structureVersion = 1;
    }

    public VolumeStructureDescriptor(byte[] block, int ofs, int len) {
        if (len < 1 + STD_ID_LEN + 1) {
            throw new IllegalArgumentException();
        }

        int end = ofs + len;

        this.structureType = block[ofs++];

        // NOTE: will that always be ASCII, compatible to UTF-8?
        this.standardIdentifier = new String(block, ofs, STD_ID_LEN);
        ofs += STD_ID_LEN;

        this.structureVersion = block[ofs++];

        this.data = new BytePtr.Checked(block, ofs, end - ofs);
    }

    public boolean check() {
        if (!this.standardIdentifier.equals(BEA01) &&
            !this.standardIdentifier.equals(TEA01) &&
            !this.standardIdentifier.equals(NSR02) &&
            !this.standardIdentifier.equals(NSR03)) {
            return false;
        }
        if (0 != this.structureType ||
            1 != this.structureVersion) {
            return false;
        }
        return true;
    }

    public int write(byte[] block, int ofs) {
        block[ofs++] = (byte)this.structureType;

        System.arraycopy(this.standardIdentifier.getBytes(), 0, block, ofs, STD_ID_LEN);
        ofs += STD_ID_LEN;

        block[ofs++] = (byte)this.structureVersion;

        if (null != this.data) {
            System.arraycopy(this.data.buf,
                             this.data.ofs,
                             block,
                             ofs,
                             this.data.len);
            ofs +=           this.data.len;
        }

        return ofs;
    }

    public String toString() {
        return String.format("VSD:type=%d,id=%s,ver=%d",
                BinUtils.u32ToLng(this.structureType),
                this.standardIdentifier,
                BinUtils.u32ToLng(this.structureVersion));
    }
}
