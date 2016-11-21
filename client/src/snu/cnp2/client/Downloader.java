package snu.cnp2.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Awesomepiece on 2016-11-21.
 */
    public class Downloader {

    public static Downloader singleton = new Downloader();
    public static Downloader get() { return singleton; }

    private ConcurrentLinkedQueue<Constants.Segment> buffer = new ConcurrentLinkedQueue<>();
    private ArrayList<Constants.FrameRate> frameRates = new ArrayList<>();
    private final Object lock = new Object();
    private Constants.State state = Constants.State.INITIAL_BUFFERING;

    private Downloader() {

    }

    public void clearFrameRate() {
        frameRates.clear();
    }

    public Downloader addFrameRate(Constants.FrameRate frameRate) {
        frameRates.add(frameRate);

        return this;
    }

    public void run() {
        if(frameRates.size() < 3) {
            System.err.println("Please Add FrameRate Information.");
            return;
        }

        new Thread(){
            @Override
            public void run() {
                state = Constants.State.INITIAL_BUFFERING;

                int chunkIdx = Constants.CHUNK_IDX_START;

                while(chunkIdx <= Constants.CHUNK_IDX_END) {
                    // 초기 버퍼링 끝났는지 확인
                    if(state == Constants.State.INITIAL_BUFFERING &&
                            buffer.size() * Constants.SEGMENT_SIZE_SEC > Constants.INITIAL_BUFFERING_SEC) {
                        state = Constants.State.BUFFERING;
                        Player.get().run();
                    }

                    // 버퍼 꽉 찼으면 대기
                    if((buffer.size() + 1) * Constants.SEGMENT_SIZE_SEC > Constants.MAXIMUM_BUFFERING_SEC) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    Constants.FrameRate frameRate = getBBA();

                    String filePath = downloadFile(chunkIdx, frameRate);
                    if(filePath != null) {
                        System.out.println("Complete : " + filePath);
                        Constants.Segment segment = new Constants.Segment(filePath, chunkIdx);
                        buffer.add(segment);
                        chunkIdx++;
                    } else {
                        System.err.println("Failed to download chunk #" + chunkIdx);
                    }
                }
            }
        }.start();
    }

    public Constants.Segment getSegment() {
        return buffer.poll();
    }

    private String getRequestURL(Constants.FrameRate frameRate, int chunkIdx) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.URL)
                .append(String.format(Constants.FILENAME_FORM, frameRate.filepath, chunkIdx));

        return sb.toString();
    }

    private Constants.FrameRate getBBA() {
        // Lock 이 필요할까? 필요없을 것 같은데
        int sec = buffer.size() * Constants.SEGMENT_SIZE_SEC;

        if (sec <= Constants.MINIMUM_RATE_SEC)
            return frameRates.get(0);
        else if (sec <= Constants.MEDIUM_RATE_SEC) {
            int mediumRange = Constants.MEDIUM_RATE_SEC - Constants.MINIMUM_RATE_SEC;
            int mediumCount = frameRates.size() - 2;
            int secDiff = mediumRange / mediumCount;

            for(int i = 0; i < mediumCount; i++) {
                if(sec <= Constants.MINIMUM_RATE_SEC + (secDiff * (i + 1))) {
                    return frameRates.get(i + 1);
                }
            }
        }
        else
            return frameRates.get(frameRates.size() - 1);

        return frameRates.get(0);
    }

    private String downloadFile(int chunkIdx, Constants.FrameRate frameRate) {
        System.out.println("Try Download (" + chunkIdx + ") rate : (" + frameRate.filepath + ") buffer(sec) : (" + buffer.size() * 4 + ")");

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            // get proper url
            String url = getRequestURL(frameRate, chunkIdx);
            HttpGet get = new HttpGet(url);

            HttpResponse response = client.execute(get);

            HttpEntity entity = response.getEntity();

            if(entity == null) {
                System.err.println("Request Failed : (" + url + ")");
                return null;
            }

            String filePath = String.format(Constants.FILENAME_FORM2, chunkIdx, frameRate.filepath);

            try (BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)))) {
                int inByte;
                while((inByte = bis.read()) != -1)
                    bos.write(inByte);

                return filePath;
            } catch (IOException e) {
                System.err.println("IOException : " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            System.err.println("IOException : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
