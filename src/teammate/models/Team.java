package teammate.models;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private List<Participant> members = new ArrayList<>();

    /** Default constructor */
    public Team() {
        this.members = new ArrayList<>();
    }

    /** New constructor to create a team from a list of participants */
    public Team(List<Participant> members) {
        this.members = new ArrayList<>(members);
    }

    public void addMember(Participant p) {
        members.add(p);
    }

    public List<Participant> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Team:\n");
        for (Participant p : members) sb.append(" - ").append(p).append("\n");
        return sb.toString();
    }
}
