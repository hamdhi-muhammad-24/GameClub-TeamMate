package teammate.services;

import teammate.exceptions.InvalidDataException;
import teammate.models.Participant;
import teammate.models.Team;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    // ============================================================
    // LOAD PARTICIPANT CSV  ✔ THIS IS THE METHOD MAIN.JAVA USES
    // ============================================================
    public List<Participant> loadParticipants(String filePath)
            throws IOException, InvalidDataException {

        List<Participant> list = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("CSV file not found at: " + filePath);
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        br.readLine(); // skip header

        int row = 1;
        while ((line = br.readLine()) != null) {

            if (line.trim().isEmpty()) continue;

            String[] data = line.split(",");

            if (data.length < 8) {
                throw new InvalidDataException("Missing values in CSV at row " + row);
            }

            String id = data[0];
            String name = data[1];
            String email = data[2];
            String game = data[3];
            int skill = Integer.parseInt(data[4]);
            String role = data[5];
            int personalityScore = Integer.parseInt(data[6]);
            String personalityType = data[7];

            Participant p = new Participant(
                    id, name, email, game, role, skill, personalityScore, personalityType
            );

            list.add(p);
            row++;
        }

        return list;
    }


    // ============================================================
    // SAVE ALL TEAMS INTO ONE CSV (WELL-BALANCED + SECONDARY)
    // ============================================================
    public void saveAllTeams(
            List<Team> wellBalanced,
            List<Team> secondary,
            List<Participant> leftover,
            String filePath
    ) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        // WELL BALANCED TEAMS
        writer.write("=== WELL-BALANCED TEAMS ===\n");
        writer.write("Team,ID,Name,Email,Game,Role,Skill,PersonalityType\n");

        int wbNum = 1;
        for (Team t : wellBalanced) {
            for (Participant p : t.getMembers()) {
                writer.write("WB-Team " + wbNum + "," +
                        p.getId() + "," +
                        p.getName() + "," +
                        p.getEmail() + "," +
                        p.getGame() + "," +
                        p.getRole() + "," +
                        p.getSkillLevel() + "," +
                        p.getPersonalityType() + "\n"
                );
            }
            wbNum++;
        }

        // SECONDARY TEAMS
        writer.write("\n=== SECONDARY TEAMS ===\n");
        writer.write("Team,ID,Name,Email,Game,Role,Skill,PersonalityType\n");

        int scNum = 1;
        for (Team t : secondary) {
            for (Participant p : t.getMembers()) {
                writer.write("SC-Team " + scNum + "," +
                        p.getId() + "," +
                        p.getName() + "," +
                        p.getEmail() + "," +
                        p.getGame() + "," +
                        p.getRole() + "," +
                        p.getSkillLevel() + "," +
                        p.getPersonalityType() + "\n"
                );
            }
            scNum++;
        }

        // UNASSIGNED PARTICIPANTS
        writer.write("\n=== UNASSIGNED PARTICIPANTS ===\n");
        writer.write("ID,Name,Email,Game,Role,Skill,PersonalityType\n");

        for (Participant p : leftover) {
            writer.write(
                    p.getId() + "," +
                            p.getName() + "," +
                            p.getEmail() + "," +
                            p.getGame() + "," +
                            p.getRole() + "," +
                            p.getSkillLevel() + "," +
                            p.getPersonalityType() + "\n"
            );
        }

        writer.close();
    }
}
