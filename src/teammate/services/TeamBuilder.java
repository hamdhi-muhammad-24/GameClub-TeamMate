package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.util.*;

public class TeamBuilder {

    private static final int MAX_PER_GAME = 2;
    private static final int MIN_ROLES = 3;
    private static final int MAX_THINKERS = 2;
    private static final int REQUIRED_LEADERS = 1;

    // ============================================================
    // MAIN METHOD: Build ALL team categories
    // ============================================================
    public Map<String, Object> formAllTeams(List<Participant> participants, int teamSize) {

        Map<String, Object> result = new HashMap<>();

        // ===============================
        // STEP 1 — FORM WELL-BALANCED TEAMS
        // ===============================
        Map<String, Object> balancedResult = formWellBalancedTeams(participants, teamSize);

        List<Team> wellBalanced = (List<Team>) balancedResult.get("teams");
        List<Participant> leftover = (List<Participant>) balancedResult.get("leftover");

        // ===============================
        // STEP 2 — FORM SECONDARY TEAMS
        // (Random grouping, flexible rules)
        // ===============================
        List<Team> secondaryTeams = formSecondaryTeams(leftover, teamSize);

        // Remove secondary members from leftover
        removeUsedParticipants(leftover, secondaryTeams);

        // ===============================
        // FINAL RESULT
        // ===============================
        result.put("wellBalanced", wellBalanced);
        result.put("secondary", secondaryTeams);
        result.put("leftover", leftover);

        return result;
    }


    // ============================================================
    // PART 1 — WELL-BALANCED TEAMS (STRICT)
    // ============================================================
    private Map<String, Object> formWellBalancedTeams(List<Participant> participants, int teamSize) {

        Map<String, Object> result = new HashMap<>();
        List<Participant> leftover = new ArrayList<>();

        List<Team> teams = new ArrayList<>();

        List<Participant> leaders = new ArrayList<>();
        List<Participant> thinkers = new ArrayList<>();
        List<Participant> balanced = new ArrayList<>();

        for (Participant p : participants) {
            switch (p.getPersonalityType()) {
                case "Leader": leaders.add(p); break;
                case "Thinker": thinkers.add(p); break;
                default: balanced.add(p);
            }
        }

        Collections.shuffle(leaders);
        Collections.shuffle(thinkers);
        Collections.shuffle(balanced);

        int fullTeams = participants.size() / teamSize;
        for (int i = 0; i < fullTeams; i++) teams.add(new Team());

        // ---------------- LEADERS ----------------
        for (int i = 0; i < fullTeams; i++) {
            if (i < leaders.size()) teams.get(i).addMember(leaders.get(i));
        }
        if (leaders.size() > fullTeams)
            leftover.addAll(leaders.subList(fullTeams, leaders.size()));

        // ---------------- THINKERS ----------------
        for (int i = 0; i < fullTeams; i++) {
            if (i < thinkers.size()) teams.get(i).addMember(thinkers.get(i));
        }

        List<Participant> extraThinkers = new ArrayList<>();
        if (thinkers.size() > fullTeams)
            extraThinkers.addAll(thinkers.subList(fullTeams, thinkers.size()));

        Collections.shuffle(extraThinkers);
        for (Participant th : extraThinkers) {
            boolean placed = false;
            for (Team t : teams) {
                if (t.getMembers().size() < teamSize &&
                        countThinkers(t) < MAX_THINKERS) {
                    t.addMember(th);
                    placed = true;
                    break;
                }
            }
            if (!placed) leftover.add(th);
        }

        // ---------------- BALANCED ----------------
        List<Participant> remaining = new ArrayList<>(balanced);
        Collections.shuffle(remaining);

        for (Participant p : remaining) {
            Team team = findValidTeam(p, teams, teamSize);
            if (team != null) team.addMember(p);
            else leftover.add(p);
        }

        enforceMinimumRoleDiversity(teams, leftover, teamSize);

        // Remove incomplete teams
        for (Iterator<Team> it = teams.iterator(); it.hasNext();) {
            Team t = it.next();
            if (t.getMembers().size() < teamSize) {
                leftover.addAll(t.getMembers());
                it.remove();
            }
        }

        result.put("teams", teams);
        result.put("leftover", leftover);
        return result;
    }


