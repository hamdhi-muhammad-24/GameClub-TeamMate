package teammate.tests;

import org.junit.jupiter.api.Test;
import teammate.threads.TeamBuilderThread;
import teammate.services.TeamBuilder;
import teammate.models.Participant;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeamBuilderThreadTest {

    @Test
    void testThreadBuildsTeams() throws Exception {
        List<Participant> sample = List.of(
                new Participant("P1", "A", "a@university.edu", "Valorant", "Attacker", 5, 95, "Leader"),
                new Participant("P2", "B", "b@university.edu", "FIFA", "Support", 6, 80, "Balanced"),
                new Participant("P3", "C", "c@university.edu", "Chess", "Strategist", 7, 60, "Thinker")
        );

        TeamBuilderThread t = new TeamBuilderThread(sample, 3, new TeamBuilder());
        t.start();
        t.join();

        assertEquals(1, t.getWellBalancedTeams().size());
    }
}
