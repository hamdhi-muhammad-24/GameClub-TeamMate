package teammate.threads;

import teammate.models.Team;
import teammate.models.Participant;
import teammate.services.TeamBuilder;

import java.util.List;

public class TeamBuilderThread extends Thread {

    private List<Participant> participants;
    private int teamSize;
    private List<Team> result;

    public TeamBuilderThread(List<Participant> participants, int teamSize) {
        this.participants = participants;
        this.teamSize = teamSize;
    }

    public List<Team> getResult() {
        return result;
    }

    @Override
    public void run() {
        TeamBuilder builder = new TeamBuilder();
        result = builder.formTeams(participants, teamSize);

        System.out.println("Team formation completed.");
    }
}
