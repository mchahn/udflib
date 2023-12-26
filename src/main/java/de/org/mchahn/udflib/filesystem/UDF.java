package de.org.mchahn.udflib.filesystem;

public interface UDF {
    public static final int    VOLUME_SPACE_INIT_SIZE = 32768;
    public static final long   ROOT_FILENTRY_UID = 0L;
    public static final int    MIN_UNIQUE_ID = 16;
    public static final int    MAX_FILENAME_DSTRLEN = 255;
    public static final int    MAX_PATH_LEN = 1023;
    public static final String ENCODING_UTF8 = "UTF-8";

    public enum Compliance {
        STRICT(10),
        VISTA(9);
        Compliance(int level) {
            this.level = level;
        }
        int level;
        public static Compliance _default = Compliance.STRICT;
        public static boolean is(Compliance cmpl) {
            return _default.level <= cmpl.level;
        }
        public static void setTo(Compliance cmpl) {
            _default = cmpl;
        }
    }
}
