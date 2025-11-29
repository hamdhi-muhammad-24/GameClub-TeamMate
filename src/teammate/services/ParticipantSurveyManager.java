package teammate.services;

import teammate.models.Participant;
import teammate.utils.LoggerUtil;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class ParticipantSurveyManager {

    private static final String CSV_PATH = "Resources/participants_sample.csv";
    private static final Logger log = LoggerUtil.getLogger();

    // ======================================================
    // PUBLIC METHOD — Run Survey for a Participant
    // ======================================================
    public void startSurvey() {

        log.info("Participant survey started.");

        Scanner sc = new Scanner(System.in);

        System.out.println("\n===============================");
        System.out.println("     Participant Survey");
        System.out.println("===============================\n");

        // AUTO-GENERATE ID
        String nextId = generateNextId();
        System.out.println("Assigned ID: " + nextId);
        log.fine("Generated next participant ID: " + nextId);

        // ================================
        // NAME VALIDATION
        // ================================
        String name = "";
        while (true) {
            System.out.print("Enter your Name: ");
            name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty! Please enter your name.\n");
                log.warning("User entered empty name. Requesting retry.");
                continue;
            }
            break;
        }
        log.fine("Name entered: " + name);

        // ================================
        // EMAIL VALIDATION
        // ================================
        String email;
        while (true) {
            System.out.print("Enter your Email (must end with @university.edu): ");
            email = sc.nextLine().trim();

            if (!email.toLowerCase().endsWith("@university.edu")) {
                System.out.println("❌ Invalid email! It must end with @university.edu\n");
                log.warning("Invalid email format entered: " + email);
                continue;
            }

            if (emailExists(email)) {
                System.out.println("❌ This email is already registered! Try a different email.\n");
                log.warning("Duplicate email detected: " + email);
                continue;
            }
            break;
        }
        log.fine("Email validated: " + email);

        // ================================
        // PREFERRED GAME SELECT
        // ================================
        String game = "";
        while (true) {
            System.out.println("\nChoose your Preferred Game:");
            System.out.println("1. Chess");
            System.out.println("2. FIFA");
            System.out.println("3. Basketball");
            System.out.println("4. CS:GO");
            System.out.println("5. DOTA 2");
            System.out.println("6. Valorant");
            System.out.println("7. Other");
            System.out.print("Enter choice (1–7): ");

            String input = sc.nextLine().trim();

            switch (input) {
                case "1": game = "Chess"; break;
                case "2": game = "FIFA"; break;
                case "3": game = "Basketball"; break;
                case "4": game = "CS:GO"; break;
                case "5": game = "DOTA 2"; break;
                case "6": game = "Valorant"; break;

                case "7":
                    while (true) {
                        System.out.print("Enter your game (cannot be empty): ");
                        game = sc.nextLine().trim();
                        if (game.isEmpty()) {
                            System.out.println("❌ Game name cannot be empty!\n");
                            log.warning("User entered empty custom game name.");
                            continue;
                        }
                        break;
                    }
                    break;

                default:
                    System.out.println("Invalid choice! Please enter 1–7.\n");
                    log.warning("Invalid game selection option: " + input);
                    continue;
            }
            break;
        }
        log.fine("Game selected: " + game);

        // ================================
        // ROLE SELECT
        // ================================
        String role = "";
        while (true) {

            System.out.println("\nChoose your Preferred Role:");
            System.out.println("1. Strategist");
            System.out.println("2. Attacker");
            System.out.println("3. Defender");
            System.out.println("4. Supporter");
            System.out.println("5. Coordinator");
            System.out.print("Enter choice (1–5): ");

            String r = sc.nextLine().trim();

            switch (r) {
                case "1": role = "Strategist"; break;
                case "2": role = "Attacker"; break;
                case "3": role = "Defender"; break;
                case "4": role = "Supporter"; break;
                case "5": role = "Coordinator"; break;
                default:
                    System.out.println("❌ Invalid choice! Please enter 1–5.\n");
                    log.warning("Invalid role selection: " + r);
                    continue;
            }
            break;
        }
        log.fine("Role selected: " + role);

        // ================================
        // SKILL LEVEL INPUT
        // ================================
        int skill = getNumberInput(sc, "Enter Skill Level (1–10): ", 1, 10);
        log.fine("Skill level entered: " + skill);

        // ================================
        // PERSONALITY SURVEY
        // ================================
        System.out.println("\nRate each statement from 1 to 5\n");

        int q1 = getNumberInput(sc, "Q1: ", 1, 5);
        int q2 = getNumberInput(sc, "Q2: ", 1, 5);
        int q3 = getNumberInput(sc, "Q3: ", 1, 5);
        int q4 = getNumberInput(sc, "Q4: ", 1, 5);
        int q5 = getNumberInput(sc, "Q5: ", 1, 5);

        int rawTotal = q1 + q2 + q3 + q4 + q5;
        int finalScore = rawTotal * 4;
        log.fine("Personality score calculated: " + finalScore);

        String personalityType;
        if (finalScore >= 90) personalityType = "Leader";
        else if (finalScore >= 70) personalityType = "Balanced";
        else personalityType = "Thinker";

        log.info("Personality classified as " + personalityType);

        // SAVE NEW PARTICIPANT
        appendToCSV(new Participant(
                nextId, name, email, game, role, skill, finalScore, personalityType
        ));
        log.info("Participant saved successfully: " + name);

        System.out.println("Your responses have been recorded successfully!");
    }

    // ======================================================
    // CHECK IF EMAIL EXISTS
    // ======================================================
    public boolean emailExists(String email) {

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length >= 3) {
                    if (data[2].trim().equalsIgnoreCase(email)) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            log.severe("Error checking email existence: " + e.getMessage());
        }

        return false;
    }

    // ======================================================
    // APPEND NEW PARTICIPANT TO CSV
    // ======================================================
    private void appendToCSV(Participant p) {

        try {
            File file = new File(CSV_PATH);
            boolean needsNewLine = false;

            if (file.length() > 0) {
                FileInputStream fis = new FileInputStream(file);
                int lastByte = -1, current;
                while ((current = fis.read()) != -1) lastByte = current;
                fis.close();

                if (lastByte != '\n') needsNewLine = true;
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_PATH, true));

            if (needsNewLine) bw.newLine();

            bw.write(
                    p.getId() + "," +
                            p.getName() + "," +
                            p.getEmail() + "," +
                            p.getGame() + "," +
                            p.getSkillLevel() + "," +
                            p.getRole() + "," +
                            p.getPersonalityScore() + "," +
                            p.getPersonalityType()
            );

            bw.newLine();
            bw.close();

            log.info("Participant appended to CSV: " + p.getId());

        } catch (IOException e) {
            log.severe("Error saving participant: " + e.getMessage());
        }
    }

    // ======================================================
    // SAFE NUMBER INPUT
    // ======================================================
    private int getNumberInput(Scanner sc, String label, int min, int max) {

        while (true) {
            System.out.print(label);

            try {
                int n = Integer.parseInt(sc.nextLine());
                if (n < min || n > max) {
                    System.out.println("Enter a value between " + min + " and " + max + ".");
                    log.warning("Invalid number outside range: " + n);
                    continue;
                }
                return n;

            } catch (Exception e) {
                System.out.println("Invalid number! Try again.");
                log.warning("User entered invalid number format.");
            }
        }
    }

    // ======================================================
    // GENERATE NEXT ID
    // ======================================================
    public String generateNextId() {
        int maxNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                String id = data[0];

                if (id.startsWith("P")) {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxNumber) maxNumber = num;
                }
            }

        } catch (Exception e) {
            log.severe("Error reading last participant ID: " + e.getMessage());
        }

        String newId = "P" + (maxNumber + 1);
        log.fine("Next ID computed = " + newId);

        return newId;
    }
}
