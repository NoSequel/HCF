package rip.vapor.tablist.entry;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TablistElement {

    private final List<TablistEntry> entries = new ArrayList<>();

    /**
     * Get a {@link TablistEntry} from the {@link TablistElementSupplier}
     *
     * @param x the x position
     * @param y the y position
     * @return the tablist entry
     */
    public TablistEntry getEntry(int x, int y) {
        return this.entries.stream()
                .filter(entry -> entry.getX() == x && entry.getY() == y)
                .findFirst().orElseGet(() -> new TablistEntry(x, y, "", -1));
    }

    /**
     * Add a {@link TablistEntry} to the list of entries
     *
     * @param x    the x position
     * @param y    the y position
     * @param text the displayed text
     * @param ping the displayed ping
     */
    public void add(int x, int y, String text, int ping) {
        this.add(new TablistEntry(x, y, text, ping));
    }

    /**
     * Add a {@link TablistEntry} to the list of entries, defaults ping value to -1
     *
     * @param x    the x position
     * @param y    the y position
     * @param text the displayed text
     */
    public void add(int x, int y, String text) {
        this.add(x, y, text, -1);
    }

    /**
     * Add a {@link TablistEntry} to the list of entries
     *
     * @param entry the entry
     */
    public void add(TablistEntry entry) {
        this.entries.add(entry);
    }
}