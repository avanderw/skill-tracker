package net.avdw.skilltracker.cli.player.view;

public class MovePlayerView {

    private static final String TEMPLATE = "Change name from %s to %s.%n";
    private static final String SAME_TEMPLATE = "%s is the same. No action taken.%n";
    private static final String MERGE_TEMPLATE = "%s exists! Merged %s into existing player.";

    public String render(MovePlayerModel model) {
        StringBuilder render = new StringBuilder();
        render.append(String.format(TEMPLATE, model.getFrom(), model.getTo()));

        if (model.isSame()) {
            render.append(String.format(SAME_TEMPLATE, model.getTo()));
        }

        if (model.isMerge()) {
            render.append(String.format(MERGE_TEMPLATE, model.getTo(), model.getFrom()));
        }

        return render.toString();
    }
}
