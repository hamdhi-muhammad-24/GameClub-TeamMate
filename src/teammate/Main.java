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

                case 2:
                    if (!checkParticipantsLoaded()) break;
                    viewParticipants();
                    break;

                case 3:
                    setTeamSize(scanner);
                    break;

                case 4:
                    if (!checkParticipantsLoaded()) break;
                    runTeamFormation(builder);
                    break;

                case 5:
                    if (!checkTeamsFormed()) break;
                    viewWellBalanced();
                    break;

                case 6:
                    if (!checkTeamsFormed()) break;
                    viewSecondary();
                    break;

                case 7:
                    if (!checkParticipantsLoaded()) break;
                    viewUnassigned();
                    break;

                case 8:
                    if (!checkParticipantsLoaded()) break;
                    saveAll(csv);
                    break;

                case 9:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Enter a valid number!");
            }
        }
    }

    // ===============================================
    // LOAD PARTICIPANTS
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
    // CHECKS
    // ===============================================
    private static boolean checkParticipantsLoaded() {
        if (participants == null || participants.isEmpty()) {
            System.out.println("❗ Please load participants first (Option 1).");
            return false;
        }
        return true;
    }

    private static boolean checkTeamsFormed() {
        if (wellBalanced.isEmpty() && secondary.isEmpty()) {
            System.out.println("❗ Please run team formation first (Option 4).");
            return false;
        }
        return true;
    }

    // ===============================================
    // VIEW PARTICIPANTS (unchanged)
    // ===============================================

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


    // ===============================================
    // SET TEAM SIZE
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
    // RUN TEAM FORMATION
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
    // VIEW TEAMS
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

    // ===============================================
    // SAVE ALL TEAMS
    // ===============================================
    private static void saveAll(CSVHandler csv) {
        try {
            csv.saveAllTeams(wellBalanced, secondary, leftover, "Resources/all_teams_output.csv");
            System.out.println("Saved to Resources/all_teams_output.csv");
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }
}
