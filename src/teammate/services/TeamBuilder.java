package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.util.*;

public class TeamBuilder {

    public Map<String, Object> formTeamsWithLeftovers(List<Participant> participants, int teamSize) {

        // RESULT MAP
        Map<String, Object> result = new HashMap<>();

        // LEFTOVER LIST
        List<Participant> leftover = new ArrayList<>();

        // ---- Step 1: Separate by personality ----
        List<Participant> leaders = new ArrayList<>();
        List<Participant> thinkers = new ArrayList<>();
        List<Participant> balanced = new ArrayList<>();

        for (Participant p : participants) {
            switch (p.getPersonalityType()) {
                case "Leader":
                    leaders.add(p);
                    break;
                case "Thinker":
                    thinkers.add(p);
                    break;
                default:
                    balanced.add(p);
            }
        }

        // ---- Step 2: Shuffle for fairness ----
        Collections.shuffle(leaders);
        Collections.shuffle(thinkers);
        Collections.shuffle(balanced);

        // ---- Step 3: Calculate total FULL teams ----
        int fullTeams = participants.size() / teamSize;

        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < fullTeams; i++) {
            teams.add(new Team());
        }

        // ---- Step 4: Assign ONE leader per team ----
        for (int i = 0; i < fullTeams && i < leaders.size(); i++) {
            teams.get(i).addMember(leaders.get(i));
        }

        // ---- Step 5: Assign ONE thinker per team ----
        for (int i = 0; i < fullTeams && i < thinkers.size(); i++) {
            teams.get(i).addMember(thinkers.get(i));
        }

        // ---- Step 6: Collect remaining participants ----
        List<Participant> remaining = new ArrayList<>();
        remaining.addAll(balanced);

        if (thinkers.size() > fullTeams)
            remaining.addAll(thinkers.subList(fullTeams, thinkers.size()));

        if (leaders.size() > fullTeams)
            remaining.addAll(leaders.subList(fullTeams, leaders.size()));

        Collections.shuffle(remaining);

        // ---- Step 7: Fill teams EXACTLY to team size ----
        for (Participant p : remaining) {
            Team bestTeam = getBestTeamForParticipant(teams, p, teamSize);

            if (bestTeam != null) {
                bestTeam.addMember(p);
            } else {
                leftover.add(p); // If all teams are full, add to leftover
            }
        }

        // Store results
        result.put("teams", teams);
        result.put("leftover", leftover);

        return result;
    }


    private Team getBestTeamForParticipant(List<Team> teams, Participant p, int teamSize) {

        Team best = null;
        int minScore = Integer.MAX_VALUE;

        for (Team t : teams) {
            if (t.getMembers().size() >= teamSize)
                continue; // skip FULL teams

            int score = evaluateTeamFit(t, p);

            if (score < minScore) {
                minScore = score;
                best = t;
            }
        }
        return best;
    }

    private int evaluateTeamFit(Team t, Participant p) {

        int score = 0;

        long sameGameCount = t.getMembers().stream()
                .filter(m -> m.getGame().equalsIgnoreCase(p.getGame()))
                .count();
        if (sameGameCount >= 2) score += 5;

        boolean roleExists = t.getMembers().stream()
                .anyMatch(m -> m.getRole().equalsIgnoreCase(p.getRole()));
        if (roleExists) score += 3;

        long leaders = t.getMembers().stream()
                .filter(m -> m.getPersonalityType().equals("Leader"))
                .count();
        if (leaders >= 1 && p.getPersonalityType().equals("Leader"))
            score += 6;

        int avgSkill = (int) t.getMembers().stream()
                .mapToInt(Participant::getSkillLevel)
                .average()
                .orElse(50);

        score += Math.abs(avgSkill - p.getSkillLevel()) / 5;

        return score;
    }
}
