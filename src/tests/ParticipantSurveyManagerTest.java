package tests;

import org.junit.jupiter.api.Test;
import teammate.services.ParticipantSurveyManager;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipantSurveyManagerTest {

    @Test
    void testGenerateNextId() {
        ParticipantSurveyManager manager = new ParticipantSurveyManager();

        String id = manager.generateNextId();
        assertTrue(id.startsWith("P"));
    }

    @Test
    void testEmailExistsCheck() {
        ParticipantSurveyManager manager = new ParticipantSurveyManager();

        boolean exists = manager.emailExists("test@university.edu");
        assertFalse(exists); // In a real test you'll provide controlled CSV
    }
}
