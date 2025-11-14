package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.util.*;

public class TeamBuilder {

    public List<Team> formBalancedTeams(List<Participant> participants, int teamSize) {

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

        // ---- Step 3: Create empty teams ----
        int totalTeams = (int) Math.ceil((double) participants.size() / teamSize);
        List<Team> teams = new ArrayList<>();

        for (int i = 0; i < totalTeams; i++) {
            teams.add(new Team());
        }

        // ---- Step 4: Assign ONE leader per team ----
        for (int i = 0; i < totalTeams && i < leaders.size(); i++) {
            teams.get(i).addMember(leaders.get(i));
        }

        // ---- Step 5: Assign ONE thinker per team ----
        for (int i = 0; i < totalTeams && i < thinkers.size(); i++) {
            teams.get(i).addMember(thinkers.get(i));
        }

        // ---- Step 6: Fill remaining slots with Balanced type ----
        List<Participant> remaining = new ArrayList<>();
        remaining.addAll(balanced);

        // Add extra thinkers (if any)
        if (thinkers.size() > totalTeams) {
            remaining.addAll(thinkers.subList(totalTeams, thinkers.size()));
        }

        // Add extra leaders (if any)
        if (leaders.size() > totalTeams) {
            remaining.addAll(leaders.subList(totalTeams, leaders.size()));
        }

        // Shuffle remaining for fairness
        Collections.shuffle(remaining);

        // ---- Step 7: Now apply role + game balancing constraints ----
        for (Participant p : remaining) {
            Team bestTeam = getBestTeamForParticipant(teams, p, teamSize);
            bestTeam.addMember(p);
        }

        return teams;
    }


    // Method to choose best team for the participant based on constraints
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

    // Lower score = better fit
    private int evaluateTeamFit(Team t, Participant p) {

        int score = 0;

        // --- Game diversity: penalize if too many same game ---
        long sameGameCount = t.getMembers().stream()
                .filter(m -> m.getGame().equalsIgnoreCase(p.getGame()))
                .count();

        if (sameGameCount >= 2)
            score += 5;

        // --- Role diversity ---
        boolean roleExists = t.getMembers().stream()
                .anyMatch(m -> m.getRole().equalsIgnoreCase(p.getRole()));

        if (roleExists)
            score += 3;

        // --- Personality balance ---
        long leaders = t.getMembers().stream()
                .filter(m -> m.getPersonalityType().equals("Leader"))
                .count();

        if (leaders >= 1 && p.getPersonalityType().equals("Leader"))
            score += 6;

        // --- Skill balancing: penalize if average differs too much ---
        int avgSkill = (int) t.getMembers().stream()
                .mapToInt(Participant::getSkillLevel)
                .average()
                .orElse(50);

        score += Math.abs(avgSkill - p.getSkillLevel()) / 5;

        return score;
    }
}
