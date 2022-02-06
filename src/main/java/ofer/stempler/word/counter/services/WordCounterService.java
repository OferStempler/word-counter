package ofer.stempler.word.counter.services;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import ofer.stempler.word.counter.dao.WordCounterDao;
import ofer.stempler.word.counter.models.PayloadType;
import ofer.stempler.word.counter.models.WordCounter;


public abstract class WordCounterService {

//    private static Map<String, Integer> wordsMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> wordsMap = null;
    private ReentrantLock reentrantLock = new ReentrantLock();
    @Autowired
    private WordCounterDao wordCounterDao;

    List<WordCounter> wordsList = new ArrayList<>();
    Instant startCreatingMap = null;
    Instant endCreatingMap = null;
    Instant endPersisting = null;
    Instant startPersisting = null;

    /**
     * Handles to text specifically according to required format.
     * Each service will override and implement accordingly.
     * @param text
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract void handleText(String text) throws IOException, InterruptedException;


    public abstract PayloadType getServiceType();

    /**
     * Process the incoming text. Maps the unique words and persist them.
     * @param it LineIterator
     * @throws InterruptedException
     */
    public void processText(LineIterator it) throws InterruptedException {
        readWithThreadPool(it);
        convertToWordCounter();
        truncateAndSave();
        printRunSummary();
    }

    /**
     * Prints the operations time summary and unique word count.
     */
    private void printRunSummary() {
        long mapTimeElapsed = Duration.between(startCreatingMap, endCreatingMap).toMillis();
        long persistTimeElapsed = Duration.between(startPersisting, endPersisting).toMillis();
        System.out.println("Successfully finished process.");
        System.out.println("Time took to create map: " + mapTimeElapsed + " Milliseconds");
        System.out.println("Time took to persist : " + persistTimeElapsed + " Milliseconds");
        System.out.println("Unique records persisted: " + wordsList.size());
    }

    /**
     * Truncates the ddb and saves the word map with the unique words and their occurrences.
     */
    private void truncateAndSave() {
        System.out.println("Truncating db and persisting values");
        startPersisting = Instant.now();
        wordCounterDao.truncateWordCounter();
        wordCounterDao.saveAll(wordsList);
        endPersisting = Instant.now();
        System.out.println("Successfully saved all words to db.");
    }

    /**
     * Converts the map into WordCounter to be persisted easily using Spring JPA.
     */
    private void convertToWordCounter() {
        wordsList = wordsMap.entrySet().stream()
                .map(e -> new WordCounter(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Processes the text using parallels threads.
     * Utilizing a concurrentHashMap prevents the threads to access the hash map holding the unique words
     * and their count.
     * @param it LineIterator
     * @throws InterruptedException
     */
    public void readWithThreadPool(LineIterator it) throws InterruptedException {

        System.out.println("Starting processing text");
        wordsMap = new ConcurrentHashMap<>();
        startCreatingMap = Instant.now();
//        wordsMap = new ConcurrentHashMap<>();

        //optimize threads can be calculated
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        while (it.hasNext()) {
            StringBuilder text = new StringBuilder();

            //number of lines can be optimized
            for (int i = 0; i < 500; i++) {
                if (it.hasNext()) {
                    String line = it.nextLine();
                    text.append(line).append("\n");
                }

            }
            executorService.submit(() -> {
                countAndMerge(text.toString());
            });
        }

        executorService.shutdown();
        //Timeout can be changed/configured
        if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
            executorService.shutdownNow();
            throw new RuntimeException("Executor service was not properly shot down.");
        }
       endCreatingMap = Instant.now();
        System.out.println("Successfully counted all unique words");
    }

    /**
     * Each thread counts the words into a new map.
     * All thread's maps are finally merged into a concurrentHashMap.
     * @param str
     * @return wordsMap
     */
    public Map<String, Integer> countAndMerge(String str) {
        Map<String, Integer> map1 = countWords(splitIntoWords(str));
        Map<String, Integer> map3 = new HashMap<>(map1);
        map3.forEach(
                (key, value) -> wordsMap.merge(key, value, Integer::sum));
        return wordsMap;
    }

    /**
     * Splits original string to words with no punctuations.
     *
     * @param str
     * @return array of words
     */
    protected String[] splitIntoWords(String str) {
        return str.split("[\\p{Punct}\\s]+");

    }

    /**
     * Counts all words occurrences in a given array and saves them to a Map.
     *
     * @param words array
     * @return map with all words occurrences.
     */
    protected Map<String, Integer> countWords(String[] words) {
        Map<String, Integer> wordsMap= new HashMap<>();
        for (String word : words) {
            if (StringUtils.isNotBlank(word)) {
                Integer count = wordsMap.getOrDefault(word.toLowerCase(), 0);
                wordsMap.put(word.toLowerCase(), count + 1);
            }
        }
        return wordsMap;
    }
}
