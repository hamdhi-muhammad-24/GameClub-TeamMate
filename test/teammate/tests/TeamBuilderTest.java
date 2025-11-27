package teammate.tests;

import org.junit.jupiter.api.Test;
import teammate.services.TeamBuilder;
import teammate.models.Participant;
import teammate.models.Team;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TeamBuilderTest {

    @Test
    void testTeamFormationCreatesTeams() {
        TeamBuilder builder = new TeamBuilder();

        List<Participant> sample = List.of(
                new Participant("P1", "A", "a@university.edu", "Valorant", "Attacker", 5, 95, "Leader"),
                new Participant("P2", "B", "b@university.edu", "FIFA", "Supporter", 6, 80, "Balanced"),
                new Participant("P3", "C", "c@university.edu", "Chess", "Strategist", 7, 60, "Thinker")
        );

        Map<String, Object> wb = builder.formWellBalancedTeams(sample, 3);

        assertNotNull(wb);

        List<Team> teams = (List<Team>) wb.get("teams");
        assertNotNull(teams);

        // Expect exactly 1 team with size 3
        assertEquals(1, teams.size());
        assertEquals(3, teams.get(0).getMembers().size());

        // leftover should be empty
        List<Participant> leftover = (List<Participant>) wb.get("leftover");
        assertEquals(0, leftover.size());
    }

}
