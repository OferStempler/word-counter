package ofer.stempler.word.counter.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ofer.stempler.word.counter.models.IncomingRequest;
import ofer.stempler.word.counter.models.PayloadType;

@Service
public class MainWordCountService {

    private static final String URL_REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    @Autowired
    private List<WordCounterService> services;

    private static final Map<PayloadType, WordCounterService> servicesMap = new HashMap<>();

    /**
     * Initialize the service map.
     * This will later provide the required service based on the input type resolved.
     */
    @PostConstruct
    public void initServiceMap(){

        for(WordCounterService service : services) {
            servicesMap.put(service.getServiceType(), service);
        }
    }

    /**
     * Initiates the correct service according to the request payload type.
     * @param incomingRequest
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean handlePayload(IncomingRequest incomingRequest) {
        try {
            String input = incomingRequest.getInput();
            PayloadType payloadType = resolveInputType(input);
            servicesMap.get(payloadType).handleText(input);
            return true;
        } catch (Exception e) {
            System.out.println("Process finished with error.");
            e.printStackTrace(System.out);
            return false;
        }
    }

    /**
     * Resolve the input type.
     * @param input from payload.
     * @return the Payload type.
     */
    private PayloadType resolveInputType(String input) {
        PayloadType payloadType = PayloadType.TEXT; //default

        Pattern urlRegex = Pattern.compile(URL_REGEX);
        if (urlRegex.matches(URL_REGEX, input)){
            payloadType = PayloadType.URL;

        } else if (new File(input).isFile()){
            payloadType =  PayloadType.PATH;
        }

        System.out.println("Incoming request input type is: " + payloadType);
        return payloadType;
    }


}
