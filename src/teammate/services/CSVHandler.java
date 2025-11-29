package teammate.services;

import teammate.exceptions.InvalidDataException;
import teammate.models.Participant;
import teammate.models.Team;
import teammate.utils.LoggerUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CSVHandler {

    private static final Logger log = LoggerUtil.getLogger();

    // ============================================================
    // LOAD PARTICIPANT CSV
    // ============================================================
    public List<Participant> loadParticipants(String filePath)
            throws IOException, InvalidDataException {

        log.info("Loading participants from CSV: " + filePath);

        List<Participant> list = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            log.severe("CSV file not found: " + filePath);
            throw new FileNotFoundException("CSV file not found at: " + filePath);
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine(); // header

        log.fine("CSV header read: " + line);

        int row = 1;

        while ((line = br.readLine()) != null) {

            if (line.trim().isEmpty()) {
                log.warning("Skipping empty line at row " + row);
                continue;
            }

            log.fine("Reading CSV row " + row + ": " + line);

            String[] data = line.split(",");

            if (data.length < 8) {
                log.severe("Missing column values at row " + row);
                throw new InvalidDataException("Missing values in CSV at row " + row);
            }

            try {
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
                log.fine("Participant added: " + name + " (" + id + ")");

            } catch (NumberFormatException ex) {
                log.severe("Invalid number format at row " + row + ": " + ex.getMessage());
                throw new InvalidDataException("Invalid data at row " + row + " → " + ex.getMessage());
            }

            row++;
        }

        br.close();

        log.info("Successfully loaded " + list.size() + " participants.");
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

        log.info("Saving all teams into CSV: " + filePath);

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        // WELL BALANCED TEAMS
        writer.write("=== WELL-BALANCED TEAMS ===\n");
        writer.write("Team,ID,Name,Email,Game,Role,Skill,PersonalityType\n");

        int wbNum = 1;
        for (Team t : wellBalanced) {
            log.fine("Writing WB-Team " + wbNum + " with " + t.getMembers().size() + " members.");
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
            log.fine("Writing SC-Team " + scNum + " with " + t.getMembers().size() + " members.");
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

        log.info("Writing " + leftover.size() + " unassigned participants.");

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
        log.info("Team CSV saved successfully: " + filePath);
    }
}
