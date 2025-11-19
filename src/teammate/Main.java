package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.services.TeamBuilder;
import teammate.threads.TeamBuilderThread;
import teammate.threads.SurveyProcessorThread;
import teammate.exceptions.InvalidDataException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> teams = new ArrayList<>();
    private static List<Participant> leftover = new ArrayList<>();
    private static int teamSize = 5;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CSVHandler csv = new CSVHandler();
        TeamBuilder builder = new TeamBuilder();

        while (true) {

            System.out.println("\n======================================");
            System.out.println("     TeamMate - Team Formation UI");
            System.out.println("======================================");
            System.out.println("1. Load Participants");
            System.out.println("2. View Participants");
            System.out.println("3. Set Team Size");
            System.out.println("4. Run Team Formation");
            System.out.println("5. View Full Teams");
            System.out.println("6. View Unassigned Participants");
            System.out.println("7. Save Teams to CSV");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number 1–8");
                continue;
            }

            switch (choice) {

                case 1:
                    loadParticipants(csv);
                    break;

                case 2:
                    viewParticipants();
                    break;

                case 3:
                    setTeamSize(scanner);
                    break;

                case 4:
                    runTeamFormation(builder);
                    break;

                case 5:
                    viewFullTeams();
                    break;

                case 6:
                    viewLeftover();
                    break;

                case 7:
                    saveTeams(csv);
                    break;

                case 8:
                    System.out.println("Exiting TeamMate. Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice! Enter a number 1–8.");
            }
        }
    }

    // ================================
    // Menu Option 1: Load CSV
    // ================================
    private static void loadParticipants(CSVHandler csv) {
        try {
            System.out.println("Loading participants...");
            participants = csv.loadParticipants("Resources/participants_sample.csv");
            System.out.println("Loaded " + participants.size() + " participants successfully!");

        } catch (IOException | InvalidDataException e) {
            System.out.println("Error loading CSV: " + e.getMessage());
        }
    }

    // ================================
// Menu Option 2: View Participants
// ================================
    private static void viewParticipants() {

        if (participants.isEmpty()) {
            System.out.println("No participants loaded yet!");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("\nView Participants:");
        System.out.println("1. View ALL participants");
        System.out.println("2. View 10-by-10");
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine().trim();

        // ====================================================
        // OPTION 1 — VIEW ALL PARTICIPANTS
        // ====================================================
        if (choice.equals("1")) {

            System.out.println("\n===== ALL PARTICIPANTS =====\n");

            for (int i = 0; i < participants.size(); i++) {
                System.out.println((i + 1) + ". " + participants.get(i));
            }

            System.out.println("\nEnd of participant list.");
            System.out.println("Press Enter to return to main menu...");
            scanner.nextLine();
            return;
        }


        // ====================================================
        // OPTION 2 — PAGINATED VIEW (10 by 10)
        // ====================================================
        else if (choice.equals("2")) {

            int index = 0; // start position

            while (true) {

                int end = Math.min(index + 10, participants.size());

                // Show range info
                System.out.println("\nShowing participants " +
                        (index + 1) + " to " + end +
                        " of " + participants.size() + "\n");

                // Print participants in current range
                for (int i = index; i < end; i++) {
                    System.out.println((i + 1) + ". " + participants.get(i));
                }

                // If this is the last partial batch (<10)
                if (end == participants.size() && (end - index) < 10) {
                    System.out.println("\nShowing last " + (end - index) + " participants.");
                }

                // Navigation options
                System.out.println("\nOptions: [N] Next | [P] Previous | [Q] Quit");
                System.out.print("Enter your choice: ");
                String nav = scanner.nextLine().trim().toUpperCase();

                // Quit pagination
                if (nav.equals("Q")) {
                    return;
                }

                // NEXT PAGE FIXED
                else if (nav.equals("N")) {

                    if (end >= participants.size()) {
                        // ⭐ FIX: Only show the message once, do NOT re-render list
                        System.out.println("No more participants to show.");
                        continue; // stay on same page without printing list again
                    } else {
                        index += 10;
                    }
                }

                // PREVIOUS PAGE
                else if (nav.equals("P")) {

                    if (index == 0) {
                        System.out.println("Already at the beginning.");
                    } else {
                        index -= 10;
                    }
                }

                // Invalid input
                else {
                    System.out.println("Invalid option! Enter N, P, or Q.");
                }
            }
        }

        // ====================================================
        // INVALID CHOICE
        // ====================================================
        else {
            System.out.println("Invalid choice! Returning to main menu.");
        }
    }


    // ================================
    // Menu Option 3: Set Team Size
    // ================================
    private static void setTeamSize(Scanner scanner) {
        System.out.print("Enter team size (minimum 5): ");

        try {
            int size = Integer.parseInt(scanner.nextLine());

            if (size < 5) {
                System.out.println("Team size must be at least 5!");
                return;
            }

            teamSize = size;
            System.out.println("Team size set to " + teamSize);

        } catch (Exception e) {
            System.out.println("Invalid number!");
        }
    }

    // ================================
    // Menu Option 4: Run Team Formation
    // ================================
    private static void runTeamFormation(TeamBuilder builder) {

        if (participants.isEmpty()) {
            System.out.println("Load participants first!");
            return;
        }

        try {
            System.out.println("Running personality classification...");
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();

            System.out.println("Building teams...");
            TeamBuilderThread t2 = new TeamBuilderThread(participants, teamSize, builder);
            t2.start();
            t2.join();

            teams = t2.getTeams();
            leftover = t2.getLeftover();

            System.out.println("\nTeam Formation Completed!");
            System.out.println("Full Teams Formed: " + teams.size());
            System.out.println("Unassigned Participants: " + leftover.size());

        } catch (Exception e) {
            System.out.println("Error during team formation: " + e.getMessage());
        }
    }

    // ================================
    // Menu Option 5: View Full Teams
    // ================================
    private static void viewFullTeams() {

        if (teams.isEmpty()) {
            System.out.println("No teams formed yet!");
            return;
        }

        System.out.println("\n========== FULL TEAMS ==========\n");

        int num = 1;
        for (Team t : teams) {
            System.out.println("Team " + num++);
            t.getMembers().forEach(System.out::println);
            System.out.println();
        }
    }

    // ================================
    // Menu Option 6: View Leftover
    // ================================
    private static void viewLeftover() {

        System.out.println("\n========== UNASSIGNED PARTICIPANTS ==========\n");

        if (leftover.isEmpty()) {
            System.out.println("None — all participants assigned.");
            return;
        }

        leftover.forEach(p -> System.out.println(" - " + p));
    }

    // ================================
    // Menu Option 7: Save Teams
    // ================================
    private static void saveTeams(CSVHandler csv) {
        try {
            csv.saveTeams(teams, "Resources/formed_teams.csv");
            System.out.println("Teams saved to Resources/formed_teams.csv");

        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
