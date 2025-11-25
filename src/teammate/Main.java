package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.services.TeamBuilder;
import teammate.services.ParticipantSurveyManager;
import teammate.threads.TeamBuilderThread;
import teammate.threads.SurveyProcessorThread;

import java.io.IOException;
import java.util.*;

public class Main {

    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> wellBalanced = new ArrayList<>();
    private static List<Team> secondary = new ArrayList<>();
    private static List<Participant> leftover = new ArrayList<>();

    private static Integer teamSize = null;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CSVHandler csv = new CSVHandler();
        TeamBuilder builder = new TeamBuilder();
        ParticipantSurveyManager surveyManager = new ParticipantSurveyManager();

        while (true) {

            System.out.println("\n===============================");
            System.out.println("      TeamMate - Main Menu");
            System.out.println("===============================");
            System.out.println("1. Organizer");
            System.out.println("2. Participant");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            String mainChoice = scanner.nextLine().trim();

            switch (mainChoice) {

                case "1":
                    organizerMenu(scanner, csv, builder);
                    break;

                case "2":
                    surveyManager.startSurvey();
                    break;

                case "3":
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice! Enter 1, 2, or 3.");
            }
        }
    }

    // =============================================================
    // ORGANIZER MENU
    // =============================================================
    private static void organizerMenu(Scanner scanner, CSVHandler csv, TeamBuilder builder) {

        while (true) {

            System.out.println("\n===============================");
            System.out.println("        Organizer Menu");
            System.out.println("===============================");
            System.out.println("1. Load Participants");
            System.out.println("2. View Participants");
            System.out.println("3. Set Team Size");
            System.out.println("4. Run Team Formation");
            System.out.println("5. View FORMED Teams");
            System.out.println("6. View UNASSIGNED Participants");
            System.out.println("7. Save All Teams to CSV");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    loadParticipants(csv);
                    break;

                case "2":
                    if (!checkParticipantsLoaded()) break;
                    viewParticipants();
                    break;

                case "3":
                    setTeamSize(scanner);
                    break;

                case "4":
                    if (!checkParticipantsLoaded()) break;
                    if (!checkTeamSizeSet()) break;
                    runTeamFormation(builder);
                    break;

                case "5":
                    if (!checkTeamsFormed()) break;
                    viewFormedTeams(scanner);
                    break;

                case "6":
                    if (!checkParticipantsLoaded()) break;
                    viewUnassigned();
                    break;

                case "7":
                    if (!checkParticipantsLoaded()) break;
                    saveAll(csv);
                    break;

                case "8":
                    return; // back to main menu

                default:
                    System.out.println("Enter a valid number!");
            }
        }
    }

    // =============================================================
    // LOAD PARTICIPANTS
    // =============================================================
    private static void loadParticipants(CSVHandler csv) {
        try {
            participants = csv.loadParticipants("Resources/participants_sample.csv");
            System.out.println("Loaded " + participants.size() + " participants.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =============================================================
    // CHECKS
    // =============================================================
    private static boolean checkParticipantsLoaded() {
        if (participants == null || participants.isEmpty()) {
            System.out.println("❗ Please load participants first (Option 1).");
            return false;
        }
        return true;
    }

    private static boolean checkTeamSizeSet() {
        if (teamSize == null) {
            System.out.println("❗ Please set team size first (Option 3).");
            return false;
        }

        if (teamSize <= 1) {
            System.out.println("❗ Invalid team size. Please set a value greater than 1.");
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

    // =============================================================
    // VIEW PARTICIPANTS + PAGINATION
    // =============================================================
    private static void viewParticipants() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n====== VIEW PARTICIPANTS ======");
            System.out.println("1. View ALL participants");
            System.out.println("2. View 10-by-10");
            System.out.println("3. Back to Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {

                System.out.println("\n===== ALL PARTICIPANTS =====\n");
                for (int i = 0; i < participants.size(); i++) {
                    System.out.println((i + 1) + ". " + participants.get(i));
                }

                System.out.println("\nPress Enter to return...");
                scanner.nextLine();
                continue;
            }

            else if (choice.equals("2")) {

                int index = 0;

                while (true) {

                    int end = Math.min(index + 10, participants.size());

                    System.out.println("\nShowing participants " +
                            (index + 1) + " to " + end +
                            " of " + participants.size() + "\n");

                    for (int i = index; i < end; i++) {
                        System.out.println((i + 1) + ". " + participants.get(i));
                    }

                    System.out.println("\nOptions: [N] Next | [P] Prev | [Q] Quit");
                    System.out.print("Enter: ");

                    String nav = scanner.nextLine().trim().toUpperCase();

                    if (nav.equals("Q")) break;

                    else if (nav.equals("N")) {
                        if (end >= participants.size())
                            System.out.println("No more participants.");
                        else index += 10;
                    }

                    else if (nav.equals("P")) {
                        if (index == 0)
                            System.out.println("Already at the start.");
                        else index -= 10;
                    }

                    else System.out.println("Invalid!");
                }

                continue;
            }

            else if (choice.equals("3"))
                return;

            else
                System.out.println("Invalid choice!");
        }
    }

    // =============================================================
    // SET TEAM SIZE
    // =============================================================
    private static void setTeamSize(Scanner scan) {
        System.out.print("Enter team size (min 5): ");
        try {
            int size = Integer.parseInt(scan.nextLine());
            if (size <= 1) {
                System.out.println("❌ Cannot form teams with team size " + size + ". Enter a value greater than 1.");
                return;
            }
            teamSize = size;
            System.out.println("Team size updated.");
        } catch (Exception e) {
            System.out.println("Invalid number!");
        }
    }

    // =============================================================
    // TEAM FORMATION
    // =============================================================
    private static void runTeamFormation(TeamBuilder builder) {

        try {
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();

            TeamBuilderThread t2 = new TeamBuilderThread(participants, teamSize, builder);
            t2.start();
            t2.join();

            wellBalanced = t2.getWellBalancedTeams();
            secondary = t2.getSecondaryTeams();
            leftover = t2.getLeftover();

            System.out.println("\nFormation Completed!");
            System.out.println("Well-Balanced Teams: " + wellBalanced.size());
            System.out.println("Secondary Teams   : " + secondary.size());
            System.out.println("Unassigned        : " + leftover.size());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =============================================================
    // FORMED TEAMS SUB-MENU
    // =============================================================
    private static void viewFormedTeams(Scanner scanner) {

        while (true) {
            System.out.println("\n========== VIEW FORMED TEAMS ==========");
            System.out.println("1. View ALL Teams");
            System.out.println("2. View WELL-BALANCED Teams");
            System.out.println("3. View SECONDARY Teams");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");

            String c = scanner.nextLine();

            switch (c) {
                case "1": viewAllTeams(); break;
                case "2": viewWellBalanced(); break;
                case "3": viewSecondary(); break;
                case "4": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewAllTeams() {

        if (wellBalanced.isEmpty() && secondary.isEmpty()) {
            System.out.println("No teams formed.");
            return;
        }

        int n = 1;
        System.out.println("\n======= ALL TEAMS =======\n");

        for (Team t : wellBalanced) {
            System.out.println("WB-Team " + n++);
            t.getMembers().forEach(System.out::println);
            System.out.println();
        }

        for (Team t : secondary) {
            System.out.println("SC-Team " + n++);
            t.getMembers().forEach(System.out::println);
            System.out.println();
        }
    }

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

    // =============================================================
    // VIEW UNASSIGNED
    // =============================================================
    private static void viewUnassigned() {

        if (leftover.isEmpty()) {
            System.out.println("No unassigned participants.");
            return;
        }

        System.out.println("\nUnassigned Participants:\n");
        leftover.forEach(p -> System.out.println(" - " + p));
    }

    // =============================================================
    // SAVE ALL TEAMS
    // =============================================================
    private static void saveAll(CSVHandler csv) {
        try {
            csv.saveAllTeams(wellBalanced, secondary, leftover, "Resources/all_teams_output.csv");
            System.out.println("Saved to Resources/all_teams_output.csv");
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

}
