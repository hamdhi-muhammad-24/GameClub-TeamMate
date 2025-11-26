package teammate.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import teammate.models.Participant;
import teammate.services.PersonalityClassifier;

public class PersonalityClassifierTest {

    @Test
    void testLeaderClassification() {
        PersonalityClassifier classifier = new PersonalityClassifier();

        Participant p1 = new Participant("A", "a@mail.com", "Valorant", "Attacker", 95);
        classifier.classify(p1);
        assertEquals("Leader", p1.getPersonalityType());

        Participant p2 = new Participant("B", "b@mail.com", "Dota", "Support", 100);
        classifier.classify(p2);
        assertEquals("Leader", p2.getPersonalityType());
    }

    @Test
    void testBalancedClassification() {
        PersonalityClassifier classifier = new PersonalityClassifier();

        Participant p1 = new Participant("X", "x@mail.com", "FIFA", "Defender", 70);
        classifier.classify(p1);
        assertEquals("Balanced", p1.getPersonalityType());

        Participant p2 = new Participant("Y", "y@mail.com", "Dota", "Strategist", 80);
        classifier.classify(p2);
        assertEquals("Balanced", p2.getPersonalityType());
    }

    @Test
    void testThinkerClassification() {
        PersonalityClassifier classifier = new PersonalityClassifier();

        Participant p1 = new Participant("Z", "z@mail.com", "Chess", "Thinker", 40);
        classifier.classify(p1);
        assertEquals("Thinker", p1.getPersonalityType());
    }
}
