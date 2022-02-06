package ofer.stempler.word.counter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ofer.stempler.word.counter.dao.WordCounterDao;
import ofer.stempler.word.counter.models.WordCounter;

@Service
public class WordStatistecService {

    @Autowired
    private WordCounterDao wordCounterDao;

    /**
     * Finds the occurrences of a word in a pre sent text.
     * @param word
     * @return the words occurrences.
     */
    public int getWordOccurrences(String word){
        WordCounter wordCounter = null;
        System.out.println("Retrieving occurrences for word: " + word);
        if (StringUtils.hasText(word)) {
            wordCounter = wordCounterDao.findByWord(word.trim().toLowerCase());
        }
        return wordCounter != null ? wordCounter.getOccurrences() : 0;
    }
}
