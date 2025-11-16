package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.util.*;

public class TeamBuilder {

    private static final int MAX_PER_GAME = 2;
    private static final int MIN_ROLES = 3;
    private static final int MAX_THINKERS = 2;
    private static final int REQUIRED_LEADERS = 1;

    // =============================================================
    // MAIN METHOD — FORMS FULL TEAMS + EXTRA TEAMS + LEFTOVER LIST
    // =============================================================
    public Map<String, Object> formTeamsWithLeftovers(List<Participant> participants, int teamSize) {

        Map<String, Object> result = new HashMap<>();
        List<Participant> leftover = new ArrayList<>();

        // ---------------------------------------------
        // STEP 1: Separate by personality
        // ---------------------------------------------
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

        // ---------------------------------------------
        // STEP 2: Create only FULL teams
        // ---------------------------------------------
        int fullTeams = participants.size() / teamSize;
        List<Team> teams = new ArrayList<>();

        for (int i = 0; i < fullTeams; i++)
            teams.add(new Team());

        // ---------------------------------------------
        // STEP 3: Assign EXACTLY 1 Leader per team
        // ---------------------------------------------
        for (int i = 0; i < fullTeams && i < leaders.size(); i++)
            teams.get(i).addMember(leaders.get(i));

        if (leaders.size() > fullTeams)
            leftover.addAll(leaders.subList(fullTeams, leaders.size()));

        // ---------------------------------------------
        // STEP 4: Assign 1 Thinker per team
        // ---------------------------------------------
        for (int i = 0; i < fullTeams && i < thinkers.size(); i++)
            teams.get(i).addMember(thinkers.get(i));

        // ---------------------------------------------
        // STEP 5: Extra thinkers (strict max)
        // ---------------------------------------------
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

        // ---------------------------------------------
        // STEP 6: Add Balanced participants to valid teams
        // ---------------------------------------------
        Collections.shuffle(balanced);

        for (Participant p : balanced) {
            Team team = findValidTeam(p, teams, teamSize);
            if (team != null) team.addMember(p);
            else leftover.add(p);
        }

        // ---------------------------------------------
        // STEP 7: Fix role diversity
        // ---------------------------------------------
        enforceMinimumRoleDiversity(teams, leftover, teamSize);

        // ---------------------------------------------
        // STEP 8: Remove incomplete teams (safety)
        // ---------------------------------------------
        for (Iterator<Team> it = teams.iterator(); it.hasNext(); ) {
            Team t = it.next();
            if (t.getMembers().size() < teamSize) {
                leftover.addAll(t.getMembers());
                it.remove();
            }
        }

        // ---------------------------------------------
        // STEP 9: BUILD EXTRA TEAMS FROM LEFTOVER (Pattern A/B)
        // ---------------------------------------------
        List<Team> extraTeams = formExtraTeamsFromLeftover(leftover);
        teams.addAll(extraTeams);

        // Remove used participants from leftover
        removeUsedParticipants(leftover, extraTeams);

        // RETURN
        result.put("teams", teams);
        result.put("leftover", leftover);
        return result;
    }


    // =============================================================
    // FORM EXTRA TEAMS FROM LEFTOVER
    // =============================================================
    private List<Team> formExtraTeamsFromLeftover(List<Participant> leftover) {

        List<Team> extraTeams = new ArrayList<>();

        while (true) {
            Team newTeam = tryBuildTeam(leftover);

            if (newTeam == null)
                break;

            extraTeams.add(newTeam);
        }

        return extraTeams;
    }


