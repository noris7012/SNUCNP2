package snu.cnp2.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Awesomepiece on 2016-11-21.
 */
public class Downloader {

    public static Downloader singleton = new Downloader();
    public static Downloader get() { return singleton; }

    private Queue<Constants.Segment> buffer = new LinkedList<>();
    private Object lock = new Object();

    private Downloader() {

    }

    public void run() {
        new Thread(){
            @Override
            public void run() {
                for(int chunkIdx = Constants.CHUNK_IDX_START; chunkIdx <= Constants.CHUNK_IDX_END; chunkIdx++) {
                    Constants.BitRate bitRate = getBBA(chunkIdx);

                    String filePath = downloadFile(chunkIdx, bitRate);
                    if (filePath == null) {
                        System.out.println("FilePath is null");
                        continue;
                    }

                    Constants.Segment segment = new Constants.Segment(filePath);
                    synchronized (lock) {
                        buffer.add(segment);
                    }
                }
            }
        }.run();
    }

    public Constants.Segment getSegment() {
        if (buffer.isEmpty())
            return null;

        synchronized (lock) {
            return buffer.poll();
        }
    }

    private String getRequestURL(Constants.BitRate bitRate, int chunkIdx) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.URL)
                .append(String.format(Constants.FILENAME_FORM, bitRate.value, chunkIdx));

        return sb.toString();
    }

    private Constants.BitRate getBBA(int chunkIdx) {
        // Lock 이 필요할까? 필요없을 것 같은데
        int size = buffer.size();

        if (size * 4 <= 8)
            return Constants.BitRate.LEVEL_1;
        else if (size * 4 <= 25)
            return Constants.BitRate.LEVEL_2;
        else if (size * 4 <= 42)
            return Constants.BitRate.LEVEL_3;
        else
            return Constants.BitRate.LEVEL_4;

//        // 4 sec per chunk. chunk idx starts from 1.
//        int elapsedSec = (chunkIdx - 1) * 4;
//
//        if(elapsedSec <= 8) {
//            return Constants.BitRate.LEVEL_1;
//        } else if(elapsedSec <= 42) {
//            int mediumLevelCount = Constants.BitRate.COUNT.value - 2;
//
//        }
    }

    private String downloadFile(int chunkIdx, Constants.BitRate bitRate) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            // get proper url
            String url = getRequestURL(bitRate, chunkIdx);
            HttpGet get = new HttpGet(url);

            HttpResponse response = client.execute(get);

            // TODO Entity Null Check

            HttpEntity entity = response.getEntity();

            if(entity == null) {
                System.err.println("Request Failed : (" + url + ")");
                return null;
            }

            String filePath = String.format(Constants.FILENAME_FORM2, chunkIdx, bitRate.value);

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
