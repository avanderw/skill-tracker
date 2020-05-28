package net.avdw.property;

import org.tinylog.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PropertyFileReader {
    public Properties read(final Path propertyPath) {
        Properties properties = new Properties();
        if (Files.exists(propertyPath)) {
            try {
                properties.load(new FileReader(propertyPath.toFile()));
                Logger.debug("File loaded: {}", propertyPath);
            } catch (IOException e) {
                Logger.error(e.getMessage());
                Logger.debug(e);
            }
        } else {
            Logger.debug("File does not exist: {}", propertyPath);
        }
        return properties;
    }
}
