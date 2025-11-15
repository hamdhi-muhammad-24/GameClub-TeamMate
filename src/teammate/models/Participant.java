package teammate.models;

public class Participant {

    private String id;
    private String name;
    private String email;
    private String game;
    private String role;
    private int skillLevel;
    private int personalityScore;
    private String personalityType;

    public Participant(String id, String name, String email,
                       String game, String role, int skillLevel,
                       int personalityScore, String personalityType) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.game = game;
        this.role = role;
        this.skillLevel = skillLevel;
        this.personalityScore = personalityScore;
        this.personalityType = personalityType;
    }

    // GETTERS
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGame() { return game; }
    public String getRole() { return role; }
    public int getSkillLevel() { return skillLevel; }
    public int getPersonalityScore() { return personalityScore; }
    public String getPersonalityType() { return personalityType; }

    // SETTERS
    public void setPersonalityType(String personalityType) {
        this.personalityType = personalityType;
    }

    @Override
    public String toString() {
        return name + " (" + role + ", " + personalityType + ", Skill=" + skillLevel + ")";
    }
}
