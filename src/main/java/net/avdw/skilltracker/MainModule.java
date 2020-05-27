package net.avdw.skilltracker;

import com.google.inject.AbstractModule;

import java.util.LinkedList;
import java.util.List;

class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(List.class).to(LinkedList.class);
    }
}
