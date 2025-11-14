package teammate.models;

public class Participant {
    private String name;
    private String game;
    private String role;
    private int skillLevel;
    private int q1, q2, q3, q4, q5;
    private String personalityType;

    public Participant(String name, String game, String role,
                       int skillLevel, int q1, int q2, int q3, int q4, int q5) {

        this.name = name;
        this.game = game;
        this.role = role;
        this.skillLevel = skillLevel;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.q5 = q5;
    }

    public int getTotalPersonalityScore() {
        return (q1 + q2 + q3 + q4 + q5) * 4;
    }

    // GETTERS & SETTERS
    public String getName() { return name; }
    public String getGame() { return game; }
    public String getRole() { return role; }
    public int getSkillLevel() { return skillLevel; }
    public String getPersonalityType() { return personalityType; }

    public void setPersonalityType(String type) {
        this.personalityType = type;
    }

    @Override
    public String toString() {
        return name + " (" + role + ", " + personalityType + ", Skill=" + skillLevel + ")";
    }
}
