package net.avdw.property;

import com.google.inject.AbstractModule;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class AbstractPropertyModule extends AbstractModule {
    protected Properties configureProperties() {
        Path localPropertyPath = Paths.get(String.format("%s/%s", globalDirectory(), propertyFileName()));
        Path globalPropertyPath = Paths.get(System.getProperty("user.home"))
                .resolve(Paths.get(String.format("net.avdw/%s/%s", globalDirectory(), propertyFileName())));
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        Properties localProperties = propertyFileReader.read(localPropertyPath);
        Properties globalProperties = propertyFileReader.read(globalPropertyPath);
        Properties defaultProperties = defaultProperties();
        PropertyConfigurer propertyConfigurer = new PropertyConfigurer(defaultProperties, localProperties, globalProperties);
        return propertyConfigurer.configure();
    }


    protected String globalDirectory() {
        String globalDirectory = this.getClass().getPackage().getName();
        globalDirectory = globalDirectory.replace("net.avdw.", "");
        if (globalDirectory.contains(".")) {
            globalDirectory = globalDirectory.substring(0, globalDirectory.lastIndexOf("."));
            globalDirectory = globalDirectory.replace('.', '/');
        }
        return globalDirectory;
    }

    protected String propertyFileName() {
        String propertyFileName = this.getClass().getPackage().getName();
        propertyFileName = propertyFileName.substring(propertyFileName.lastIndexOf(".") + 1);
        propertyFileName = String.format("%s.properties", propertyFileName);
        return propertyFileName;
    }

    protected abstract Properties defaultProperties();
}
