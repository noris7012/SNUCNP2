package snu.cnp2.client;

/**
 * Created by Awesomepiece on 2016-11-21.
 */
public class Constants {
    public static final int CHUNK_IDX_START = 1;
    public static final int CHUNK_IDX_END = 150;
    public static final String URL = "http://cn.snucse.org/";
    public static final String FILENAME_FORM  = "%d/BigBuckBunny_4s%d.m4s";
    public static final String FILENAME_FORM2 = System.getProperty("user.dir") + "/BigBuckBunny_4s%d_%d.m4s";

    public enum BitRate {
        LEVEL_1 (1473801),
        LEVEL_2 (2409742),
        LEVEL_3 (3340509),
        LEVEL_4 (3936261),
        COUNT (4);

        int value;

        BitRate(int v) {
            this.value = v;
        }
    }

    public enum State {
        INITIAL_BUFFERING,
        BBA
    }

    public static class Segment {
        public final String filepath;

        public Segment(String filepath) {
            this.filepath = filepath;
        }
    }
}
