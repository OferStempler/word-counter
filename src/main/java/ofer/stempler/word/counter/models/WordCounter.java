package ofer.stempler.word.counter.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "word_counter",
        indexes = {@Index(name = "word_index",  columnList="word", unique = true)})
public class WordCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String word;

    @Column
    private int occurrences;

    public Long getId() {
        return id;
    }

    public WordCounter() {
    }

    public WordCounter(String word, int occurrences) {
        this.word = word;
        this.occurrences = occurrences;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
}
