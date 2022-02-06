package ofer.stempler.word.counter.services;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.stereotype.Service;

import ofer.stempler.word.counter.models.PayloadType;

@Service
public class FilesService extends WordCounterService {

    @Override
    public void handleText(String text) throws IOException, InterruptedException {
        readFromPath(text);
    }

    @Override
    public PayloadType getServiceType() {
        return PayloadType.PATH;
    }

    /**
     * Reads the file path using Line iterator.
     * @param path from request
     * @throws IOException
     * @throws InterruptedException
     */
    public void readFromPath(String path) throws IOException, InterruptedException {
        LineIterator it = FileUtils.lineIterator(new File(path), "UTF-8");
        processText(it);
    }
}
