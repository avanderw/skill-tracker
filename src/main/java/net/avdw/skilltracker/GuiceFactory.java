package net.avdw.skilltracker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import picocli.CommandLine;

final public class GuiceFactory implements CommandLine.IFactory {
    private static final GuiceFactory INSTANCE = new GuiceFactory();
    private static final Injector INJECTOR = Guice.createInjector(new MainModule());

    public static CommandLine.IFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public <K> K create(final Class<K> aClass) {
        return INJECTOR.getInstance(aClass);
    }

}
