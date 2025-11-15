package teammate.services;

import teammate.exceptions.InvalidDataException;
import teammate.models.Participant;
import teammate.models.Team;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

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

            // EXPECT EXACTLY 8 COLUMNS
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


    public void saveTeams(List<Team> teams, String filePath) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("Team,ID,Name,Game,Role,Skill,PersonalityType\n");

        int teamNum = 1;

        for (Team t : teams) {
            for (Participant p : t.getMembers()) {

                writer.write(teamNum + "," +
                        p.getId() + "," +
                        p.getName() + "," +
                        p.getGame() + "," +
                        p.getRole() + "," +
                        p.getSkillLevel() + "," +
                        p.getPersonalityType() + "\n"
                );

            }
            teamNum++;
        }

        writer.close();
    }
}
