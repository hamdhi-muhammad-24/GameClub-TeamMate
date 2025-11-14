package teammate.models;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private List<Participant> members = new ArrayList<>();

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
