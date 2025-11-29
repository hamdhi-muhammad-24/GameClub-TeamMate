package teammate.threads;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.TeamBuilder;
import teammate.utils.LoggerUtil;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TeamBuilderThread extends Thread {

    private static final Logger log = LoggerUtil.getLogger();

    private final List<Participant> participants;
    private final int teamSize;
    private final TeamBuilder builder;

    private List<Team> wellBalanced;
    private List<Team> secondary;
    private List<Participant> leftover;

    public TeamBuilderThread(List<Participant> participants, int teamSize, TeamBuilder builder) {
        this.participants = participants;
        this.teamSize = teamSize;
        this.builder = builder;
    }

    public List<Team> getWellBalancedTeams() { return wellBalanced; }
    public List<Team> getSecondaryTeams()    { return secondary; }
    public List<Participant> getLeftover()   { return leftover; }

    @Override
    public void run() {

        log.info("TeamBuilderThread started. Team size = " + teamSize +
                ", Total participants = " + participants.size());

        try {
            Map<String, Object> result = builder.formAllTeams(participants, teamSize);

            wellBalanced = (List<Team>) result.get("wellBalanced");
            secondary    = (List<Team>) result.get("secondary");
            leftover     = (List<Participant>) result.get("leftover");

            log.info("TeamBuilderThread completed. " +
                    "WellBalanced=" + wellBalanced.size() +
                    ", Secondary=" + secondary.size() +
                    ", Leftover=" + leftover.size());

        } catch (Exception e) {
            log.severe("Error during team formation in TeamBuilderThread: " + e.getMessage());
        }
    }
}
