package de.org.mchahn.udflib.filesystem;

import de.org.mchahn.baselib.util.BinUtils;
import de.org.mchahn.baselib.util.BytePtr;

public class PartitionMap {
    public static final int NOT_SPECIFIED = 0;
    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;

    public byte    type;
    public int     length;
    public BytePtr mapping;

    public PartitionMap() { }

    protected PartitionMap(byte[] buf, int ofs) {
        this.type    = buf[ofs];                    ofs++;
        this.length  = BinUtils.u8ToInt(buf[ofs]);  ofs++;
        this.mapping = new BytePtr.Checked(buf, ofs, this.length - 2);
    }

    public int write(byte[] buf, int ofs) {
        buf[ofs++] = this.type;
        buf[ofs++] = (byte)this.length;
        return ofs;
    }

    public String toString() {
        return String.format("PM:type=%d,len=%d", this.type, this.length);
    }

    ///////////////////////////////////////////////////////////////////////////

    public static PartitionMap[] parse(
            int count, BytePtr data) throws UDFException {
        PartitionMap[] result = new PartitionMap[count];

        byte[] buf = data.buf;
        int ofs = data.ofs;
        int end = ofs + data.len;

        for (int i = 0; i < count; i++) {
            PartitionMap pm;

            switch(buf[ofs]) {
                default: {
                    pm = new PartitionMap(buf, ofs);
                    break;
                }
                case TYPE_1: {
                    pm = new Type1(buf, ofs);
                    break;
                }
                case TYPE_2: {
                    pm = new Type2(buf, ofs);
                    break;
                }
            }

            ofs += pm.length;

            if (ofs > end) {
                throw new UDFException("partition map data out of range");
            }

            result[i] = pm;
        }

        if (ofs != end) {
            throw new UDFException("partition map incomplete (%d)", end - ofs);
        }

        return result;
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Type1 extends PartitionMap {
        public static final int LENGTH = 6;

        public short volumeSequenceNumber;
        public short partitionNumber;

        public Type1() {
            super();
            this.type   = TYPE_1;
            this.length = LENGTH;
        }

        protected Type1(byte[] buf, int ofs) throws UDFException {
            super(buf, ofs);
            ofs += 2;

            if (LENGTH != this.length) {
                throw new UDFException(
                        "invalid partition map (type 1) length %d", this.length);
            }

            this.volumeSequenceNumber = BinUtils.readInt16LE(buf, ofs); ofs += 2;
            this.partitionNumber      = BinUtils.readInt16LE(buf, ofs);
        }

        @Override
        public int write(byte[] buf, int ofs) {
            ofs = super.write(buf, ofs);

            BinUtils.writeInt16LE(this.volumeSequenceNumber, buf, ofs); ofs += 2;
            BinUtils.writeInt16LE(this.partitionNumber     , buf, ofs); ofs += 2;

            return ofs;
        }

        @Override
        public String toString() {
            return super.toString() + String.format(",vsn=%d,pn=%d",
                    BinUtils.u16ToInt(this.volumeSequenceNumber),
                    BinUtils.u16ToInt(this.partitionNumber));
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Type2 extends PartitionMap {
        public static final int LENGTH = 64;

        public BytePtr partitionIdentifier;

        protected Type2(byte[] buf, int ofs) throws UDFException {
            super(buf, ofs);

            if (LENGTH != this.length) {
                throw new UDFException(
                        "invalid partition map (type 2) length %d", this.length);
            }

            this.partitionIdentifier = new BytePtr.Checked(buf, ofs, LENGTH - 2);
        }
    }
}
