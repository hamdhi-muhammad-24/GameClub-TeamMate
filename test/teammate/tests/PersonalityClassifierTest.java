package teammate.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import teammate.services.PersonalityClassifier;
import teammate.models.Participant;

public class PersonalityClassifierTest {

    @Test
    void testLeaderClassification() {
        PersonalityClassifier classifier = new PersonalityClassifier();

        Participant p1 = new Participant("P1", "A", "a@university.edu",
                "Valorant", "Attacker", 5, 95, "");

        classifier.classify(p1);
        assertEquals("Leader", p1.getPersonalityType());
    }

    @Test
    void testBalancedClassification() {
        PersonalityClassifier classifier = new PersonalityClassifier();

        Participant p2 = new Participant("P2", "B", "b@university.edu",
                "FIFA", "Support", 4, 75, "");

        classifier.classify(p2);
        assertEquals("Balanced", p2.getPersonalityType());
    }

    @Test
    void testThinkerClassification() {
        PersonalityClassifier classifier = new PersonalityClassifier();

        Participant p3 = new Participant("P3", "C", "c@university.edu",
                "Chess", "Strategist", 3, 40, "");

        classifier.classify(p3);
        assertEquals("Thinker", p3.getPersonalityType());
    }
}
