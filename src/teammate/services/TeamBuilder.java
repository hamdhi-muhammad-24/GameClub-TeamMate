package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamBuilder {

    public List<Team> formTeams(List<Participant> participants, int teamSize) {

        Collections.shuffle(participants); // fairness

        List<Team> teams = new ArrayList<>();
        int index = 0;

        while (index < participants.size()) {
            Team team = new Team();

            for (int i = 0; i < teamSize && index < participants.size(); i++) {
                team.addMember(participants.get(index));
                index++;
            }

            teams.add(team);
        }

        return teams;
    }
}
