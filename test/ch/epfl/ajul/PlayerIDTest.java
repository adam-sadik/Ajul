package ch.epfl.ajul;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerIDTest {

    @Test
    void enumContainsExactlyFourPlayersInOrder(){
        PlayerId [] values = PlayerId.values();

        assertEquals(4,values.length);
        assertEquals(PlayerId.P1, values[0]);
        assertEquals(PlayerId.P2, values[1]);
        assertEquals(PlayerId.P3, values[2]);
        assertEquals(PlayerId.P4, values[3]);
    }

    @Test
    void allContainsAllPlayersInCorrectOrder() {
        List<PlayerId> all = PlayerId.ALL;

        assertEquals(4, all.size());
        assertEquals(PlayerId.P1, all.get(0));
        assertEquals(PlayerId.P2, all.get(1));
        assertEquals(PlayerId.P3, all.get(2));
        assertEquals(PlayerId.P4, all.get(3));
    }

    @Test
    void allIsImmutable() {
        assertThrows(UnsupportedOperationException.class,
                () -> PlayerId.ALL.add(PlayerId.P1));
    }

}
