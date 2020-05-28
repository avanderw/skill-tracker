package net.avdw.property;

import com.google.inject.Inject;
import org.tinylog.Logger;

import java.util.Properties;

public class PropertyConfigurer {
    private final Properties defaultProperties;
    private final Properties localProperties;
    private final Properties globalProperties;

    @Inject
    public PropertyConfigurer(@Default final Properties defaultProperties,
                              @Local final Properties localProperties,
                              @Global final Properties globalProperties) {
        this.defaultProperties = defaultProperties;
        this.localProperties = localProperties;
        this.globalProperties = globalProperties;
    }

    public Properties configure() {
        Properties prioritizedProperties = new Properties();
        prioritizedProperties.putAll(defaultProperties);
        prioritizedProperties.putAll(localProperties);
        prioritizedProperties.putAll(globalProperties);
        prioritizedProperties.forEach((key, value) -> Logger.debug("{}={}", key, value));
        return prioritizedProperties;
    }
}
