package teammate.threads;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.TeamBuilder;

import java.util.List;
import java.util.Map;

public class TeamBuilderThread extends Thread {

    private List<Participant> participants;
    private int teamSize;
    private TeamBuilder builder;

    private List<Team> finalTeams;
    private List<Participant> leftover;

    public TeamBuilderThread(List<Participant> participants, int teamSize, TeamBuilder builder) {
        this.participants = participants;
        this.teamSize = teamSize;
        this.builder = builder;
    }

    public List<Team> getTeams() {
        return finalTeams;
    }

    public List<Participant> getLeftover() {
        return leftover;
    }

    @Override
    public void run() {
        Map<String, Object> result = builder.formTeamsWithLeftovers(participants, teamSize);

        finalTeams = (List<Team>) result.get("teams");
        leftover   = (List<Participant>) result.get("leftover");
    }
}
