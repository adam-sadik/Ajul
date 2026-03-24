package ch.epfl.ajul;

import ch.epfl.ajul.gamestate.ImmutableGameState;
import ch.epfl.ajul.gamestate.Move;
import ch.epfl.ajul.gamestate.MutableGameState;
import ch.epfl.ajul.gamestate.ReadOnlyGameState;
import ch.epfl.ajul.gamestate.packed.PkFloor;
import ch.epfl.ajul.gamestate.packed.PkPatterns;
import ch.epfl.ajul.gamestate.packed.PkPlayerStates;
import ch.epfl.ajul.gamestate.packed.PkTileSet;
import ch.epfl.ajul.gamestate.packed.PkWall;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.random.RandomGenerator;

public final class AjulTUI {

    static void printState(ReadOnlyGameState gameState) {
        int nbSources = gameState.pkTileSources().size();
        System.out.print("Fabriques :");
        for (int i = 1; i < nbSources; i++) {
            System.out.printf(" [%d] %-10s", i, formatTileSet(gameState.pkTileSources().get(i)));
            if (i % 3 == 0 && i < nbSources - 1) {
                System.out.print("\n           ");
            }
        }
        System.out.println();
        System.out.printf("   Centre : [0] %s\n\n", formatTileSet(gameState.pkTileSources().get(0)));

        for (PlayerId pId : gameState.playerIds()) {
            System.out.printf(" %-10s %d pts\n", pId.name(), PkPlayerStates.points(gameState.pkPlayerStates(), pId));
            int patterns = PkPlayerStates.pkPatterns(gameState.pkPlayerStates(), pId);
            int wall = PkPlayerStates.pkWall(gameState.pkPlayerStates(), pId);

            for (TileDestination.Pattern p : TileDestination.Pattern.ALL) {
                System.out.printf(" %7s | %s\n", formatPatternLine(patterns, p), formatWallLine(wall, p));
            }

            int floor = PkPlayerStates.pkFloor(gameState.pkPlayerStates(), pId);
            System.out.printf(" Plancher: %s\n\n", formatFloor(floor));
        }
    }

    private static String formatTileSet(int pkTileSet) {
        StringBuilder sb = new StringBuilder();
        for (TileKind kind : TileKind.ALL) {
            int count = PkTileSet.countOf(pkTileSet, kind);
            for (int i = 0; i < count; i++) {
                if (kind == TileKind.FIRST_PLAYER_MARKER) sb.append("1 ");
                else sb.append(kind.toString()).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private static String formatPatternLine(int pkPatterns, TileDestination.Pattern line) {
        int size = PkPatterns.size(pkPatterns, line);
        int capacity = line.capacity();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < capacity - size; i++) sb.append(".");
        if (size > 0) {
            String colorName = PkPatterns.color(pkPatterns, line).name();
            sb.append(colorName.repeat(size));
        }
        return String.format("%5s", sb.toString());
    }

    private static String formatWallLine(int pkWall, TileDestination.Pattern line) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < PkWall.WALL_WIDTH; col++) {
            TileKind.Colored expectedColor = PkWall.colorAt(line, col);
            if (PkWall.hasTileAt(pkWall, line, expectedColor)) {
                sb.append(expectedColor.name());
            } else {
                sb.append(expectedColor.name().toLowerCase());
            }
        }
        return sb.toString();
    }

    private static String formatFloor(int pkFloor) {
        StringBuilder sb = new StringBuilder();
        int size = PkFloor.size(pkFloor);
        for (int i = 0; i < size; i++) {
            TileKind kind = PkFloor.tileAt(pkFloor, i);
            if (kind == TileKind.FIRST_PLAYER_MARKER) sb.append("1 ");
            else sb.append(kind.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    static Move queryNextMove(String playerName, ReadOnlyGameState gameState) {
        Scanner scanner = new Scanner(System.in);
        short[] validMovesPacked = new short[Move.MAX_MOVES];
        int nbValidMoves = gameState.validMoves(validMovesPacked);

        while (true) {
            System.out.printf("Quel coup désirez-vous jouer, %s ?\n> ", playerName);
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.length() != 3) {
                continue;
            }

            char sourceChar = input.charAt(0);
            char colorChar = input.charAt(1);
            char destChar = input.charAt(2);

            if (sourceChar < '0' || sourceChar > '9') continue;
            int sourceIndex = sourceChar - '0';
            if (sourceIndex >= gameState.pkTileSources().size()) continue;
            TileSource source = TileSource.ALL.get(sourceIndex);

            if (colorChar < 'A' || colorChar > 'E') continue;
            TileKind.Colored color = TileKind.Colored.valueOf(String.valueOf(colorChar));

            if (destChar < '0' || destChar > '5') continue;
            int destIndex = destChar - '0';
            TileDestination dest = destIndex == 0 ? TileDestination.FLOOR : TileDestination.Pattern.ALL.get(destIndex - 1);

            Move move = new Move(source, color, dest);
            short packedMove = move.packed();

            for (int i = 0; i < nbValidMoves; i++) {
                if (validMovesPacked[i] == packedMove) {
                    return move;
                }
            }
            System.out.println("Coup invalide. Veuillez réessayer.");
        }
    }

    public static void main(String[] args) {
        RandomGenerator randomGenerator = new Random();

        List<Game.PlayerDescription> playerInfos = List.of(
                new Game.PlayerDescription(PlayerId.P1, "Adam", Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.P2, "Rayane", Game.PlayerDescription.PlayerKind.HUMAN)
        );

        Map<PlayerId, String> playerNames = Map.of(
                PlayerId.P1, "Adam",
                PlayerId.P2, "Rayane"
        );

        Game game = new Game(playerInfos);
        MutableGameState gameState = new MutableGameState(ImmutableGameState.initial(game));

        gameState.fillFactories(randomGenerator);

        while (!gameState.isGameOver()) {
            printState(gameState);
            String playerName = playerNames.get(gameState.currentPlayerId());
            Move move = queryNextMove(playerName, gameState);
            gameState.registerMove(move.packed());

            if (gameState.isRoundOver()) {
                gameState.endRound();
                if (!gameState.isGameOver()) {
                    gameState.fillFactories(randomGenerator);
                }
            }
        }
        gameState.endGame();

        printState(gameState);
        System.out.println("Partie terminée ! Scores finaux :");
        for (PlayerId pId : gameState.playerIds()) {
            System.out.printf("%s : %d points\n", playerNames.get(pId), PkPlayerStates.points(gameState.pkPlayerStates(), pId));
        }
    }
}
