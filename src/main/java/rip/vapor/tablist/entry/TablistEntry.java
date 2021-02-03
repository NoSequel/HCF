package rip.vapor.tablist.entry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TablistEntry {

    private final int x;
    private final int y;
    private final String text;
    private final int ping;

}
