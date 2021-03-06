import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by andrei on 7/29/14.
 */
public class Board {

    private int[][] blocks;
    private int[] linearBlocks;
    private int dimension;
    private final Set<Board> neighbours = new HashSet<Board>();
    private int[][] goalArray;



    public Board(int[][] b){
        this.dimension = b.length;
        int N = this.dimension;
        this.blocks = new int[this.dimension][this.dimension];
        this.goalArray = new int[this.dimension][this.dimension];
        linearBlocks = new int[this.dimension*this.dimension+1];
        for (int i= 0;i < this.dimension; i++)
            for (int j = 0; j < this.dimension; j++){
                this.blocks[i][j] = b[i][j];
                if ((i == N-1) && (j == N-1)){
                    goalArray[i][j] = 0;
                }else {
                    goalArray[i][j] = i * N + j+1;
                }
            }

    }
    private class Block{
        private int x;
        private int y;
        public Block(int x,int y){
            this.x = x;
            this.y =y;
        }
    }
    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public int dimension(){
        return this.dimension;
    }                 // board dimension N
    private int xytTo1D(int i, int j){
        return i*this.dimension + j+1;
    }
    public int hamming(){
        int distance = 0;
        for (int i= 0;i < this.dimension; i++)
            for (int j = 0; j < this.dimension; j++){
                int linearValue = this.xytTo1D(i,j);
                this.linearBlocks[linearValue] = blocks[i][j];
            }
        for (int i = 0; i < this.dimension*this.dimension; i++){
            if (linearBlocks[i] != i){
                distance++;
            }

        }
        return distance;

    }                   // number of blocks out of place
    private Block findCorrectIndices(int value){
        for (int i= 0;i < this.dimension; i++)
            for (int j = 0; j < this.dimension; j++){
                if (goalArray[i][j] == value)
                    return new Block(i,j);
            }
        return null;
    }
    public int manhattan(){
        int manhattan = 0;
        for (int i= 0;i < this.dimension; i++)
            for (int j = 0; j < this.dimension; j++){
                if (blocks[i][j] != 0){
                    if (blocks[i][j] != goalArray[i][j]){
                        Block correctBlock = findCorrectIndices(blocks[i][j]);
                        int man = Math.abs(i-correctBlock.x)+Math.abs(j-correctBlock.y);
                        manhattan += man;
                    }
                }


            }
        return manhattan;
    }                 // sum of Manhattan distances between blocks and goal

    public boolean isGoal() {

        int N = this.dimension;

        for (int i= 0;i < N; i++)
            for (int j = 0; j < N; j++){
                if (blocks[i][j]!= goalArray[i][j]){
                    return false;
                }

            }
        return true;
    }                // is this board the goal board?
    public Board twin() {
        // initially try to swap two blocks on the first line
        int x = 0;

        final Block zero = this.findZero();
        //if zero is on the first row try the second row
        if (zero.x == 0){
            x = 1;
        }
        //copy the two dimension array
        int[][] cloneBlocks = new int[dimension][dimension];
        copy(blocks, cloneBlocks);

        //swap block[x][0] and block[x][1]
        int swap = cloneBlocks[x][0];
        cloneBlocks[x][0] = cloneBlocks[x][1];
        cloneBlocks[x][1] = swap;

        return new Board(cloneBlocks);

    }                    // a board obtained by exchanging two adjacent blocks in the same row

    private void copy(final int[][] blocks, int[][] dest) {
        for (int i = 0; i < blocks.length; i++) {
            System.arraycopy(blocks[i], 0, dest[i], 0, blocks[i].length);
        }
    }
    private Block findZero(){
        for (int i=0;i < this.dimension;i++)
            for (int j= 0;j < this.dimension;j++){
                if (blocks[i][j] == 0){
                    return new Block(i,j);
                }
            }
        throw new IllegalStateException("Couldn't find zero-th element!");
    }
    public boolean equals(Object y) {
        if (y == null) return false;
        if (this == y) return true;
        if (this.getClass() != y.getClass()) return false;

        Board boardY = (Board) y;

        if (boardY.dimension != this.dimension) return false;

        for (int i=0;i<this.dimension;i++)
            for (int j=0;j<this.dimension;j++){
                if (this.blocks[i][j] != boardY.blocks[i][j])
                    return false;
            }
        return true;
    }       // does this board equal y?
    public Iterable<Board> neighbors() {
        return new IterableBoard();

    }    // all neighboring boards
    private class IterableBoard implements Iterable<Board>{
        private Iterator<Board> setIter;
        public IterableBoard(){
            Board.this.neighbours.addAll(findNeighbours(Board.this));
            setIter = Board.this.neighbours.iterator();
        }

