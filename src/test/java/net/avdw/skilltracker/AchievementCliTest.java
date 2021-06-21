package net.avdw.skilltracker;

import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import net.avdw.test.CliTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AchievementCliTest {
    private static final Path DATABASE_SNAPSHOT;

    static {
        Path path = null;
        try {
            path = new File(StatTest.class.getResource("/database/2021-06-03-new.sqlite").toURI()).toPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        DATABASE_SNAPSHOT = path;
    }

    private static final Path DATABASE_TEST = Paths.get("target/test-resources/database/achievement-test.sqlite");
    private CliTester cliTester;
    private CommandLine.IFactory instance;

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
    public void testHelp() {
        cliTester.execute("achievement --help").success();
    }

    @Test
    public void testAchievement() {
        cliTester.execute("achievement").success()
                .contains("comrade")
                .contains("dominator")
                .contains("enthusiast")
                .contains("guardian")
                .contains("nemesis");
    }

    @Test
    public void testComrade() {
        cliTester.execute("achievement comrade").success()
                .contains("Comrade");
    }

    @Test
    public void testDominator() {
        cliTester.execute("achievement dominator").success()
                .contains("Dominator");
    }

    @Test
    public void testEnthusiast() {
        cliTester.execute("achievement enthusiast").success()
                .contains("Enthusiast");
    }

    @Test
    public void testGuardian() {
        cliTester.execute("achievement guardian").success()
                .contains("Guardian");
    }

    @Test
    public void testNemesis() {
        cliTester.execute("achievement nemesis").success()
                .contains("Nemesis");
    }

}
