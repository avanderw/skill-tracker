package net.avdw.skilltracker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import picocli.CommandLine;

final class TestGuiceFactory implements CommandLine.IFactory {
    private static final TestGuiceFactory INSTANCE = new TestGuiceFactory();
    private static Injector INJECTOR;

    public static CommandLine.IFactory getInstance(TestModule testModule) {
        INJECTOR = Guice.createInjector(testModule);
        return INSTANCE;
    }

    @Override
    public <K> K create(final Class<K> aClass) {
        return INJECTOR.getInstance(aClass);
    }

}
