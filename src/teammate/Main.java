package teammate;

import teammate.models.Participant;
import teammate.models.Team;
import teammate.services.CSVHandler;
import teammate.services.TeamBuilder;
import teammate.services.ParticipantSurveyManager;
import teammate.threads.TeamBuilderThread;
import teammate.threads.SurveyProcessorThread;
import teammate.utils.LoggerUtil;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = LoggerUtil.getLogger();

    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> wellBalanced = new ArrayList<>();
    private static List<Team> secondary = new ArrayList<>();
    private static List<Participant> leftover = new ArrayList<>();

    private static Integer teamSize = null;

    public static void main(String[] args) {

        log.info("Application started.");

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
            log.fine("Main menu choice = " + mainChoice);

            switch (mainChoice) {

                case "1":
                    log.info("Organizer selected.");

                    if (!organizerLogin(scanner)) {
                        log.warning("Organizer login failed.");
                        System.out.println("Returning to main menu...\n");
                        break;
                    }

                    log.info("Organizer login successful.");
                    organizerMenu(scanner, csv, builder);
                    break;


                case "2":
                    log.info("Participant selected.");
                    participantMenu(scanner, surveyManager);
                    break;


                case "3":
                    log.info("System exit chosen.");
                    System.out.println("Goodbye!");
                    return;

                default:
                    log.warning("Invalid main menu choice: " + mainChoice);
                    System.out.println("Invalid choice! Enter 1, 2, or 3.");
            }
        }
    }

    // =============================================================
    // PARTICIPANT MENU
    // =============================================================
    private static void participantMenu(Scanner scanner, ParticipantSurveyManager surveyManager) {

        log.info("Opening Participant Menu.");

        while (true) {
            System.out.println("\n===============================");
            System.out.println("        Participant Menu");
            System.out.println("===============================");
            System.out.println("1. Complete Survey");
            System.out.println("2. Back to Main Menu");
            System.out.println("3. Exit System");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            log.fine("Participant menu choice = " + choice);

            switch (choice) {

                case "1":
                    log.info("Participant started survey.");
                    surveyManager.startSurvey();
                    break;

                case "2":
                    log.info("Returning to main menu from participant menu.");
                    return;

                case "3":
                    log.severe("System exit triggered by participant.");
                    System.out.println("Exiting system...");
                    System.exit(0);

                default:
                    log.warning("Invalid participant menu choice: " + choice);
                    System.out.println("Invalid choice!");
            }
        }
    }

    // =============================================================
    // ORGANIZER LOGIN
    // =============================================================
    private static boolean organizerLogin(Scanner scanner) {

        final String USERNAME = "admin";
        final String PASSWORD = "1234";

        System.out.println("\n--------- Organizer Login ---------");

        System.out.print("Enter Username: ");
        String u = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String p = scanner.nextLine().trim();

        log.fine("Organizer attempted login with username: " + u);

        if (u.equalsIgnoreCase(USERNAME) && p.equals(PASSWORD)) {
            log.info("Organizer login success.");
            System.out.println("✔ Login Successful!\n");
            return true;
        } else {
            log.warning("Organizer login failed: wrong credentials.");
            System.out.println("❌ Invalid username or password!\n");
            return false;
        }
    }

    // =============================================================
    // ORGANIZER MENU
    // =============================================================
    private static void organizerMenu(Scanner scanner, CSVHandler csv, TeamBuilder builder) {

        log.info("Opening Organizer Menu.");

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
            System.out.println("9. Exit System");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            log.fine("Organizer menu choice = " + choice);

            switch (choice) {

                case "1":
                    log.info("Loading participants...");
                    loadParticipants(csv);
                    break;

                case "2":
                    if (!checkParticipantsLoaded()) break;
                    log.info("Viewing participants.");
                    viewParticipants();
                    break;

                case "3":
                    log.info("Setting team size.");
                    setTeamSize(scanner);
                    break;

                case "4":
                    if (!checkParticipantsLoaded()) break;
                    if (!checkTeamSizeSet()) break;
                    log.info("Running team formation.");
                    runTeamFormation(builder);
                    break;

                case "5":
                    if (!checkTeamsFormed()) break;
                    log.info("Viewing formed teams.");
                    viewFormedTeams(scanner);
                    break;

                case "6":
                    if (!checkParticipantsLoaded()) break;
                    log.info("Viewing unassigned participants.");
                    viewUnassigned();
                    break;

                case "7":
                    if (!checkParticipantsLoaded()) break;
                    log.info("Saving all teams.");
                    saveAll(csv);
                    break;

                case "8":
                    log.info("Back to main menu.");
                    return;

                case "9":
                    log.severe("System exit triggered by organizer.");
                    System.out.println("Exiting system...");
                    System.exit(0);

                default:
                    log.warning("Invalid organizer menu choice: " + choice);
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
            log.info("Participants loaded: count = " + participants.size());
            System.out.println("Loaded " + participants.size() + " participants.");
        } catch (Exception e) {
            log.severe("Error loading participants: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =============================================================
    // CHECKS
    // =============================================================
    private static boolean checkParticipantsLoaded() {
        if (participants == null || participants.isEmpty()) {
            log.warning("Participants NOT loaded.");
            System.out.println("❗ Please load participants first (Option 1).");
            return false;
        }
        return true;
    }

    private static boolean checkTeamSizeSet() {
        if (teamSize == null) {
            log.warning("Team size NOT set.");
            System.out.println("❗ Please set team size first (Option 3).");
            return false;
        }

        if (teamSize <= 3) {
            log.warning("Invalid team size: " + teamSize);
            System.out.println("❗ Invalid team size. Minimum allowed is 4.");
            return false;
        }

        return true;
    }

    private static boolean checkTeamsFormed() {
        if (wellBalanced.isEmpty() && secondary.isEmpty()) {
            log.warning("Teams not formed yet.");
            System.out.println("❗ Please run team formation first (Option 4).");
            return false;
        }
        return true;
    }

    // =============================================================
    // VIEW PARTICIPANTS
    // =============================================================
    private static void viewParticipants() {

        log.info("Opening participant viewer.");

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n====== VIEW PARTICIPANTS ======");
            System.out.println("1. View ALL participants");
            System.out.println("2. View 10-by-10");
            System.out.println("3. Back to Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();
            log.fine("View participants choice = " + choice);

            if (choice.equals("1")) {

                log.info("Displaying ALL participants.");

                System.out.println("\n===== ALL PARTICIPANTS =====\n");
                for (int i = 0; i < participants.size(); i++) {
                    System.out.println((i + 1) + ". " + participants.get(i));
                }

                System.out.println("\nPress Enter to return...");
                scanner.nextLine();
                continue;
            }

            else if (choice.equals("2")) {

                log.info("Displaying participants 10-by-10.");

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
                    log.fine("Participant list navigation: " + nav);

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

            else {
                log.warning("Invalid participants viewer choice: " + choice);
                System.out.println("Invalid choice!");
            }
        }
    }

    // =============================================================
    // SET TEAM SIZE
    // =============================================================
    private static void setTeamSize(Scanner scan) {
        System.out.print("Enter team size : ");
        try {
            int size = Integer.parseInt(scan.nextLine());
            if (size <= 3) {
                log.warning("Attempted to set invalid team size = " + size);
                System.out.println("❌ Cannot form teams with team size " + size + ". (Enter a value greater than 3).");
                return;
            }
            teamSize = size;
            log.info("Team size set to " + size);
            System.out.println("Team size updated.");
        } catch (Exception e) {
            log.severe("Invalid input for team size: " + e.getMessage());
            System.out.println("Invalid number!");
        }
    }

    // =============================================================
    // TEAM FORMATION
    // =============================================================
    private static void runTeamFormation(TeamBuilder builder) {

        log.info("Starting team formation...");

        try {
            SurveyProcessorThread t1 = new SurveyProcessorThread(participants);
            t1.start();
            t1.join();

            log.info("Survey processing thread completed.");

            TeamBuilderThread t2 = new TeamBuilderThread(participants, teamSize, builder);
            t2.start();
            t2.join();

            log.info("Team building thread completed.");

            wellBalanced = t2.getWellBalancedTeams();
            secondary = t2.getSecondaryTeams();
            leftover = t2.getLeftover();

            log.info("Well-balanced teams = " + wellBalanced.size());
            log.info("Secondary teams = " + secondary.size());
            log.info("Leftover count = " + leftover.size());

            System.out.println("\nFormation Completed!");
            System.out.println("Well-Balanced Teams: " + wellBalanced.size());
            System.out.println("Secondary Teams   : " + secondary.size());
            System.out.println("Unassigned        : " + leftover.size());

        } catch (Exception e) {
            log.severe("Team formation error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =============================================================
    // VIEW FORMED TEAMS
    // =============================================================
    private static void viewFormedTeams(Scanner scanner) {

        log.info("Viewing formed teams.");

        while (true) {
            System.out.println("\n========== VIEW FORMED TEAMS ==========");
            System.out.println("1. View ALL Teams");
            System.out.println("2. View WELL-BALANCED Teams");
            System.out.println("3. View SECONDARY Teams");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");

            String c = scanner.nextLine();
            log.fine("Formed team choice = " + c);

            switch (c) {
                case "1": viewAllTeams(); break;
                case "2": viewWellBalanced(); break;
                case "3": viewSecondary(); break;
                case "4": return;
                default:
                    log.warning("Invalid formed-team option: " + c);
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewAllTeams() {

        log.info("Displaying all teams.");

        if (wellBalanced.isEmpty() && secondary.isEmpty()) {
            log.warning("No teams available to display.");
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

        log.info("Viewing well-balanced teams.");

        if (wellBalanced.isEmpty()) {
            log.warning("No well-balanced teams.");
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

        log.info("Viewing secondary teams.");

        if (secondary.isEmpty()) {
            log.warning("No secondary teams.");
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

        log.info("Viewing unassigned participants.");

        if (leftover.isEmpty()) {
            log.info("No unassigned participants.");
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
            log.info("Teams saved to Resources/all_teams_output.csv");
            System.out.println("Saved to Resources/all_teams_output.csv");
        } catch (IOException e) {
            log.severe("Error saving teams: " + e.getMessage());
            System.out.println("Save error: " + e.getMessage());
        }
    }

}
