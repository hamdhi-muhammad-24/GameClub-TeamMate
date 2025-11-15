package teammate.services;

import teammate.models.Participant;
import teammate.exceptions.InvalidDataException;
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
        int row = 0;

        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            row++;
            String[] data = line.split(",");

            if (data.length < 9) {
                throw new InvalidDataException("Missing values in CSV at row " + row);
            }

            try {
                String name = data[0];
                String game = data[1];
                String role = data[2];
                int skill = Integer.parseInt(data[3]);

                int q1 = Integer.parseInt(data[4]);
                int q2 = Integer.parseInt(data[5]);
                int q3 = Integer.parseInt(data[6]);
                int q4 = Integer.parseInt(data[7]);
                int q5 = Integer.parseInt(data[8]);

                // Validate personality scores
                validateScore(q1);
                validateScore(q2);
                validateScore(q3);
                validateScore(q4);
                validateScore(q5);

                // Validate role
                validateRole(role);

                Participant p = new Participant(name, game, role, skill, q1, q2, q3, q4, q5);
                list.add(p);

            } catch (NumberFormatException e) {
                throw new InvalidDataException("Invalid number format at row " + row);
            }
        }
        return list;
    }

    private void validateScore(int score) throws InvalidDataException {
        if (score < 1 || score > 5)
            throw new InvalidDataException("Personality score must be 1–5.");
    }

    private void validateRole(String role) throws InvalidDataException {
        List<String> validRoles = List.of(
                "Strategist", "Attacker", "Defender", "Supporter", "Coordinator"
        );

        if (!validRoles.contains(role)) {
            throw new InvalidDataException("Invalid role: " + role);
        }
    }

    public void saveTeams(List<Team> teams, String filePath) throws IOException {

        File f = new File(filePath);

        if (!f.exists()) {
            System.out.println("Output CSV file not found. Creating a new one...");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(f));

        writer.write("Team,Name,Role,Game,Skill,Personality\n");

        int teamNum = 1;
        for (Team t : teams) {
            for (Participant p : t.getMembers()) {
                writer.write(teamNum + "," +
                        p.getName() + "," +
                        p.getRole() + "," +
                        p.getGame() + "," +
                        p.getSkillLevel() + "," +
                        p.getPersonalityType() + "\n");
            }
            teamNum++;
        }

        writer.close();
    }
}
