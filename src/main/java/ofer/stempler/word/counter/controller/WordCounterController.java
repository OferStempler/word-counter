package ofer.stempler.word.counter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ofer.stempler.word.counter.models.IncomingRequest;
import ofer.stempler.word.counter.services.MainWordCountService;
import ofer.stempler.word.counter.services.WordStatistecService;

@RestController
public class WordCounterController {

    @Autowired
    private MainWordCountService mainWordCountService;

    @Autowired
    private WordStatistecService wordStatistecService;

    /**
     * Main end point for input data.
     * The data will be processed and save in db as unique words and their count.
     * @param incomingRequest
     * @return 200 or 500 for error
     */
    @PostMapping(value = "/wordCounter")
    public ResponseEntity<String> countWords(@RequestBody IncomingRequest incomingRequest) {
        if (mainWordCountService.handlePayload(incomingRequest)) {
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred while counting words.");

    }

    /**
     * Recevies the required word and retrieves its number of occurrences.
     * @param word to retrieve its count.
     * @return 200
     */
    @GetMapping(value = "/wordStatistic/{word}")
    public int getStatistic(@PathVariable String word) {
        return wordStatistecService.getWordOccurrences(word);

    }
}
