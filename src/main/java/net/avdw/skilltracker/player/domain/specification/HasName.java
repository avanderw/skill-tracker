package net.avdw.skilltracker.player.domain.specification;

import lombok.Getter;
import lombok.NonNull;
import net.avdw.repository.AbstractSpecification;
import net.avdw.skilltracker.domain.Player;

public class HasName extends AbstractSpecification<Player> {
    @NonNull @Getter private final String name;

    public HasName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public boolean isSatisfiedBy(Player player) {
        return player.getName().equals(name);
    }
}
