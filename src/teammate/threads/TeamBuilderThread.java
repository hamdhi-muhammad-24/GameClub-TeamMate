package teammate.threads;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.TeamBuilder;

import java.util.List;

public class TeamBuilderThread extends Thread {

    private List<Participant> participants;
    private int teamSize;
    private List<Team> result;
    private TeamBuilder builder;

    public TeamBuilderThread(List<Participant> participants, int teamSize, TeamBuilder builder) {
        this.participants = participants;
        this.teamSize = teamSize;
        this.builder = builder;
    }

    public List<Team> getResult() {
        return result;
    }

    @Override
    public void run() {
        result = builder.formBalancedTeams(participants, teamSize);
    }
}
