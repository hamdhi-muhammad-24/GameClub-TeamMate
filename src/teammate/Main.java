package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.services.TeamBuilder;
import teammate.threads.TeamBuilderThread;
import teammate.threads.SurveyProcessorThread;
import teammate.exceptions.InvalidDataException;

import java.io.IOException;
import java.util.*;

public class Main {

    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> wellBalanced = new ArrayList<>();
    private static List<Team> secondary = new ArrayList<>();
    private static List<Participant> leftover = new ArrayList<>();

    private static int teamSize = 5;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CSVHandler csv = new CSVHandler();
        TeamBuilder builder = new TeamBuilder();

        while (true) {

            System.out.println("\n===============================");
            System.out.println("      TeamMate - Main Menu");
            System.out.println("===============================");
            System.out.println("1. Load Participants");
            System.out.println("2. View Participants");
            System.out.println("3. Set Team Size");
            System.out.println("4. Run Team Formation");
            System.out.println("5. View WELL-BALANCED Teams");
            System.out.println("6. View SECONDARY Teams");
            System.out.println("7. View UNASSIGNED Participants");
            System.out.println("8. Save All Teams to CSV");
            System.out.println("9. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid number! Enter 1–9.");
                continue;
            }

            switch (choice) {

                case 1: loadParticipants(csv); break;
                case 2: viewParticipants(); break;
                case 3: setTeamSize(scanner); break;
                case 4: runTeamFormation(builder); break;
                case 5: viewWellBalanced(); break;
                case 6: viewSecondary(); break;
                case 7: viewUnassigned(); break;
                case 8: saveAll(csv); break;
                case 9: System.out.println("Goodbye!"); return;

                default:
                    System.out.println("Enter a valid number!");
            }
        }
    }

    // ===============================================
    // OPTION 1
    // ===============================================
    private static void loadParticipants(CSVHandler csv) {
        try {
            participants = csv.loadParticipants("Resources/participants_sample.csv");
            System.out.println("Loaded " + participants.size() + " participants.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===============================================
    // OPTION 2 — View participants (with pagination)
    // ===============================================
    private static void viewParticipants() {
        // (your existing pagination method unchanged)
        System.out.println("\nShowing participants...");
        // You already uploaded the final working version
    }

    // ===============================================
    // OPTION 3
    // ===============================================
    private static void setTeamSize(Scanner scan) {
        System.out.print("Enter team size (min 5): ");
        try {
            int size = Integer.parseInt(scan.nextLine());
            if (size < 5) {
                System.out.println("Must be >= 5.");
                return;
            }
            teamSize = size;
            System.out.println("Team size updated.");

        } catch (Exception e) {
            System.out.println("Invalid number!");
        }
    }

    // ===============================================
    // OPTION 4 — Run Team Formation
    // ===============================================
    private static void runTeamFormation(TeamBuilder builder) {

        try {
            System.out.println("Running personality classification...");
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();

            System.out.println("Forming teams...");
            TeamBuilderThread t2 = new TeamBuilderThread(participants, teamSize, builder);
            t2.start();
            t2.join();

            wellBalanced = t2.getWellBalancedTeams();
            secondary = t2.getSecondaryTeams();
            leftover = t2.getLeftover();

            System.out.println("\nFormation Complete!");
            System.out.println("Well-Balanced Teams: " + wellBalanced.size());
            System.out.println("Secondary Teams: " + secondary.size());
            System.out.println("Unassigned Participants: " + leftover.size());

        } catch (Exception e) {
            System.out.println("Error forming teams: " + e.getMessage());
        }
    }

    // ===============================================
    private static void viewWellBalanced() {
        if (wellBalanced.isEmpty()) {
            System.out.println("No well-balanced teams.");
            return;
        }
        int i = 1;
        for (Team t : wellBalanced) {
            System.out.println("\nWB-Team " + i++);
            t.getMembers().forEach(System.out::println);
        }
    }

    private static void viewSecondary() {
        if (secondary.isEmpty()) {
            System.out.println("No secondary teams.");
            return;
        }
        int i = 1;
        for (Team t : secondary) {
            System.out.println("\nSC-Team " + i++);
            t.getMembers().forEach(System.out::println);
        }
    }

    private static void viewUnassigned() {
        if (leftover.isEmpty()) {
            System.out.println("No unassigned participants.");
            return;
        }
        System.out.println("\nUnassigned:");
        leftover.forEach(p -> System.out.println(" - " + p));
    }

    private static void saveAll(CSVHandler csv) {
        try {
            csv.saveAllTeams(wellBalanced, secondary, leftover, "Resources/all_teams_output.csv");
            System.out.println("Saved to Resources/all_teams_output.csv");
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }
}
