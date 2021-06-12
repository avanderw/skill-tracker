package net.avdw.skilltracker;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import org.junit.Test;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertEquals;

public class LiquibaseTest {

    @SneakyThrows
    @Test
    public void test_20210603FlattenTables_DbUpdate() {
        Path source = new File(LiquibaseTest.class.getResource("/database/2021-06-03-old.sqlite").toURI()).toPath();
        Path target = Paths.get("target/test-resources/database/2021-06-03-update.sqlite");
        Files.createDirectories(target.getParent());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        CommandLine.IFactory instance = TestGuiceFactory.getInstance(new TestModule(target.toString()));
        instance.create(DbIntegrity.class).init();

        try (ConnectionSource connectionSource = instance.create(ConnectionSource.class)) {
            Dao<PlayEntity, Integer> playDao = DaoManager.createDao(connectionSource, PlayEntity.class);
            assertEquals(444, playDao.queryForAll().size());
        }
    }
}
