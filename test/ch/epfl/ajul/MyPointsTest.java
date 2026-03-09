package ch.epfl.ajul;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyPointsTest {
    @Test
    void newWallTilePointsWorksForAllValidCombinations() {
        for (int h = 1; h <= 5; h++) {
            for (int v = 1; v <= 5; v++) {
                int expected;
                if (h == 1 && v == 1) expected = 1;
                else if (h == 1) expected = v;
                else if (v == 1) expected = h;
                else expected = h + v;

                assertEquals(expected, Points.newWallTilePoints(h, v));
            }
        }
    }

    @Test
    void floorPenaltyIsExactlyCorrectForAllIndices() {
        int[] expectedPenalties = {1, 1, 2, 2, 2, 3, 3}; // Index 0 à 6
        for (int i = 0; i < 7; i++) {
            assertEquals(expectedPenalties[i], Points.floorPenalty(i), "Erreur à l'index " + i);
        }
    }

    @Test
    void totalFloorPenaltyIsExactlyCorrectForAllCounts() {
        int[] expectedTotals = {0, 1, 2, 4, 6, 8, 11, 14}; // 0 à 7 tuiles
        for (int count = 0; count <= 7; count++) {
            assertEquals(expectedTotals[count], Points.totalFloorPenalty(count), "Erreur pour " + count + " tuiles");
        }
    }

    @Test
    void newWallTilePoints_isolatedTile_returnsOne() {
        assertEquals(1, Points.newWallTilePoints(1, 1));
    }

    @Test
    void newWallTilePoints_horizontalGroupOnly_returnsHorizontalSize() {
        assertEquals(2, Points.newWallTilePoints(2, 1));
        assertEquals(5, Points.newWallTilePoints(5, 1));
    }

    @Test
    void newWallTilePoints_verticalGroupOnly_returnsVerticalSize() {
        assertEquals(3, Points.newWallTilePoints(1, 3));
        assertEquals(4, Points.newWallTilePoints(1, 4));
    }

    @Test
    void newWallTilePoints_crossGroup_returnsSum() {
        assertEquals(4, Points.newWallTilePoints(2, 2));
        assertEquals(10, Points.newWallTilePoints(5, 5));
        assertEquals(7, Points.newWallTilePoints(3, 4));
    }

    @Test
    void floorPenalty_firstTwoTiles_penaltyIsOne() {
        assertEquals(1, Points.floorPenalty(0));
        assertEquals(1, Points.floorPenalty(1));
    }

    @Test
    void floorPenalty_middleThreeTiles_penaltyIsTwo() {
        assertEquals(2, Points.floorPenalty(2));
        assertEquals(2, Points.floorPenalty(3));
        assertEquals(2, Points.floorPenalty(4));
    }

    @Test
    void floorPenalty_lastTwoTiles_penaltyIsThree() {
        assertEquals(3, Points.floorPenalty(5));
        assertEquals(3, Points.floorPenalty(6));
    }

    @Test
    void totalFloorPenalty_zeroTiles_returnsZero() {
        assertEquals(0, Points.totalFloorPenalty(0));
    }

    @Test
    void totalFloorPenalty_oneTile_returnsOne() {
        assertEquals(1, Points.totalFloorPenalty(1));
    }

    @Test
    void totalFloorPenalty_twoTiles_returnsTwo() {
        assertEquals(2, Points.totalFloorPenalty(2));
    }

    @Test
    void totalFloorPenalty_fourTiles_returnsSix() {

        assertEquals(6, Points.totalFloorPenalty(4));
    }

    @Test
    void totalFloorPenalty_sevenTiles_returnsFourteen() {
        assertEquals(14, Points.totalFloorPenalty(7));
    }
}
