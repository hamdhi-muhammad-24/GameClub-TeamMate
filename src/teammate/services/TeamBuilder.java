package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.util.*;

public class TeamBuilder {

    private static final int MAX_PER_GAME = 2;      // Max 2 players from same game
    private static final int MIN_ROLES = 3;         // Minimum 3 unique roles per team
    private static final int MAX_THINKERS = 2;      // Max 2 thinkers per team
    private static final int REQUIRED_LEADERS = 1;  // Exactly 1 Leader per team

    public Map<String, Object> formTeamsWithLeftovers(List<Participant> participants, int teamSize) {

        Map<String, Object> result = new HashMap<>();
        List<Participant> leftover = new ArrayList<>();

        // STEP 1: Separate by personality type
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

        // Randomize lists
        Collections.shuffle(leaders);
        Collections.shuffle(thinkers);
        Collections.shuffle(balanced);

        // STEP 2: Determine number of full teams
        int fullTeams = participants.size() / teamSize;
        List<Team> teams = new ArrayList<>();

        for (int i = 0; i < fullTeams; i++) {
            teams.add(new Team());
        }

        // STEP 3: Assign exactly one Leader per team
        for (int i = 0; i < fullTeams; i++) {
            if (i < leaders.size()) {
                teams.get(i).addMember(leaders.get(i));
            }
        }

        // Extra Leaders go to leftover
        if (leaders.size() > fullTeams) {
            leftover.addAll(leaders.subList(fullTeams, leaders.size()));
        }

        // STEP 4: Assign 1 Thinker first
        for (int i = 0; i < fullTeams; i++) {
            if (i < thinkers.size()) {
                teams.get(i).addMember(thinkers.get(i));
            }
        }

        // STEP 5: Add remaining Thinkers (strict max 2)
        List<Participant> remainingThinkers = new ArrayList<>();
        if (thinkers.size() > fullTeams) {
            remainingThinkers.addAll(thinkers.subList(fullTeams, thinkers.size()));
        }

        Collections.shuffle(remainingThinkers);

        for (Participant th : remainingThinkers) {
            boolean placed = false;
            for (Team t : teams) {
                if (countThinkers(t) < MAX_THINKERS && t.getMembers().size() < teamSize) {
                    t.addMember(th);
                    placed = true;
                    break;
                }
            }
            if (!placed) leftover.add(th);
        }

        // STEP 6: Add Balanced participants & enforce constraints
        List<Participant> remaining = new ArrayList<>(balanced);
        Collections.shuffle(remaining);

        for (Participant p : remaining) {
            Team team = findValidTeam(p, teams, teamSize);
            if (team != null) {
                team.addMember(p);
            } else {
                leftover.add(p);
            }
        }

        // STEP 7: Final pass for role diversity
        enforceMinimumRoleDiversity(teams, leftover);

        result.put("teams", teams);
        result.put("leftover", leftover);

        return result;
    }

    // ---------------- VALID TEAM CHECK ----------------

    private Team findValidTeam(Participant p, List<Team> teams, int teamSize) {
        Team best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Team t : teams) {

            if (t.getMembers().size() >= teamSize)
                continue;

            if (isLeader(p) && countLeaders(t) >= REQUIRED_LEADERS)
                continue;

            if (isThinker(p) && countThinkers(t) >= MAX_THINKERS)
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

    private boolean isLeader(Participant p) {
        return p.getPersonalityType().equals("Leader");
    }

    private boolean isThinker(Participant p) {
        return p.getPersonalityType().equals("Thinker");
    }

    // ---------------- ROLE DIVERSITY ----------------

    private boolean helpsRoleDiversity(Team t, Participant p) {
        Set<String> roles = new HashSet<>();
        for (Participant m : t.getMembers()) roles.add(m.getRole());

        if (roles.size() >= MIN_ROLES) return true;
        return !roles.contains(p.getRole());
    }

    // ---------------- COUNT HELPERS ----------------

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

    // ---------------- SKILL BALANCE ----------------

    private int evaluateSkillFit(Team t, Participant p) {
        int avg = (int) t.getMembers().stream()
                .mapToInt(Participant::getSkillLevel)
                .average().orElse(50);
        return Math.abs(avg - p.getSkillLevel()) / 5;
    }

    // ---------------- FIX ROLE DIVERSITY IF NEEDED ----------------

    private void enforceMinimumRoleDiversity(List<Team> teams, List<Participant> leftover) {
        for (Team t : teams) {
            Set<String> roles = new HashSet<>();
            for (Participant m : t.getMembers()) roles.add(m.getRole());

            if (roles.size() < MIN_ROLES) {
                Iterator<Participant> itr = leftover.iterator();
                while (itr.hasNext() && roles.size() < MIN_ROLES) {
                    Participant p = itr.next();
                    if (!roles.contains(p.getRole())) {
                        t.addMember(p);
                        roles.add(p.getRole());
                        itr.remove();
                    }
                }
            }
        }
    }
}
