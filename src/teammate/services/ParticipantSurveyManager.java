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

        System.out.print("Enter your Name: ");
        String name = sc.nextLine().trim();

        // ================================
        // EMAIL VALIDATION (Updated)
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

            break; // VALID email
        }

        System.out.print("Enter Preferred Game: ");
        String game = sc.nextLine().trim();

        System.out.print("Enter Preferred Role: ");
        String role = sc.nextLine().trim();

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

        // ------------------------------------------------------
        // SCORING LOGIC (your rules)
        // ------------------------------------------------------
        int rawTotal = q1 + q2 + q3 + q4 + q5;   // 5–25
        int finalScore = rawTotal * 4;           // 20–100

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
    private boolean emailExists(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length >= 3) {
                    String existingEmail = data[2].trim();
                    if (existingEmail.equalsIgnoreCase(email)) {
                        return true; // email found!
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error checking email uniqueness: " + e.getMessage());
        }

        return false;
    }


    // ======================================================
    // APPEND PARTICIPANT ROW INTO CSV
    // ======================================================
    private void appendToCSV(Participant p) {

        try {
            FileWriter fw = new FileWriter(CSV_PATH, true);  // append mode
            BufferedWriter bw = new BufferedWriter(fw);

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
    private String generateNextId() {
        int maxNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                String id = data[0]; // ID col

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
}
