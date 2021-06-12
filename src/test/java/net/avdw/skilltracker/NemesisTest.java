package net.avdw.skilltracker;

import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import net.avdw.test.CliTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class NemesisTest {
    private static final Path DATABASE_SNAPSHOT;

    static {
        Path path = null;
        try {
            path = new File(NemesisTest.class.getResource("/database/2021-06-03-new.sqlite").toURI()).toPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        DATABASE_SNAPSHOT = path;
    }

    private static final Path DATABASE_TEST = Paths.get("target/test-resources/database/nemesis-test.sqlite");
    private CliTester cliTester;
    private IFactory instance;

    @After
    @SneakyThrows
    public void afterTest() {
        instance.create(ConnectionSource.class).close();
        Files.delete(DATABASE_TEST);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.createDirectories(DATABASE_TEST.getParent());
        Files.copy(DATABASE_SNAPSHOT, DATABASE_TEST, StandardCopyOption.REPLACE_EXISTING);

        instance = TestGuiceFactory.getInstance(new TestModule(DATABASE_TEST.toString()));
        CommandLine commandLine = new CommandLine(MainCli.class, instance);
        cliTester = new CliTester(commandLine);
    }

    @Test
    public void testNemesis() {
        cliTester.execute("player view Andrew -g AgeOfEmpires2").success()
                .contains("Minions: Wicus")
                .contains("15,1")
                .contains("Andrew is #6")
                .contains("9 matches");
        cliTester.execute("player view Wicus -g AgeOfEmpires2").success()
                .contains("Nemesis: Andrew")
                .contains("19,7")
                .contains("Wicus is #5")
                .contains("28 matches");
    }
}
