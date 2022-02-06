package ofer.stempler.word.counter.services;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.regex.Pattern;

import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class WordCounterServiceTest {

    private TextService wordCounterService = new TextService();

    @Test
    public void testWordsSplitRegex() {
        String test = "Hi! My name is(what?), my@#$%$%^*&*() name is(who?), my name is Slim Shady";
        String[] words = wordCounterService.splitIntoWords(test);
        for (String s : words) {
            System.out.println(s);
            assertTrue(!Pattern.matches("\\p{Punct}", s));
        }
    }

    @Test
    public void testWordCount() {
        String[] strings = {"ofer", "ofer", "STEMPLER", "stempler", "stempler"};
        Map<String, Integer> wordMap = wordCounterService.countWords(strings);

        assertThat(wordMap, IsMapContaining.hasEntry("ofer", 2));
        assertThat(wordMap, IsMapContaining.hasEntry("stempler", 3));
        assertThat(wordMap, not(IsMapContaining.hasEntry("jjj", 9)));
    }
}
