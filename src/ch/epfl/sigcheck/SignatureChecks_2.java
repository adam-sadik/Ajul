package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

import ch.epfl.ajul.gamestate.packed.PkMove;
import ch.epfl.ajul.gamestate.packed.PkTileSet;

final class SignatureChecks_2 {
    private SignatureChecks_2() {}

    void checkPlayerId() {
        v02 = (Enum) v01;
        v03 = ch.epfl.ajul.PlayerId.ALL;
        v01 = ch.epfl.ajul.PlayerId.P1;
        v01 = ch.epfl.ajul.PlayerId.P2;
        v01 = ch.epfl.ajul.PlayerId.P3;
        v01 = ch.epfl.ajul.PlayerId.P4;
        v01 = ch.epfl.ajul.PlayerId.valueOf(v04);
        v05 = ch.epfl.ajul.PlayerId.values();
    }

    void checkMove() {
        v02 = (Record) v06;
        v06 = new ch.epfl.ajul.gamestate.Move(v07, v08, v09);
        v10 = ch.epfl.ajul.gamestate.Move.MAX_MOVES;
        v06 = ch.epfl.ajul.gamestate.Move.ofPacked(v11);
        v09 = v06.destination();
        v12 = v06.equals(v02);
        v10 = v06.hashCode();
        v11 = v06.packed();
        v07 = v06.source();
        v08 = v06.tileColor();
        v04 = v06.toString();
    }

    void checkPkMove() {
        v13 = new PkMove();
        v08 = PkMove.color(v11);
        v09 = PkMove.destination(v11);
        v11 = PkMove.pack(v07, v08, v09);
        v07 = PkMove.source(v11);
    }

    void checkPkTileSet() {
        v14 = new PkTileSet();
        v10 = PkTileSet.EMPTY;
        v10 = PkTileSet.FULL;
        v10 = PkTileSet.FULL_COLORED;
        v10 = PkTileSet.add(v10, v15);
        v10 = PkTileSet.copyColoredInto(v10, v16);
        v10 = PkTileSet.countOf(v10, v15);
        v10 = PkTileSet.difference(v10, v10);
        v12 = PkTileSet.isEmpty(v10);
        v10 = PkTileSet.of(v10, v15);
        v10 = PkTileSet.remove(v10, v15);
        v10 = PkTileSet.sampleColoredInto(v10, v16, v10, v17);
        v10 = PkTileSet.size(v10);
        v10 = PkTileSet.subsetOf(v10, v15);
        v04 = PkTileSet.toString(v10);
        v10 = PkTileSet.union(v10, v10);
    }

    ch.epfl.ajul.PlayerId v01;
    Object v02;
    java.util.List<ch.epfl.ajul.PlayerId> v03;
    String v04;
    ch.epfl.ajul.PlayerId[] v05;
    ch.epfl.ajul.gamestate.Move v06;
    ch.epfl.ajul.TileSource v07;
    ch.epfl.ajul.TileKind.Colored v08;
    ch.epfl.ajul.TileDestination v09;
    int v10;
    short v11;
    boolean v12;
    PkMove v13;
    PkTileSet v14;
    ch.epfl.ajul.TileKind v15;
    ch.epfl.ajul.TileKind.Colored[] v16;
    java.util.random.RandomGenerator v17;
}
