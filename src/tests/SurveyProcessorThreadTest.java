package tests;

import org.junit.jupiter.api.Test;
import teammate.threads.SurveyProcessorThread;
import teammate.models.Participant;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SurveyProcessorThreadTest {

    @Test
    void testProcessorThreadRuns() throws Exception {
        List<Participant> sample = List.of(
                new Participant("P1", "A", "a@university.edu", "Valorant", "Attacker", 5, 50, ""),
                new Participant("P2", "B", "b@university.edu", "FIFA", "Support", 6, 80, "")
        );

        SurveyProcessorThread t = new SurveyProcessorThread(sample);
        t.start();
        t.join();

        assertNotNull(sample.get(0).getPersonalityType());
        assertNotNull(sample.get(1).getPersonalityType());
    }
}
