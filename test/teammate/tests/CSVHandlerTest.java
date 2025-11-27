package teammate.tests;

import org.junit.jupiter.api.Test;
import teammate.services.CSVHandler;
import teammate.models.Participant;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CSVHandlerTest {

    @Test
    void testLoadParticipants() throws Exception {
        CSVHandler handler = new CSVHandler();

        List<Participant> list =
                handler.loadParticipants("Resources/participants_sample.csv");

        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    void testSaveAllTeamsCreatesFile() throws Exception {
        CSVHandler handler = new CSVHandler();
        File out = new File("test_output.csv");

        handler.saveAllTeams(List.of(), List.of(), List.of(), "test_output.csv");

        assertTrue(out.exists());
        out.delete();
    }
}
