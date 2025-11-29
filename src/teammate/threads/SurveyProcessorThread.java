package teammate.threads;

import teammate.models.Participant;
import teammate.services.PersonalityClassifier;
import teammate.utils.LoggerUtil;

import java.util.List;
import java.util.logging.Logger;

public class SurveyProcessorThread extends Thread {

    private static final Logger log = LoggerUtil.getLogger();
    private final List<Participant> participants;

    public SurveyProcessorThread(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public void run() {

        log.info("SurveyProcessorThread started. Total participants = " + participants.size());

        try {
            PersonalityClassifier classifier = new PersonalityClassifier();

            for (Participant p : participants) {
                log.fine("Processing survey data for participant: " + p.getName());
                classifier.classify(p);
            }

            log.info("SurveyProcessorThread completed successfully.");

        } catch (Exception e) {
            log.severe("Error in SurveyProcessorThread: " + e.getMessage());
        }
    }
}
