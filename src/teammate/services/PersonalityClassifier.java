package teammate.services;

import teammate.models.Participant;

public class PersonalityClassifier {

    public void classify(Participant p) {
        int score = p.getTotalPersonalityScore();

        if (score >= 90) p.setPersonalityType("Leader");
        else if (score >= 70) p.setPersonalityType("Balanced");
        else p.setPersonalityType("Thinker");
    }
}
