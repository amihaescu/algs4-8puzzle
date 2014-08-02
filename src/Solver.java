import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by andrei on 8/1/14.
 */
public class Solver {

    private List<Board> solutionBoards = new LinkedList<Board>();
    MinPQ<ComparableBoard> minPQ = new MinPQ<ComparableBoard>();
    private static final int[][] GOAL_ARRAY = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private ComparableBoard solutionBoard = new ComparableBoard(new Board(GOAL_ARRAY));
    private int moves;
    private Board initial;

    private class ComparableBoard implements Comparable<ComparableBoard>{

        private Board comparableBoard;

        public ComparableBoard(Board b){
           this.comparableBoard = b;
        }

        public Board getBoard(){
            return this.comparableBoard;
        }

        @Override
        public int compareTo(ComparableBoard that){
            if (that == null){
                throw new IllegalArgumentException();
            }

            final Board thatBoard = that.getBoard();

            if (thatBoard == this.comparableBoard){
                return 0;
            }

            int manDist = comparableBoard.manhattan();
            int hamDist = comparableBoard.hamming();

            int thatManDist = that.getBoard().manhattan();
            int thatHamDist = that.getBoard().hamming();

            if (manDist > thatManDist || hamDist > thatHamDist){
                return 1;
            } else if (manDist < thatManDist || hamDist < thatHamDist){
                return -1;
            }

            return 0;

        }

        @Override
        public boolean equals(Object o){
            if (o.getClass() != this.getClass()){
                return false;
            }

            ComparableBoard boardObject = (ComparableBoard) o;

            if (this == boardObject){
                return true;
            }

            if (this.getBoard().equals(boardObject.getBoard())){
                return true;
            }
            return false;
        }
    }
    private int solve(Board initial){
        int m = 0;
        solutionBoards = new LinkedList<Board>();
        solutionBoards.add(initial);
        minPQ = new MinPQ<ComparableBoard>();
        minPQ.insert(new ComparableBoard(initial));
        final double maxIter = Math.pow(initial.dimension(), 13);

        ComparableBoard b = minPQ.delMin();
        while(!b.equals(solutionBoard)){
            minPQ = new MinPQ<ComparableBoard>();
            m++;
            Iterable<Board> neighbours = b.getBoard().neighbors();
            for (Board iteratorB:neighbours){
                if (!solutionBoards.contains(iteratorB)) {
                    minPQ.insert(new ComparableBoard(iteratorB));
                }
            }
            if (minPQ.isEmpty()){
                return -1;
            }
            b = minPQ.delMin();
            solutionBoards.add(b.getBoard());
            if (m > maxIter){
                return -1;
            }
        }
        return m;

    }

    public Solver(Board initial) {
        this.initial = initial;
        this.moves = solve(this.initial);

    }            // find a solution to the initial board (using the A* algorithm)
    public boolean isSolvable() {
        if (this.initial.isGoal()) {
            return true;
        }

        Board twinB = initial.twin();

        final int twinMoves = solve(twinB);
        final int initMoves = solve(this.initial);



        return (initMoves >= 0) && (twinMoves == -1);


    }             // is the initial board solvable?
    public int moves() {
        return moves;

    }                      // min number of moves to solve initial board; -1 if no solution
    public Iterable<Board> solution() {
        return solutionBoards;

    }      // sequence of boards in a shortest solution; null if no solution
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

    } // solve a slider puzzle (given below)
}

