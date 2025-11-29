package teammate.services;

import teammate.models.Participant;
import teammate.utils.LoggerUtil;

import java.util.logging.Logger;

public class PersonalityClassifier {

    private static final Logger log = LoggerUtil.getLogger();

    // ======================================================
    // CLASSIFY PERSONALITY BASED ON SCORE
    // ======================================================
    public void classify(Participant p) {

        int score = p.getPersonalityScore();

        log.fine("Classifying personality for participant: " + p.getName() + " | Score: " + score);

        if (score < 0 || score > 100) {
            log.warning("Unusual personality score detected (" + score + ") for participant: " + p.getName());
        }

        if (score >= 90) {
            p.setPersonalityType("Leader");
            log.info("Participant " + p.getName() + " assigned as LEADER.");
        }

        else if (score >= 70) {
            p.setPersonalityType("Balanced");
            log.info("Participant " + p.getName() + " assigned as BALANCED.");
        }

        else {
            p.setPersonalityType("Thinker");
            log.info("Participant " + p.getName() + " assigned as THINKER.");
        }
    }
}
