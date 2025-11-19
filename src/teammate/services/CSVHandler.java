package teammate.services;

import teammate.models.Participant;
import teammate.models.Team;

import java.io.*;
import java.util.List;

public class CSVHandler {

    public void saveAllTeams(
            List<Team> wellBalanced,
            List<Team> secondary,
            List<Participant> leftover,
            String filePath
    ) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        // =============================
        // WELL-BALANCED TEAMS
        // =============================
        writer.write("=== WELL-BALANCED TEAMS ===\n");
        writer.write("Team,ID,Name,Email,Game,Role,Skill,PersonalityType\n");

        int num = 1;
        for (Team t : wellBalanced) {
            for (Participant p : t.getMembers()) {
                writer.write("WB-Team " + num + "," +
                        p.getId() + "," +
                        p.getName() + "," +
                        p.getEmail() + "," +
                        p.getGame() + "," +
                        p.getRole() + "," +
                        p.getSkillLevel() + "," +
                        p.getPersonalityType() + "\n");
            }
            num++;
        }

        // =============================
        // SECONDARY TEAMS
        // =============================
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
                        p.getPersonalityType() + "\n");
            }
            scNum++;
        }

        // =============================
        // UNASSIGNED PARTICIPANTS
        // =============================
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
