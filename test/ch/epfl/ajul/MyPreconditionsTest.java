package ch.epfl.ajul;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyPreconditionsTest {

    @Test
    void checkThrowsExceptionTrivial(){
        assertThrows(IllegalArgumentException.class, () -> {
            Preconditions.checkArgument(false);
        });
    }
    @Test
    void checkDoesNotThrowExceptionTrivial(){
        assertDoesNotThrow(() -> {
            Preconditions.checkArgument(true);
        });
    }

}
