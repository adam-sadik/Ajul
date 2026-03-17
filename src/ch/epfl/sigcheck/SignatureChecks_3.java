package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

import ch.epfl.ajul.gamestate.packed.PkFloor;
import ch.epfl.ajul.gamestate.packed.PkPatterns;

final class SignatureChecks_3 {
    private SignatureChecks_3() {}

    void checkPkPatterns() {
        v01 = new PkPatterns();
        v02 = PkPatterns.EMPTY;
        v02 = PkPatterns.asPkTileSet(v02);
        v05 = PkPatterns.canContain(v02, v03, v04);
        v04 = PkPatterns.color(v02, v03);
        v05 = PkPatterns.isFull(v02, v03);
        v02 = PkPatterns.size(v02, v03);
        v06 = PkPatterns.toString(v02);
        v02 = PkPatterns.withAddedTiles(v02, v03, v02, v04);
        v02 = PkPatterns.withEmptyLine(v02, v03);
    }

    void checkPkFloor() {
        v07 = new PkFloor();
        v02 = PkFloor.EMPTY;
        v02 = PkFloor.asPkTileSet(v02);
        v05 = PkFloor.containsFirstPlayerMarker(v02);
        v02 = PkFloor.size(v02);
        v08 = PkFloor.tileAt(v02, v02);
        v06 = PkFloor.toString(v02);
        v02 = PkFloor.withAddedTiles(v02, v02);
    }

    void checkPreconditions() {
        v09 = new ch.epfl.ajul.Preconditions();
        ch.epfl.ajul.Preconditions.checkArgument(v05);
    }

    void checkGame() {
        v10 = new ch.epfl.ajul.Game(v11);
        v02 = v10.centralAreaMaxSize();
        v12 = v10.factories();
        v02 = v10.factoriesCount();
        v11 = v10.playerDescriptions();
        v13 = v10.playerIds();
        v02 = v10.playersCount();
        v14 = v10.tileSources();
        v02 = v10.tileSourcesCount();
    }

    void checkGame_PlayerDescription() {
        v16 = (Record) v15;
        v15 = new ch.epfl.ajul.Game.PlayerDescription(v17, v06, v18);
        v05 = v15.equals(v16);
        v02 = v15.hashCode();
        v17 = v15.id();
        v18 = v15.kind();
        v06 = v15.name();
        v06 = v15.toString();
    }

    void checkGame_PlayerDescription_PlayerKind() {
        v16 = (Enum) v18;
        v18 = ch.epfl.ajul.Game.PlayerDescription.PlayerKind.AI;
        v18 = ch.epfl.ajul.Game.PlayerDescription.PlayerKind.HUMAN;
        v18 = ch.epfl.ajul.Game.PlayerDescription.PlayerKind.valueOf(v06);
        v19 = ch.epfl.ajul.Game.PlayerDescription.PlayerKind.values();
    }

    PkPatterns v01;
    int v02;
    ch.epfl.ajul.TileDestination.Pattern v03;
    ch.epfl.ajul.TileKind.Colored v04;
    boolean v05;
    String v06;
    PkFloor v07;
    ch.epfl.ajul.TileKind v08;
    ch.epfl.ajul.Preconditions v09;
    ch.epfl.ajul.Game v10;
    java.util.List<ch.epfl.ajul.Game.PlayerDescription> v11;
    java.util.List<ch.epfl.ajul.TileSource.Factory> v12;
    java.util.List<ch.epfl.ajul.PlayerId> v13;
    java.util.List<ch.epfl.ajul.TileSource> v14;
    ch.epfl.ajul.Game.PlayerDescription v15;
    Object v16;
    ch.epfl.ajul.PlayerId v17;
    ch.epfl.ajul.Game.PlayerDescription.PlayerKind v18;
    ch.epfl.ajul.Game.PlayerDescription.PlayerKind[] v19;
}
