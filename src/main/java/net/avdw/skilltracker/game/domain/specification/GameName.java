package net.avdw.skilltracker.game.domain.specification;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import net.avdw.repository.AbstractSpecification;
import net.avdw.skilltracker.domain.Game;

@Value
@EqualsAndHashCode(callSuper = true)
public class GameName extends AbstractSpecification<Game> {
    @NonNull String name;

    public GameName(String name) {
        this.name = name;
    }

    @Override
    public boolean isSatisfiedBy(Game game) {
        return name.equals(game.getName());
    }
}
