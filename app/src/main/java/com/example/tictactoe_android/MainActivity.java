package com.example.tictactoe_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
/**
 * @author      Jovan C. Chandra
 * @version     1.1
 * @since       1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[] buttons = new Button[9];   //Array of Buttons displayed in the app
    private String[] board = new String[9];     //Array of strings containing the text in each button

    private int turnCount;                      //Tracks which turn a game is on

    private int playerPoints;
    private int botPoints;

    private TextView textViewPlayer;
    private TextView textViewBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPlayer = findViewById(R.id.text_view_player);
        textViewBot = findViewById(R.id.text_view_bot);

        //Assign the button array to the buttons created in activity_main.xml
        for (int i = 0; i < 9; i++) {
                String buttonID = "button_" + i; //button_0, button_1, ..., button_8
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i] = findViewById(resID);
                buttons[i].setOnClickListener(this);
        }

        //Initializes the string array with the text of each button ("" or "X" or "O")
        updateBoard();

        //Initializes turn to 1
        turnCount = 1;
        whoStarts();

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    /**
     * Determines whether the player or the BOT moves first.
     */
    private void whoStarts() {
        Random rand = new Random();
        int start = rand.nextInt(2);    //Generates either 0 or 1;

        if (start == 0) {   //If the BOT starts first, call the BOT's algorithm before user input (clicking a button)
            Toast.makeText(this, "BOT Starts", Toast.LENGTH_SHORT).show();
            botMove();
        } else {
            Toast.makeText(this, "You have the first move!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the string array with the contents of the button after a turn.
     */
    private void updateBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = buttons[i].getText().toString();
        }
    }

    @Override
    public void onClick(View v) {
        //If the button clicked is already occupied, return immediately.
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        ///PLAYER'S TURN
        ((Button) v).setText("X");
        turnCount++;
        updateBoard();                  //Player's turn is over, update the board.

        if (roundOver("X")) {       //Check if game is over after player's last turn
            return;
        }

        ///BOT'S TURN
        botMove();
        turnCount++;
        updateBoard();

        if (roundOver("O")) {       //Check if game is over after BOT's last turn
            return;
        }
    }

    /**
     * Checks whether the round is over, signifying someone's victory or a tie.
     *
     * @param mark The mark signifying the player ("X") or the BOT ("O").
     * @return TRUE if the round is over, FALSE if not.
     */
    private boolean roundOver(String mark) {
        if (winCheck(board, mark)) {        //Check the updated board to see if someone has won
            if (mark.equals("O")) {
                botWins();
            } else if (mark.equals("X")) {
                playerWins();
            }
            return true;
        } else if (isBoardFull(board)) {    //Check the update board to see if game ends in a tie
            tie();
            return true;
        }
        return false;
    }
    /**
     * Checks if someone has won the round.
     *
     * @param b The board to be checked.
     * @param mark  The mark signifying the player ("X") or the BOT ("O").
     * @return  TRUE if someone won, FALSE if not.
     */
    private boolean winCheck(String[] b, String mark) {
        return ( (b[0].equals(b[1]) && b[0].equals(b[2]) && b[0].equals(mark)) ||
                (b[3].equals(b[4]) && b[3].equals(b[5]) && b[3].equals(mark)) ||
                (b[6].equals(b[7]) && b[6].equals(b[8]) && b[6].equals(mark)) ||
                (b[0].equals(b[3]) && b[0].equals(b[6]) && b[0].equals(mark)) ||
                (b[1].equals(b[4]) && b[1].equals(b[7]) && b[1].equals(mark)) ||
                (b[2].equals(b[5]) && b[2].equals(b[8]) && b[2].equals(mark)) ||
                (b[0].equals(b[4]) && b[0].equals(b[8]) && b[0].equals(mark)) ||
                (b[2].equals(b[4]) && b[2].equals(b[6]) && b[2].equals(mark)) );
    }

    /**
     * Check if the board is full.
     *
     * @param b The board to be checked.
     * @return TRUE if board is full, FALSE if not.
     */
    private boolean isBoardFull(String [] b) {
        return (!Arrays.asList(b).contains(""));
    }

    /**
     * Run the Tic-Tac-Toe BOT's algorithm.
     * The algorithm can be found at https://en.wikipedia.org/wiki/Tic-tac-toe#Strategy
     */
    private void botMove() {
        ArrayList<Integer > emptySpaces = new ArrayList<Integer>();     //A list of indices in the current state of the board that is not occupied
        ArrayList<Integer> emptyCorners = new ArrayList<Integer>();     //A list of indices in the current state of the board that are corners is not occupied
        ArrayList<Integer> emptySides = new ArrayList<Integer>();       //A list of indices in the current state of the board that are sides is not occupied
        ArrayList<Integer> playerSpaces = new ArrayList<Integer>();     //A list of indices in the current state of the board that is occupied by the Player
        ArrayList<Integer> playerCorners = new ArrayList<Integer>();    //A list of indices in the current state of the board that are corners and occupied by the Player


        for (int i = 0; i < 9; i++) {
            if (board[i].equals("")) {      //Find all empty/unoccupied spaces in the board
                emptySpaces.add(i);
            }

            if (board[i].equals("X")) {     //Find all spaces occupied by the player in the board
                playerSpaces.add(i);
            }
        }

        for (int x : emptySpaces) {                      //From the empty spaces in the board
            if (x == 0 || x == 2 || x == 6 || x == 8) {     //Find all that are corners
                emptyCorners.add(x);
            }

            if (x == 1 || x == 3 || x == 5 || x == 7) {     //Find all that are sides
                emptySides.add(x);
            }
        }

        for (int x : playerSpaces) {                     //From the spaces occupied by the player in the board
            if (x == 0 || x == 2 || x == 6 || x == 8) {     //Find all that are corners
                playerCorners.add(x);
            }
        }

        //1. Win - BOT goes for the win
        ArrayList<Integer> winningMoves = futureStep(board, "O");   //A list of indices in the current state of the board where,
                                                                          //by occupying the space represented by the index, the BOT will win.
        if (winningMoves.size() >= 1) {             //If there is at least one space that results in the BOT's victory upon occupying it,
            int move = selectRandom(winningMoves);  //Randomly select any of those spaces and occupy them.
            buttons[move].setText("O");
            return;
        }

        //2. Block - BOT blocks Player from winning
        ArrayList<Integer> blockMoves = futureStep(board, "X");   //A list of indices in the current state of the board where,
                                                                        //by occupying the space represented by the index, the player will win.

        if (blockMoves.size() >= 1) {               //If there is at least one space that results in the player's victory upon them occupying it,
            int move = selectRandom(blockMoves);    //Randomly select any of those spaces and occupy them.
            buttons[move].setText("O");             //(Note: If the size of blockMoves is more than 1, it means the player will win regardless of blocking.)
            return;
        }


        //3. Fork - BOT occupies a space that would create 2 ways for the BOT to win with 1 more move each.
        ArrayList<Integer> possibleBotForks = new ArrayList<Integer>();    //A list of indices in the current state of the board where,
                                                                  //by occupying the space represented by the index, the BOT can fork.
        for (int move1 : emptySpaces) {                 //For each possible move the BOT can make (for each empty space)
        String[] copy = freshCopy(board);               //Create a fresh copy of the current state of the board

            copy[move1] = "O";                                           //Occupy the space with the BOT's mark (1st step into the future)
            ArrayList<Integer> forkMoves = futureStep(copy, "O");  //Returns a list of moves that, after the first move above, would result in BOT's win (2nd step into the future)
            if (forkMoves.size() >= 2) {        //If there are more than 2 ways the BOT can win after making the first move above, that move is a fork.
                possibleBotForks.add(move1);
            }
        }

        if (possibleBotForks.size() >= 1) {                 //If there is at least one fork move
            int move = selectRandom(possibleBotForks);      //Randomly select one of them and have the BOT occupy the space
            buttons[move].setText("O");
            return;
        }

        //4. Fork Block - BOT occupies a space that would, upon being occupied by the player, create 2 ways for the player to win with 1 more move each.
        ArrayList<Integer> possibleForks = new ArrayList<Integer>();    //A list of indices in the current state of the board where,
                                                                  //by occupying the space represented by the index, the player can fork.
        for (int move1 : emptySpaces) {                 //For each possible move the player can make (for each empty space)
            String[] copy = freshCopy(board);           //Create a fresh copy of the current state of the board

            copy[move1] = "X";
            ArrayList<Integer> forkMoves = futureStep(copy, "X");   //Occupy the space with the player's mark (1st step into the future)
            if (forkMoves.size() >= 2) {                                  //Returns a list of moves that, after the first move above, would result in player's win (2nd step into the future)
                possibleForks.add(move1);                                 //If there are more than 2 ways the player can win after making the first move above, that move is a fork.
            }
        }

        if (possibleForks.size() == 1) {        //If the player has exactly 1 fork move (exactly 1 move that would create 2 ways for the player to win in 1 more move)
            int move = possibleForks.get(0);    //Have the BOT block that fork move by occupying the space first
            buttons[move].setText("O");
            return;
        } else if (possibleForks.size() > 1) {  //If the player has multiple fork moves, the bot should set-up a two in a row to force the player into defending
                                                //As long as it doesn't result the player in creating a fork.
            ArrayList<Integer> setupMoves = new ArrayList<Integer>();   //A list of indices the current state of the board where,
                                                                  //by occupying the space represented by the index, the BOT will have a two in a row
                                                                  //(Similar to fork, but instead of having 2 ways to win we only need 1 way to win)
            for (int move1 : emptySpaces) {
                String []copy = freshCopy(board);

                copy[move1] = "O";
                ArrayList<Integer> twoInARow = futureStep(copy, "O");

                if (twoInARow.size() >= 1) {        //The same as the fork algorithm above (3), but only check for at least 1 way to win, signifying a two in a row
                    for (int move2 : twoInARow) {       //BOT needs to check whether the two in a row will result the player in creating a fork
                        copy[move2] = "X";
                        ArrayList<Integer> playerWins = futureStep(copy, "X");

                        if (playerWins.size() < 2) {    //Same algorithm as fork again, but this time check for the inverse (< 2 instead of >= 2)
                            setupMoves.add(move1);      //As it would tell the BOT the player can't fork.
                        }
                    }
                }
            }

            if (setupMoves.size() >= 1) {               //If there is at least one move that will create a two in a row for the BOT
                                                        //such that the player can't create a fork
                int move = selectRandom(setupMoves);
                buttons[move].setText("O");             //Randomly select on of them and have the BOT occupy it
                return;
            }
        }

        //5. Center - BOT occupies the center square UNLESS it is the very first turn (only happens if BOT has the first move)
        if (emptySpaces.contains(4) && turnCount != 1) {
            buttons[4].setText("O");
            return;
        }

        //Corners
        if (emptyCorners.size() > 0) {
            ArrayList<Integer> oppositeCorners = new ArrayList<Integer>();  //A list of indices in the current state of the board
                                                                      //representing empty corners opposite to player-occupied corners
            for (int x : playerCorners) {
                if (x == 0) {                               //Corner 0 is opposite to corner 8
                    if (emptyCorners.contains(8)) {
                        oppositeCorners.add(8);
                    }
                } else if (x == 2) {                        //Corner 2 is opposite to corner 6
                    if (emptyCorners.contains(6)) {
                        oppositeCorners.add(6);
                    }
                } else if (x == 6) {
                    if (emptyCorners.contains(2)) {
                        oppositeCorners.add(2);
                    }
                } else if (x == 8) {
                    if (emptyCorners.contains(0)) {
                        oppositeCorners.add(0);
                    }
                }
            }

            //6. Opposite Corner - BOT occupies an empty corner opposite of a corner occupied by the player
            if (oppositeCorners.size() >= 1) {
                int move = selectRandom(oppositeCorners);
                buttons[move].setText("O");
                return;
            } else { //7. Empty Corner - BOT occupies a random empty corner
                int move = selectRandom(emptyCorners);
                System.out.println(emptyCorners);
                System.out.println("Move: " + move);
                buttons[move].setText("O");
                return;
            }
        }

        //8. Empty Side - BOT occupies a random empty side
        if (emptySides.size() >= 1) {
            int move = selectRandom(emptySides);
            buttons[move].setText("O");
            return;
        }

        //If none of this is possible, it means the board is full and a game results in a tie
        //The game's status (like ending in a tie) is handled in the roundOver method called in the onClick method
    }

    /**
     * Creates and returns a fresh copy of board b.
     *
     * @param b The board to be copied.
     * @return The copy of the board.
     */
    private String[] freshCopy(String[] b) {
        String[] copy = Arrays.copyOf(b, b.length);
        return copy;
    }

    /**
     * Steps into the future once to check if a move would result in someone's victory.
     * Creates a copy of the board to be checked, occupy the empty spaces in the said board
     * one by one, each time checking whether occupying the space results in a victory.
     *
     * @param b The board to be checked
     * @param mark The mark signifying the player ("X") or the BOT ("O").
     * @return A list containing indices in b that would result in a victory for the entity represented by mark.
     */
    private ArrayList<Integer> futureStep(String[] b, String mark) {
        ArrayList<Integer > emptySpaces = new ArrayList<Integer>(); //A list of indices in b that is not occupied
        ArrayList<Integer> winningMoves = new ArrayList<Integer>(); //A list of indices in b that, if occupied by mark, results in a victory for mark.

        for (int i = 0; i < 9; i++) {
            if (b[i].equals("")) {
                emptySpaces.add(i);
            }
        }

        for (int move : emptySpaces) {                  //For each possible move mark can make (for each empty space)
            String[] copy = freshCopy(b);               //Create a fresh copy of b each move check
            copy[move] = mark;                          //Occupy the current empty space checked with mark
            if (winCheck(copy, mark)) {                 //Check if occupying that spot results in a win for mark
                winningMoves.add(move);                 //If it does, add it to the winningMoves list that will be returned
            }
        }

        return winningMoves;
    }

    /**
     * Returns a random integer from a list of integers.
     *
     * @param li The list a random integer is extracted from.
     * @return The random integer.
     */
    private int selectRandom(ArrayList<Integer> li) {
        Random rand = new Random();
        int lowerBound = 0;
        int upperBound = li.size();
        int rng = rand.nextInt(upperBound - lowerBound) + lowerBound;
        return li.get(rng);
    }


    /**
     * A message pop up signifying the player's victory.
     */
    private void playerWins() {
        playerPoints++;
        Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    /**
     * A message pop up signifying the BOT's victory and thus the player's loss.
     */
    private void botWins() {
        botPoints++;
        Toast.makeText(this, "You lose!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    /**
     * A message pop up signifying a tied game round.
     */
    private void tie() {
        Toast.makeText(this, "Tie!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    /**
     * Updates the point text at the top of the screen in the app's GUI.
     */
    private void updatePointsText() {
        textViewPlayer.setText("YOU: " + playerPoints);
        textViewBot.setText("BOT: " + botPoints);
    }

    /**
     * Resets the board and buttons.
     */
    private void resetBoard() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            board[i] = "";
        }

        turnCount = 1;

        whoStarts();    //Determines who starts the new game round.
    }

    /**
     * Resets the game completely, including the player and BOT scores.
     */
    private void resetGame() {
        playerPoints = 0;
        botPoints = 0;
        updatePointsText();
        resetBoard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("turnCount", turnCount);
        outState.putInt("playerPoints", playerPoints);
        outState.putInt("botPoints", botPoints);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        turnCount = savedInstanceState.getInt("turnCount");
        playerPoints = savedInstanceState.getInt("playerPoints");
        botPoints = savedInstanceState.getInt("botPoints");
    }
}
