package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.services.TeamBuilder;
import teammate.threads.TeamBuilderThread;
import teammate.threads.SurveyProcessorThread;
import teammate.exceptions.InvalidDataException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("========================================");
            System.out.println("   TeamMate: Team Formation System");
            System.out.println("========================================");

            System.out.print("\nEnter desired team size (N): ");
            int teamSize = scanner.nextInt();

            CSVHandler handler = new CSVHandler();

            System.out.println("\nLoading participants...");
            List<Participant> participants =
                    handler.loadParticipants("Resources/participants_sample.csv");

            System.out.println("✔ Loaded " + participants.size() + " participants.");

            // Thread 1 — Personality classification
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();

            // Thread 2 — Team building
            TeamBuilder builder = new TeamBuilder();
            TeamBuilderThread t2 = new TeamBuilderThread(participants, teamSize, builder);
            t2.start();
            t2.join();

            List<Team> teams = t2.getTeams();
            List<Participant> leftover = t2.getLeftover();

            System.out.println("\n=========== FULL TEAMS ===========\n");
            int num = 1;
            for (Team t : teams) {
                System.out.println("Team " + num++);
                t.getMembers().forEach(System.out::println);
                System.out.println();
            }

            // Print leftover
            System.out.println("\n=========== UNASSIGNED PARTICIPANTS ===========\n");
            if (leftover.isEmpty()) {
                System.out.println("None — all participants assigned to teams.");
            } else {
                leftover.forEach(p -> System.out.println(" - " + p));
            }

            // Save only full teams
            handler.saveTeams(teams, "Resources/formed_teams.csv");
            System.out.println("\n✔ Saved full teams to formed_teams.csv");

        } catch (InvalidDataException e) {
            System.out.println("❌ Invalid Data: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("❌ I/O Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("❌ Thread Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
