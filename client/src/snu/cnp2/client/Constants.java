package snu.cnp2.client;

/**
 * Created by Awesomepiece on 2016-11-21.
 */
public class Constants {
    public static final int CHUNK_IDX_START = 1;
    public static final int CHUNK_IDX_END = 150;
    public static final String URL = "http://cn.snucse.org/";
    public static final String FILENAME_FORM  = "%s/BigBuckBunny_4s%d.m4s";
    public static final String FILENAME_FORM2 = System.getProperty("user.dir") + "/BigBuckBunny_4s%d_%s.m4s";
    public static final int INITIAL_BUFFERING_SEC = 20;
    public static final int MINIMUM_RATE_SEC = 8;
    public static final int MEDIUM_RATE_SEC = 42;
    public static final int MAXIMUM_BUFFERING_SEC = 50;
    public static final int SEGMENT_SIZE_SEC = 4;

    public enum State {
        INITIAL_BUFFERING,
        BBA
    }

    public static class BitRate {
        public final String filepath;
        public BitRate(String filepath) { this.filepath = filepath; }
    }

    public static class Segment {
        public final String filepath;

        public Segment(String filepath) {
            this.filepath = filepath;
        }
    }
}
