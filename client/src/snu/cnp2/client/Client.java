package snu.cnp2.client;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.boxes.apple.AppleNameBox;
import com.googlecode.mp4parser.util.Path;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

/**
 * Created by noris on 2016-11-18.
 */
public class Client {
    private static final int CHUNK_IDX_START = 1;
    private static final int CHUNK_IDX_END = 150;
    private static final String URL = "http://cn.snucse.org/";
    private static final String FILENAME_FORM = "%d/BigBuckBunny_4s%d.m4s";

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

    public static void main(String[] args) throws IOException {
        Client cmd = new Client();
//        String xml = cmd.read("resource\\BigBuckBunny_4s3.m4s");
//        System.err.println(xml);
        cmd.downloadFile();
    }

    public String read(String videoFilePath) throws IOException {
        IsoFile isoFile = new IsoFile(videoFilePath);

        AppleNameBox nam = Path.getPath(isoFile, "/moov[0]/udta[0]/meta[0]/ilst/©nam");
        String xml = nam.getValue();
        isoFile.close();
        return xml;
    }

    public void downloadFile() {
        for(int chunkIdx = CHUNK_IDX_START; chunkIdx <= CHUNK_IDX_END; chunkIdx++) {
            try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                // get proper url
                BitRate bitRate = getBBA(chunkIdx);
                String url = getRequestURL(bitRate, chunkIdx);
                HttpGet get = new HttpGet(url);

                HttpResponse response = client.execute(get);

                // TODO Entity Null Check

                HttpEntity entity = response.getEntity();

                if(entity == null) {
                    System.err.print("Request Failed : (" + url + ")");
                    continue;
                }

                String filePath = String.format(FILENAME_FORM, bitRate, chunkIdx);

                try (BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)))) {
                    int inByte;
                    while((inByte = bis.read()) != -1)
                        bos.write(inByte);
                } catch (IOException e) {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRequestURL(BitRate bitRate, int chunkIdx) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL)
            .append(String.format(FILENAME_FORM, bitRate.value, chunkIdx));

        return sb.toString();
    }

    private BitRate getBBA(int chunkIdx) {
        // TODO

        // 4 sec per chunk. chunk idx starts from 1.
        int elapsedSec = (chunkIdx - 1) * 4;

        if(elapsedSec <= 8) {
            return BitRate.LEVEL_1;
        } else if(elapsedSec <= 42) {
            int mediumLevelCount = BitRate.COUNT.value - 2;

        }

    }
}
