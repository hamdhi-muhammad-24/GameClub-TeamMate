package teammate.services;

import teammate.models.Participant;

import java.io.*;
import java.util.Scanner;

public class ParticipantSurveyManager {

    private static final String CSV_PATH = "Resources/participants_sample.csv";

    // ======================================================
    // PUBLIC METHOD — Run Survey for a Participant
    // ======================================================
    public void startSurvey() {

        Scanner sc = new Scanner(System.in);

        System.out.println("\n===============================");
        System.out.println("     Participant Survey");
        System.out.println("===============================\n");

        // ------------------------------------------------------
        // AUTO-GENERATE NEXT ID
        // ------------------------------------------------------
        String nextId = generateNextId();
        System.out.println("Assigned ID: " + nextId);

        // ================================
        // NAME VALIDATION (must not be empty)
        // ================================
        String name = "";
        while (true) {
            System.out.print("Enter your Name: ");
            name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty! Please enter your name.\n");
                continue;
            }

            break; // valid
        }


        // ================================
        // EMAIL VALIDATION + No duplicates
        // ================================
        String email;
        while (true) {
            System.out.print("Enter your Email (must end with @university.edu): ");
            email = sc.nextLine().trim();

            if (!email.toLowerCase().endsWith("@university.edu")) {
                System.out.println("❌ Invalid email! It must end with @university.edu\n");
                continue;
            }

            if (emailExists(email)) {
                System.out.println("❌ This email is already registered! Try a different email.\n");
                continue;
            }

            break;
        }

        // ================================
        // PREFERRED GAME SELECTION (1–7)
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
                            continue;
                        }
                        break;  // valid
                    }
                    break;


                default:
                    System.out.println("Invalid choice! Please enter 1–7.\n");
                    continue;
            }
            break;
        }

        // ================================
        // PREFERRED ROLE SELECTION (1–5)
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
                    continue;
            }
            break;
        }


        int skill = getNumberInput(sc, "Enter Skill Level (1–10): ", 1, 10);

        // ------------------------------------------------------
        // 5-QUESTION PERSONALITY SURVEY
        // ------------------------------------------------------
        System.out.println("\nRate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");

        int q1 = getNumberInput(sc, "Q1. I enjoy taking the lead and guiding others during group activities.    : ", 1, 5);
        int q2 = getNumberInput(sc, "Q2. I prefer analyzing situations and coming up with strategic solutions.  : ", 1, 5);
        int q3 = getNumberInput(sc, "Q3. I work well with others and enjoy collaborative teamwork.              : ", 1, 5);
        int q4 = getNumberInput(sc, "Q4. I am calm under pressure and can help maintain team morale.            : ", 1, 5);
        int q5 = getNumberInput(sc, "Q5. I like making quick decisions and adapting in dynamic situations.      : ", 1, 5);

        int rawTotal = q1 + q2 + q3 + q4 + q5;
        int finalScore = rawTotal * 4;

        String personalityType;
        if (finalScore >= 90) personalityType = "Leader";
        else if (finalScore >= 70) personalityType = "Balanced";
        else personalityType = "Thinker";

        System.out.println("\n=========================================");
        System.out.println(" Survey Completed!");
        System.out.println(" Personality Score : " + finalScore);
        System.out.println(" Personality Type  : " + personalityType);
        System.out.println("=========================================\n");

        // ------------------------------------------------------
        // SAVE INTO CSV
        // ------------------------------------------------------
        appendToCSV(new Participant(
                nextId, name, email, game, role, skill, finalScore, personalityType
        ));

        System.out.println("Your responses have been recorded successfully!");
    }


    // ======================================================
    // CHECK IF EMAIL ALREADY EXISTS IN CSV
    // ======================================================
    public boolean emailExists(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length >= 3) {
                    String existingEmail = data[2].trim();
                    if (existingEmail.equalsIgnoreCase(email)) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error checking email uniqueness: " + e.getMessage());
        }

        return false;
    }



    // ======================================================
    // APPEND NEW PARTICIPANT TO CSV (FIXED NEWLINE ISSUE)
    // ======================================================
    private void appendToCSV(Participant p) {

        try {
            File file = new File(CSV_PATH);

            // Check the last character of the file
            boolean needsNewLine = false;

            if (file.length() > 0) {
                FileInputStream fis = new FileInputStream(file);
                int lastByte = -1;
                int current;

                while ((current = fis.read()) != -1) {
                    lastByte = current;
                }
                fis.close();

                // ASCII 10 = '\n'
                if (lastByte != '\n') {
                    needsNewLine = true;
                }
            }

            FileWriter fw = new FileWriter(CSV_PATH, true);
            BufferedWriter bw = new BufferedWriter(fw);

            // Add missing newline if needed
            if (needsNewLine) {
                bw.newLine();
            }

            // Write new participant
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

            bw.newLine();  // ensure next append always goes to new line
            bw.close();

        } catch (IOException e) {
            System.out.println("Error saving participant: " + e.getMessage());
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
                    continue;
                }
                return n;

            } catch (Exception e) {
                System.out.println("Invalid number! Try again.");
            }
        }
    }


    // ======================================================
    // AUTO-GENERATE NEXT PARTICIPANT ID (P### FORMAT)
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
            System.out.println("Error reading ID: " + e.getMessage());
        }

        return "P" + (maxNumber + 1);
    }

//    public String generateTestNextIdForUnitTest() {
    }
