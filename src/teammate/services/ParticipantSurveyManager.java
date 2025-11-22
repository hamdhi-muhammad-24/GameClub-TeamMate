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
        // BASIC INFO
        // ------------------------------------------------------
        System.out.print("Enter your ID: ");
        String id = sc.nextLine().trim();

        System.out.print("Enter your Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter your Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Enter Preferred Game: ");
        String game = sc.nextLine().trim();

        System.out.print("Enter Preferred Role: ");
        String role = sc.nextLine().trim();

        int skill = getNumberInput(sc, "Enter Skill Level (1–10): ", 1, 10);

        // ------------------------------------------------------
        // 5-QUESTION PERSONALITY SURVEY
        // ------------------------------------------------------
        System.out.println("\nRate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");

        int q1 = getNumberInput(sc, "Q1. I enjoy taking the lead.   : ", 1, 5);
        int q2 = getNumberInput(sc, "Q2. I like analyzing problems. : ", 1, 5);
        int q3 = getNumberInput(sc, "Q3. I work well in teams.      : ", 1, 5);
        int q4 = getNumberInput(sc, "Q4. I stay calm under pressure.: ", 1, 5);
        int q5 = getNumberInput(sc, "Q5. I adapt to quick changes.  : ", 1, 5);

        // ------------------------------------------------------
        // SCORING LOGIC (your rules)
        // ------------------------------------------------------
        int rawTotal = q1 + q2 + q3 + q4 + q5;    // 5–25
        int finalScore = rawTotal * 4;            // 20–100

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
                id, name, email, game, role, skill, finalScore, personalityType
        ));

        System.out.println("Your responses have been recorded successfully!");
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
    // SAFE NUMBER INPUT (with validation)
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
}
