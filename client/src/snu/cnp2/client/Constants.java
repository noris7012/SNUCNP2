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
    public static final int WAIT_FOR_BUFFER_SEC = 1;

    public enum State {
        INITIAL_BUFFERING,
        BUFFERING
    }

    public static class FrameRate {
        public final String filepath;
        public FrameRate(String filepath) { this.filepath = filepath; }
    }

    public static class Segment {
        public final String filepath;
        public final int idx;

        public Segment(String filepath, int idx) {
            this.filepath = filepath;
            this.idx = idx;
        }
    }
}
