package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.services.TeamBuilder;
import teammate.threads.SurveyProcessorThread;
import teammate.threads.TeamBuilderThread;
import teammate.exceptions.InvalidDataException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println("========================================");
            System.out.println("   TeamMate: Team Formation System");
            System.out.println("========================================");

            Scanner scanner = new Scanner(System.in);

            // ------------------------------
            // 1. Get team size from organizer
            // ------------------------------
            System.out.print("\nEnter desired team size (N): ");

            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Team size must be a number.");
            }

            int teamSize = scanner.nextInt();

            if (teamSize <= 1) {
                throw new IllegalArgumentException("Team size must be greater than 1.");
            }

            // ------------------------------
            // 2. Load CSV file
            // ------------------------------
            CSVHandler handler = new CSVHandler();

            System.out.println("\nLoading participants from CSV...");
            List<Participant> participants =
                    handler.loadParticipants("participants_sample.csv");

            System.out.println("✔ Loaded " + participants.size() + " participants.");

            // ------------------------------
            // 3. Process personality (Thread 1)
            // ------------------------------
            System.out.println("\nProcessing personality types...");
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();

            System.out.println("✔ Personality processing completed.");

            // ------------------------------
            // 4. Form balanced teams (Thread 2)
            // ------------------------------
            System.out.println("\nForming balanced teams...");
            TeamBuilder builder = new TeamBuilder();

            TeamBuilderThread t2 = new TeamBuilderThread(participants, teamSize, builder);
            t2.start();
            t2.join();

            List<Team> teams = t2.getResult();

            if (teams == null || teams.isEmpty()) {
                throw new Exception("Team formation failed. No teams generated.");
            }

            // ------------------------------
            // 5. Display formed teams
            // ------------------------------
            System.out.println("\n=========== FINAL TEAMS ===========\n");
            int teamNum = 1;

            for (Team t : teams) {
                System.out.println("Team " + teamNum + ":");
                t.getMembers().forEach(member -> System.out.println(" - " + member));
                System.out.println();
                teamNum++;
            }

            // ------------------------------
            // 6. Save teams to CSV
            // ------------------------------
            System.out.println("Saving teams to formed_teams.csv...");
            handler.saveTeams(teams, "formed_teams.csv");

            System.out.println("✔ Teams saved successfully!");

            System.out.println("\n========================================");
            System.out.println("     All Tasks Completed Successfully!");
            System.out.println("========================================");

        }
        // ------------------------------
        // Exception Handling Section
        // ------------------------------

        catch (FileNotFoundException e) {
            System.out.println("❌ File Error: " + e.getMessage());
        }

        catch (InvalidDataException e) {
            System.out.println("❌ Invalid Data: " + e.getMessage());
        }

        catch (IllegalArgumentException e) {
            System.out.println("❌ Input Error: " + e.getMessage());
        }

        catch (InterruptedException e) {
            System.out.println("❌ Thread interrupted. Operation stopped.");
        }

        catch (IOException e) {
            System.out.println("❌ I/O Error: " + e.getMessage());
        }

        catch (Exception e) {
            System.out.println("❌ Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
