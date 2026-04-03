package ch.epfl.ajul.mcts;

public final class MctsNode {

    private static final int NO_MOVE_NO_GAMES = 0b1111111111;
    private static final int NB_OF_BITS_OF_A_MOVE = 10;

    private int moveAndNbOfSimulatedGames;
    private int totalPoints;
    MctsNode[] nodeKids;

    private MctsNode ( int moveAndNbOfSimulatedGames, int totalPoints, MctsNode [] nodekids){
        this.moveAndNbOfSimulatedGames= moveAndNbOfSimulatedGames;
        this.totalPoints = totalPoints;
        this.nodeKids = nodekids;
    }

    private void gamesAdder( int nbofgames) {
        moveAndNbOfSimulatedGames = moveAndNbOfSimulatedGames & NO_MOVE_NO_GAMES
                |  ((gameCount() + nbofgames) << NB_OF_BITS_OF_A_MOVE );
    }

    public static MctsNode newRoot() { return new MctsNode( (NO_MOVE_NO_GAMES | ( 1 << NB_OF_BITS_OF_A_MOVE)),0,null); }

    public static MctsNode newMove(int move) { return new MctsNode(move, 0, null); }

    public int pkMove() { return moveAndNbOfSimulatedGames & NO_MOVE_NO_GAMES ; }

    public int gameCount() { return moveAndNbOfSimulatedGames >>> NB_OF_BITS_OF_A_MOVE;}

    public int totalPoints() { return totalPoints; }

    public double averagePoints() { return totalPoints / ((double) gameCount()); }

    public void registerEvaluation(int points){
        totalPoints += points;
        gamesAdder(1);
    }

    public int indexOfChildToExplore () {
        if ( gameCount() <= nodeKids.length){
            return gameCount() - 1;
        }
        else {
            double priority = 0;
            double newPriority;
            final double C = 80.0;
            int indexPriority = 0;
            double logParent = 2*Math.log(gameCount());

            for (int i = 0; i < nodeKids.length ; ++i){
                newPriority = nodeKids[i].averagePoints()
                        + C*Math.sqrt(logParent/ (double) nodeKids[i].gameCount());
                if ( newPriority > priority){
                    priority = newPriority;
                    indexPriority = i;
                }
            }
            return indexPriority;
        }
    }
}