    // Try building ONE leftover team using Pattern A or B
    private Team tryBuildTeam(List<Participant> leftover) {

        List<Participant> leaders = new ArrayList<>();
        List<Participant> thinkers = new ArrayList<>();
        List<Participant> balanced = new ArrayList<>();

        for (Participant p : leftover) {
            switch (p.getPersonalityType()) {
                case "Leader": leaders.add(p); break;
                case "Thinker": thinkers.add(p); break;
                default: balanced.add(p);
            }
        }

        Collections.shuffle(leaders);
        Collections.shuffle(thinkers);
        Collections.shuffle(balanced);

        // ---------------------------------------------
        // PATTERN A — 1 Leader, 2 Thinker, 2 Balanced
        // ---------------------------------------------
        if (leaders.size() >= 1 && thinkers.size() >= 2 && balanced.size() >= 2) {

            List<Participant> members = Arrays.asList(
                    leaders.get(0),
                    thinkers.get(0),
                    thinkers.get(1),
                    balanced.get(0),
                    balanced.get(1)
            );

            if (isValidTeam(members))
                return new Team(members);
        }

        // ---------------------------------------------
        // PATTERN B — 1 Leader, 1 Thinker, 3 Balanced
        // ---------------------------------------------
        if (leaders.size() >= 1 && thinkers.size() >= 1 && balanced.size() >= 3) {

            List<Participant> members = Arrays.asList(
                    leaders.get(0),
                    thinkers.get(0),
                    balanced.get(0),
                    balanced.get(1),
                    balanced.get(2)
            );

            if (isValidTeam(members))
                return new Team(members);
        }

        return null;
    }


    // =============================================================
    // TEAM VALIDATION FOR LEFTOVER TEAMS
    // =============================================================
    private boolean isValidTeam(List<Participant> members) {

        if (members.size() != 5) return false;

        long leaders = members.stream().filter(p -> p.getPersonalityType().equals("Leader")).count();
        long thinkers = members.stream().filter(p -> p.getPersonalityType().equals("Thinker")).count();

        if (leaders != 1) return false;
        if (thinkers < 1 || thinkers > 2) return false;

        Set<String> roles = new HashSet<>();
        for (Participant p : members) roles.add(p.getRole());
        if (roles.size() < 3) return false;

        Map<String, Long> gameCounts = new HashMap<>();
        for (Participant p : members)
            gameCounts.put(p.getGame(), gameCounts.getOrDefault(p.getGame(), 0L) + 1);

        for (long c : gameCounts.values())
            if (c > 2) return false;

        return true;
    }


    // =============================================================
    // FIND VALID TEAM FOR BALANCED PARTICIPANTS (MAIN TEAMS)
    // =============================================================
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


    // =============================================================
    // ROLE DIVERSITY ENFORCEMENT
    // =============================================================
    private void enforceMinimumRoleDiversity(List<Team> teams,
                                             List<Participant> leftover,
                                             int teamSize) {

        for (Team t : teams) {

            if (t.getMembers().size() == teamSize) {

                Set<String> roles = new HashSet<>();
                for (Participant m : t.getMembers())
                    roles.add(m.getRole());

                if (roles.size() < MIN_ROLES) {

                    Iterator<Participant> it = leftover.iterator();

                    while (it.hasNext() && roles.size() < MIN_ROLES) {

                        Participant p = it.next();

                        if (!roles.contains(p.getRole()) &&
                                !p.getPersonalityType().equals("Leader") &&
                                !(p.getPersonalityType().equals("Thinker") &&
                                        countThinkers(t) >= MAX_THINKERS)) {

                            t.addMember(p);
                            roles.add(p.getRole());
                            it.remove();
                        }
                    }
                }
            }
        }
    }


    // =============================================================
    // UTILITY METHODS
    // =============================================================
    private boolean isLeader(Participant p) {
        return p.getPersonalityType().equals("Leader");
    }

    private boolean isThinker(Participant p) {
        return p.getPersonalityType().equals("Thinker");
    }

    private boolean helpsRoleDiversity(Team t, Participant p) {
        Set<String> roles = new HashSet<>();
        for (Participant m : t.getMembers())
            roles.add(m.getRole());

        return roles.size() >= MIN_ROLES || !roles.contains(p.getRole());
    }

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
                .average()
                .orElse(50);
        return Math.abs(avg - p.getSkillLevel()) / 5;
    }

    private void removeUsedParticipants(List<Participant> leftover, List<Team> extraTeams) {
        for (Team t : extraTeams)
            for (Participant p : t.getMembers())
                leftover.remove(p);
    }
}
