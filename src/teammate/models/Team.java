package teammate.models;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private List<Participant> members;
    private String teamType = "";   // WB / SC

    public Team() {
        this.members = new ArrayList<>();
    }

    public Team(List<Participant> members) {
        this.members = new ArrayList<>(members);
    }

    public void setTeamType(String type) {
        this.teamType = type;
    }

    public String getTeamType() {
        return teamType;
    }

    public void addMember(Participant p) {
        members.add(p);
    }

    public List<Participant> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        String header = teamType.isEmpty() ? "Team:" : teamType + " Team:";
        StringBuilder sb = new StringBuilder(header + "\n");
        for (Participant p : members) sb.append(" - ").append(p).append("\n");
        return sb.toString();
    }
}
