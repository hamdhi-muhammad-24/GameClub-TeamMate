package teammate.services;

import teammate.models.Team;
import teammate.models.Participant;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    public List<Participant> loadParticipants(String filePath) throws IOException {
        List<Participant> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line;
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");

            Participant p = new Participant(
                    data[0],           // name
                    data[1],           // game
                    data[2],           // role
                    Integer.parseInt(data[3]),
                    Integer.parseInt(data[4]),
                    Integer.parseInt(data[5]),
                    Integer.parseInt(data[6]),
                    Integer.parseInt(data[7]),
                    Integer.parseInt(data[8])
            );
            list.add(p);
        }
        return list;
    }

    public void saveTeams(List<Team> teams, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        writer.write("Team,Name,Role,Game,Skill,Personality\n");

        int teamNumber = 1;
        for (Team team : teams) {
            for (Participant p : team.getMembers()) {
                writer.write(
                        teamNumber + "," +
                                p.getName() + "," +
                                p.getRole() + "," +
                                p.getGame() + "," +
                                p.getSkillLevel() + "," +
                                p.getPersonalityType() + "\n"
                );
            }
            teamNumber++;
        }
        writer.close();
    }
}