    // ============================================================
    // PART 2 — SECONDARY TEAMS (Quick grouping, random)
    // ============================================================
    private List<Team> formSecondaryTeams(List<Participant> leftover, int teamSize) {
        List<Team> secondary = new ArrayList<>();

        List<Participant> temp = new ArrayList<>(leftover);
        Collections.shuffle(temp);

        while (temp.size() >= teamSize) {
            List<Participant> teamMembers = new ArrayList<>(temp.subList(0, teamSize));
            secondary.add(new Team(teamMembers));
            temp.subList(0, teamSize).clear();
        }
        return secondary;
    }


    // ============================================================
    // VALID TEAM CHECK FOR WELL-BALANCED TEAMS
    // ============================================================
    private Team findValidTeam(Participant p, List<Team> teams, int teamSize) {

        Team best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Team t : teams) {

            if (t.getMembers().size() >= teamSize)
                continue;

            if (p.getPersonalityType().equals("Leader") &&
                    countLeaders(t) >= REQUIRED_LEADERS)
                continue;

            if (p.getPersonalityType().equals("Thinker") &&
                    countThinkers(t) >= MAX_THINKERS)
                continue;

            if (countGame(t, p.getGame()) >= MAX_PER_GAME)
                continue;

            if (!helpsRoleDiversity(t, p))
                continue;

            int score = evaluateSkillFit(t, p);
            if (score < bestScore) {
                bestScore = score;
                best = t;
            }
        }
        return best;
    }

    // ============================================================
    // ROLE DIVERSITY HELPER
    // ============================================================
    private boolean helpsRoleDiversity(Team t, Participant p) {
        Set<String> roles = new HashSet<>();
        for (Participant m : t.getMembers()) roles.add(m.getRole());
        if (roles.size() >= MIN_ROLES) return true;
        return !roles.contains(p.getRole());
    }

    private void enforceMinimumRoleDiversity(List<Team> teams, List<Participant> leftover, int teamSize) {
        for (Team t : teams) {
            if (t.getMembers().size() == teamSize) {
                Set<String> roles = new HashSet<>();
                for (Participant m : t.getMembers()) roles.add(m.getRole());
                if (roles.size() < MIN_ROLES) {

                    Iterator<Participant> it = leftover.iterator();
                    while (it.hasNext() && roles.size() < MIN_ROLES) {
                        Participant p = it.next();
                        if (!roles.contains(p.getRole())) {
                            t.addMember(p);
                            roles.add(p.getRole());
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    // ============================================================
    // COUNT HELPERS
    // ============================================================
    private int countLeaders(Team t) {
        return (int) t.getMembers().stream()
                .filter(m -> m.getPersonalityType().equals("Leader"))
                .count();
    }

    private int countThinkers(Team t) {
        return (int) t.getMembers().stream()
                .filter(m -> m.getPersonalityType().equals("Thinker"))
                .count();
    }

    private int countGame(Team t, String game) {
        return (int) t.getMembers().stream()
                .filter(m -> m.getGame().equalsIgnoreCase(game))
                .count();
    }

    private int evaluateSkillFit(Team t, Participant p) {
        int avg = (int) t.getMembers().stream()
                .mapToInt(Participant::getSkillLevel)
                .average().orElse(50);
        return Math.abs(avg - p.getSkillLevel()) / 5;
    }

    // ============================================================
    // REMOVE USED PARTICIPANTS
    // ============================================================
    private void removeUsedParticipants(List<Participant> leftover, List<Team> teams) {
        for (Team t : teams) {
            for (Participant p : t.getMembers()) {
                leftover.remove(p);
            }
        }
    }
}