        @Override
        public Iterator<Board> iterator() {
            return setIter;
        }

        private Set<Board> findNeighbours(Board b){
            int dim = b.dimension;
            Set<Board> neighbours = new HashSet<Board>();
            Block zeroBlock = findZero();

            //check the position above the zero and swap if possible
            if (zeroBlock.x + 1 < dim){
                Board solutionBoard= new Board(b.blocks);

                //swap position using a swap var

                int swap = solutionBoard.blocks[zeroBlock.x][zeroBlock.y];
                solutionBoard.blocks[zeroBlock.x][zeroBlock.y] = solutionBoard.blocks[zeroBlock.x+1][zeroBlock.y];
                solutionBoard.blocks[zeroBlock.x+1][zeroBlock.y] = 0;

                neighbours.add(solutionBoard);
            }

            //check the position below the zero and swap if possible
            if (zeroBlock.x -1 >= 0){
                Board solutionBoard= new Board(b.blocks);

                //swap position using a swap var

                int swap = solutionBoard.blocks[zeroBlock.x][zeroBlock.y];
                solutionBoard.blocks[zeroBlock.x][zeroBlock.y] = solutionBoard.blocks[zeroBlock.x-1][zeroBlock.y];
                solutionBoard.blocks[zeroBlock.x-1][zeroBlock.y] = 0;

                neighbours.add(solutionBoard);
            }

            //check the position to the right of the zero and swap if possible
            if (zeroBlock.y + 1 < dim){
                Board solutionBoard= new Board(b.blocks);

                //swap position using a swap var

                int swap = solutionBoard.blocks[zeroBlock.x][zeroBlock.y];
                solutionBoard.blocks[zeroBlock.x][zeroBlock.y] = solutionBoard.blocks[zeroBlock.x][zeroBlock.y+1];
                solutionBoard.blocks[zeroBlock.x][zeroBlock.y+1] = 0;

                neighbours.add(solutionBoard);
            }

            //check the position below the zero and swap if possible
            if (zeroBlock.y -1 >= 0){
                Board solutionBoard= new Board(b.blocks);

                //swap position using a swap var

                int swap = solutionBoard.blocks[zeroBlock.x][zeroBlock.y];
                solutionBoard.blocks[zeroBlock.x][zeroBlock.y] = solutionBoard.blocks[zeroBlock.x][zeroBlock.y-1];
                solutionBoard.blocks[zeroBlock.x][zeroBlock.y-1] = 0;

                neighbours.add(solutionBoard);
            }

            return neighbours;
        }
    }
    public String toString() {
        String output = "";
        output += String.format("%2d\n", this.dimension);
        for (int i=0;i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                output += String.format(" %2d", blocks[i][j]);

            }
            output += "\n";
        }
        return output;
    }               // string representation of the board (in the output format specified below)

    public static void main(String[] args){
        int[][] blocks;
        File f = null;
        f = new File("src/puzzle04.txt");
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(f);
        }catch(IOException e){
            System.out.println(e);
        }

        int N = inputStream.nextInt();
        blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = inputStream.nextInt();
            }
            inputStream.nextLine();
        }
        Board b = new Board(blocks);

        System.out.print("Initial:\n"+b+"\n");
        System.out.print("Hamming distance: "+b.hamming()+"\n");
        System.out.print("Manhattan distance: "+b.manhattan()+"\n");
        for (Board bI: b.neighbors()){
            System.out.print(bI+"\n");

        }

    }
}
