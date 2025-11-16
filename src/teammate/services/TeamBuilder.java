package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.util.*;

public class TeamBuilder {

    private static final int MAX_PER_GAME = 2;
    private static final int MIN_ROLES = 3;

    public Map<String, Object> formTeamsWithLeftovers(List<Participant> participants, int teamSize) {

        Map<String, Object> result = new HashMap<>();
        List<Participant> leftover = new ArrayList<>();

        // ----- Step 1: Divide by personality -----
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

        Collections.shuffle(leaders);
        Collections.shuffle(thinkers);
        Collections.shuffle(balanced);

        // ----- Step 2: Create exact number of full teams -----
        int fullTeams = participants.size() / teamSize;

        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < fullTeams; i++) {
            teams.add(new Team());
        }

        // ----- Step 3: Assign leaders -----
        for (int i = 0; i < fullTeams && i < leaders.size(); i++) {
            teams.get(i).addMember(leaders.get(i));
        }

        // ----- Step 4: Assign thinkers -----
        for (int i = 0; i < fullTeams && i < thinkers.size(); i++) {
            teams.get(i).addMember(thinkers.get(i));
        }

        // ----- Step 5: Add remaining participants -----
        List<Participant> remaining = new ArrayList<>();
        remaining.addAll(balanced);

        if (thinkers.size() > fullTeams)
            remaining.addAll(thinkers.subList(fullTeams, thinkers.size()));

        if (leaders.size() > fullTeams)
            remaining.addAll(leaders.subList(fullTeams, leaders.size()));

        Collections.shuffle(remaining);

        // FIRST PASS – Strict rules when filling teams
        for (Participant p : remaining) {
            Team bestTeam = findStrictTeamPlacement(teams, p, teamSize);
            if (bestTeam != null) {
                bestTeam.addMember(p);
            } else {
                leftover.add(p);
            }
        }

        // SECOND PASS – Fix teams with <3 roles
        enforceMinimumRoleDiversity(teams, leftover);

        // Return final result
        result.put("teams", teams);
        result.put("leftover", leftover);
        return result;
    }

    // ---------------- STRICT TEAM PLACEMENT ----------------

    private Team findStrictTeamPlacement(List<Team> teams, Participant p, int teamSize) {

        Team best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Team t : teams) {

            if (t.getMembers().size() >= teamSize)
                continue; // full

            if (countGame(t, p.getGame()) >= MAX_PER_GAME)
                continue; // strict game cap

            if (!canHelpRoleDiversity(t, p))
                continue; // strict role diversity

            int score = evaluateTeamFit(t, p);

            if (score < bestScore) {
                best = t;
                bestScore = score;
            }
        }
        return best;
    }

    // ---------------- ROLE DIVERSITY LOGIC ----------------

    private boolean canHelpRoleDiversity(Team t, Participant p) {
        Set<String> roles = new HashSet<>();

        for (Participant m : t.getMembers()) {
            roles.add(m.getRole());
        }

        // Already 3+ roles → allow any placement
        if (roles.size() >= MIN_ROLES) return true;

        // Team has less than 3 roles → require NEW role
        return !roles.contains(p.getRole());
    }

    private int countGame(Team t, String game) {
        return (int) t.getMembers().stream()
                .filter(m -> m.getGame().equalsIgnoreCase(game))
                .count();
    }

    // ---------------- SKILL BALANCING LOGIC ----------------

    private int evaluateTeamFit(Team t, Participant p) {
        int score = 0;

        int avgSkill = (int) t.getMembers().stream()
                .mapToInt(Participant::getSkillLevel)
                .average()
                .orElse(50);

        score += Math.abs(avgSkill - p.getSkillLevel()) / 5;

        return score;
    }

    // ---------------- POST-FIX ROLE DIVERSITY ----------------

    private void enforceMinimumRoleDiversity(List<Team> teams, List<Participant> leftover) {

        for (Team t : teams) {

            Set<String> roles = new HashSet<>();
            for (Participant m : t.getMembers()) {
                roles.add(m.getRole());
            }

            // If team has <3 roles → try to fix
            if (roles.size() < MIN_ROLES) {

                List<Participant> mustAdd = new ArrayList<>();

                for (Participant p : leftover) {
                    if (!roles.contains(p.getRole())) {
                        mustAdd.add(p);
                        roles.add(p.getRole());
                        t.addMember(p);
                        if (roles.size() >= MIN_ROLES) break;
                    }
                }

                leftover.removeAll(mustAdd);
            }
        }
    }
}
