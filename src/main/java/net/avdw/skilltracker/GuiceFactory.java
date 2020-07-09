package net.avdw.skilltracker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import picocli.CommandLine;

public final class GuiceFactory implements CommandLine.IFactory {
    static final MainModule MAIN_MODULE = new MainModule();
    static final Injector INJECTOR = Guice.createInjector(MAIN_MODULE);
    private static final GuiceFactory INSTANCE = new GuiceFactory();

    public static CommandLine.IFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public <K> K create(final Class<K> aClass) {
        return INJECTOR.getInstance(aClass);
    }

}
