package teammate.threads;

import teammate.models.Participant;
import teammate.services.PersonalityClassifier;

import java.util.List;

public class SurveyProcessorThread extends Thread {

    private List<Participant> participants;

    public SurveyProcessorThread(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public void run() {
        PersonalityClassifier classifier = new PersonalityClassifier();

        for (Participant p : participants) {
            classifier.classify(p);
        }

        System.out.println("Survey processing completed.");
    }
}
