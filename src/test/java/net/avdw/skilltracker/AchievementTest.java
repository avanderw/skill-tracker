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

public class AchievementTest {
    private static final Path DATABASE_SNAPSHOT;

    static {
        Path path = null;
        try {
            path = new File(MainCli.class.getResource("/database/2021-06-03-new.sqlite").toURI()).toPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        DATABASE_SNAPSHOT = path;
    }

    private static final Path DATABASE_TEST = Paths.get("target/test-resources/database",
            String.format("%s.sqlite", AchievementTest.class.getSimpleName()));
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


    @Test
    public void testEnthusiast() {
        cliTester.execute("game view TableTennisVR").success()
                .contains("Enthusiast: Etienne");
        cliTester.execute("player view Etienne").success()
                .contains("Obsession: TableTennisVR")
                .contains("1 game")
                .contains("9 matches");
    }

    @Test
    public void testComrade() {
        cliTester.execute("player view Andrew").success()
                .contains("Comrade: JK");
    }

    @Test
    public void testComradeForGame() {
        cliTester.execute("player view Andrew -g=AgeOfEmpires2").success()
                .contains("Comrade: JK");
    }

    @Test
    public void testGuardian() {
        cliTester.execute("player view JDK -g=AgeOfEmpires2").success()
                .contains("Guardian: JK");
        cliTester.execute("player view JK -g=AgeOfEmpires2").success()
                .contains("Wards: BOT-Hard, JDK");
    }

    @Test
    public void testDominator() {
        cliTester.execute("game view AgeOfEmpires2").success()
                .contains("Dominator: JK");
        cliTester.execute("player view JK").success()
                .contains("Dominating: Age Of Empires 2");
        cliTester.execute("player view Andrew").success()
                .notContains("Dominating: Table Tennis VR");
    }
}
