package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.services.TeamBuilder;
import teammate.threads.SurveyProcessorThread;
import teammate.threads.TeamBuilderThread;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("====== TeamMate: Team Formation System ======");

            // 1. Ask organizer for team size
            System.out.print("Enter desired team size (N): ");
            int teamSize = scanner.nextInt();

            // 2. Load CSV data
            CSVHandler handler = new CSVHandler();
            List<Participant> participants =
                    handler.loadParticipants("participants_sample.csv");

            System.out.println("\nLoaded " + participants.size() + " participants.");
            System.out.println("Processing personality data...\n");

            // 3. Process personalities in a thread
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();   // wait for personality processing to finish

            // 4. Build balanced teams using advanced algorithm
            System.out.println("Forming balanced teams...\n");
            TeamBuilder builder = new TeamBuilder();

            // Use TeamBuilderThread to run team building in parallel
            TeamBuilderThread t2 = new TeamBuilderThread(participants, teamSize, builder);
            t2.start();
            t2.join();   // wait until teams are formed

            List<Team> teams = t2.getResult();

            if (teams == null) {
                System.out.println("❌ Error: Teams could not be formed.");
                return;
            }

            // 5. Print teams to console
            System.out.println("====== Final Teams ======\n");
            int teamNumber = 1;
            for (Team team : teams) {
                System.out.println("Team " + teamNumber + ":");
                for (Participant p : team.getMembers()) {
                    System.out.println(" - " + p);
                }
                System.out.println();
                teamNumber++;
            }

            // 6. Save to CSV
            handler.saveTeams(teams, "formed_teams.csv");
            System.out.println("✔ Teams saved to -> formed_teams.csv");

            System.out.println("\nAll tasks completed successfully!");

        } catch (Exception e) {
            System.out.println("\n❌ An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
