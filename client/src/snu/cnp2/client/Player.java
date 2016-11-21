package snu.cnp2.client;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by awesomepiece on 2016. 11. 21..
 */
public class Player {
    private static Player singleton = new Player();
    public static Player get() { return singleton; }

    private static Constants.Segment currentSegment = null;

    public void run() {
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    while(currentSegment == null) {
                        currentSegment = Downloader.get().getSegment();
                    }

                    playCurrentSegment();

                    try {
                        Thread.sleep(Constants.SEGMENT_SIZE_SEC * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    currentSegment = null;
                }
            }
        }.start();
    }

    private void playCurrentSegment() {
        System.out.println("Playing Segment #" + currentSegment.idx);
    }
}
