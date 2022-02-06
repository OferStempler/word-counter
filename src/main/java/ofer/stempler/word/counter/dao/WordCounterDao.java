package ofer.stempler.word.counter.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ofer.stempler.word.counter.models.WordCounter;

@Repository
public interface WordCounterDao extends JpaRepository<WordCounter, Long> {

    WordCounter findByWord(String word);

    @Transactional
    @Modifying
    @Query("UPDATE WordCounter SET occurrences = occurrences + :count WHERE word = :word")
    int updateWordCount(String word, Integer count);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE word_counter", nativeQuery = true)
    void truncateWordCounter();
}
