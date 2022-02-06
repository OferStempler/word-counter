package ofer.stempler.word.counter.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.stereotype.Service;

import ofer.stempler.word.counter.models.PayloadType;

@Service
public class TextService extends WordCounterService {

    @Override
    public void handleText(String text) throws IOException, InterruptedException {

        String filePath = saveToTemp(text);
        LineIterator it = FileUtils.lineIterator(new File(filePath), "UTF-8");
        processText(it);

    }

    @Override
    public PayloadType getServiceType() {
        return PayloadType.TEXT;
    }

    /**
     * Saves the file to a temp dir.
     * @param text
     * @return the file full path and name.
     * @throws IOException
     */
    public String saveToTemp(String text) throws IOException {
        Path path = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), UUID.randomUUID().toString());

        String tmpdir = Files.createDirectories(path).toFile().getAbsolutePath() + "/temp.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(tmpdir));
        writer.write(text);

        writer.close();
        return tmpdir;
    }
}

