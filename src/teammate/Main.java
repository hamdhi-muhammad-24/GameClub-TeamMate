package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.threads.SurveyProcessorThread;
import teammate.threads.TeamBuilderThread;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            CSVHandler handler = new CSVHandler();

            // 1. Load participants
            List<Participant> participants =
                    handler.loadParticipants("participants_sample.csv");

            // 2. Process personality using a thread
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();

            // 3. Build teams in parallel
            TeamBuilderThread t2 = new TeamBuilderThread(participants, 4);
            t2.start();
            t2.join();

            // 4. Save teams to CSV
            handler.saveTeams(t2.getResult(), "formed_teams.csv");

            System.out.println("All tasks completed.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
