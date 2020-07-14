package net.avdw.skilltracker;

import net.avdw.property.PropertyFileReader;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MainVersion implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        if (getClass().getPackage().getImplementationVersion() == null) {

            Path pomProperties = Paths.get("target/maven-archiver/pom.properties");
            if (Files.exists(pomProperties)) {
                Properties properties = new PropertyFileReader().read(pomProperties);
                return new String[]{properties.getProperty("version")};
            } else {
                Logger.error("Implementation version not found. This class is intended for use with Maven:\n" +
                        "<archive>\n" +
                        "    <manifest>\n" +
                        "        <addDefaultImplementationEntries>true</addDefaultImplementationEntries>\n" +
                        "        <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>\n" +
                        "    </manifest>\n" +
                        "</archive>");
                throw new UnsupportedOperationException();
            }
        } else {
            return new String[]{getClass().getPackage().getImplementationVersion()};
        }
    }
}
