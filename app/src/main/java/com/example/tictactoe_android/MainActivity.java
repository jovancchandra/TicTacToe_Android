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
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[] buttons = new Button[9];
    private String[] board = new String[9];

    //private boolean playerTurn = true;

    private int turnCount;

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

        for (int i = 0; i < 9; i++) {
                String buttonID = "button_" + i; //button_0, button_1, ..., button_8
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i] = findViewById(resID);
                buttons[i].setOnClickListener(this);
        }

        for (int i = 0; i < 9; i++) {
            board[i] = buttons[i].getText().toString();
        }

        Random rand = new Random();
        int start = rand.nextInt(2);
        if (start == 0) {   //BOT Starts
            Toast.makeText(this, "BOT Starts", Toast.LENGTH_SHORT).show();
            botMove();
            turnCount++;
        } else {
            Toast.makeText(this, "You have the first move!", Toast.LENGTH_SHORT).show();
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean gameOver = false;

        if (!((Button) v).getText().toString().equals("")) {     //Checks if button does not have empty string (already used)
            return;
        }

        //Player's Turn
        ((Button) v).setText("X");
        System.out.println("Player Turn: " + turnCount);
        //playerTurn = !playerTurn;

        for (int i = 0; i < 9; i++) {
            board[i] = buttons[i].getText().toString();
        }

        turnCount++;
        if (winCheck(board, "X")) {
            playerWins();
            gameOver = true;
        } else if (turnCount == 9) {
            tie();
            gameOver = true;
        }

        //BOT's Turn
        System.out.println("Bot Turn: " + turnCount);
        if (gameOver == false)
            botMove();
        //playerTurn = !playerTurn;

        for (int i = 0; i < 9; i++) {
            board[i] = buttons[i].getText().toString();
        }

        turnCount++;
        if (winCheck(board, "O")) {
            botWins();
        } else if (turnCount == 9) {
            tie();
        }
    }

    private void botMove() {
        ArrayList<Integer > emptyPositions = new ArrayList<Integer>();
        ArrayList<Integer> playerPositions = new ArrayList<Integer>();
        ArrayList<Integer> emptyCorners = new ArrayList<Integer>();
        ArrayList<Integer> playerCorners = new ArrayList<Integer>();
        ArrayList<Integer> emptySides = new ArrayList<Integer>();

        for (int i = 0; i < 9; i++) {
            if (board[i].equals("")) {
                emptyPositions.add(i);
            }

            if (board[i].equals("X")) {
                playerPositions.add(i);
            }
        }

        for (int x : emptyPositions) {
            if (x == 0 || x == 2 || x == 6 || x == 8) {
                emptyCorners.add(x);
            }
        }

        for (int x : playerPositions) {
            if (x == 0 || x == 2 || x == 6 || x == 8) {
                playerCorners.add(x);
            }
        }

        for (int x : emptyPositions) {
            if (x == 1 || x == 3 || x == 5 || x == 7) {
                emptySides.add(x);
            }
        }

        //Win - BOT goes for the win
        ArrayList<Integer> winningMoves = futureStep(board, "O");
        if (winningMoves.size() >= 1) {
            int move = selectRandom(winningMoves);
            buttons[move].setText("O");
            return;
        }

        //Block - BOT blocks Player from winning
        ArrayList<Integer> blockMoves = futureStep(board, "X");
        if (blockMoves.size() >= 1) {
            int move = selectRandom(blockMoves);
            buttons[move].setText("O");
            return;
        }


        //Fork - BOT does a move where he has 2 ways to win the following move
        for (int move1 : emptyPositions) {
            int wins = 0;
            String copy[] = new String[board.length];

            for (int i = 0; i < board.length; i++) {
                copy[i] = board[i];                     //Resets board each move test
            }

            copy[move1] = "O";

            ArrayList<Integer> forkMoves = futureStep(copy, "O");
            if (forkMoves.size() >= 2) {
                int move = move1;
                buttons[move].setText("O");
                return;
            }
        }

        //Fork Block - BOT blocks a possible fork by player
        ArrayList<Integer> possibleForks = new ArrayList<Integer>();

        for (int move1 : emptyPositions) {
            int wins = 0;
            String copy[] = new String[board.length];

            for (int i = 0; i < board.length; i++) {
                copy[i] = board[i];                     //Resets board each move test
            }

            copy[move1] = "X";
            ArrayList<Integer> forkMoves = futureStep(copy, "X");
            if (forkMoves.size() >= 2) {
                possibleForks.add(move1);
            }
        }

        if (possibleForks.size() == 1) { //Player has exactly 1 possible fork move
            int move = possibleForks.get(0);    //Block that move
            buttons[move].setText("O");
            return;
        } else if (possibleForks.size() > 1) {  //Player can do multiple forks
            //Set-up for 2 in a row
            ArrayList<Integer> setupMoves = new ArrayList<Integer>();

            for (int move1 : emptyPositions) {
                int wins = 0;
                String copy[] = new String[board.length];

                for (int i = 0; i < board.length; i++) {
                    copy[i] = board[i];                     //Resets board each move test
                }

                copy[move1] = "O";
                ArrayList<Integer> twoInARow = futureStep(copy, "O");

                if (twoInARow.size() > 0) {
                    //Fork Check
                    for (int move2 : twoInARow) {
                        copy[move2] = "X";
                        ArrayList<Integer> playerWins = futureStep(copy, "X");

                        if (playerWins.size() < 2) {    //Player can't fork
                            setupMoves.add(move1);
                        }
                    }
                }
            }

            if (setupMoves.size() > 0) {
                int move = selectRandom(setupMoves);
                buttons[move].setText("O");
                return;
            }
        }

        //Center
        if (emptyPositions.contains(4) && turnCount != 0) {
            buttons[4].setText("O");
            return;
        }

        //Corners
        if (emptyCorners.size() > 0) {
            ArrayList<Integer> oppositeCorners = new ArrayList<Integer>();
            for (int x : playerCorners) {
                if (x == 0) {
                    if (emptyCorners.contains(8)) {
                        oppositeCorners.add(8);
                    }
                } else if (x == 2) {
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

            //Opposite Corner
            if (oppositeCorners.size() > 0) {
                int move = selectRandom(oppositeCorners);
                buttons[move].setText("O");
                return;
            } else { //Random Empty Corner
                int move = selectRandom(emptyCorners);
                System.out.println(emptyCorners);
                System.out.println("Move: " + move);
                buttons[move].setText("O");
                return;
            }
        }

        //Empty Side
        if (emptySides.size() > 0) {
            int move = selectRandom(emptySides);
            buttons[move].setText("O");
            return;
        }

        return;
    }

    private ArrayList<Integer> futureStep(String[] b, String mark) {
        ArrayList<Integer > emptyPositions = new ArrayList<Integer>();
        ArrayList<Integer> winningMoves = new ArrayList<Integer>();
        String copy[] = new String[b.length];

        for (int i = 0; i < 9; i++) {
            if (b[i].equals("")) {
                emptyPositions.add(i);
            }
        }

        for (int move : emptyPositions) {
            for (int i = 0; i < b.length; i++) {
                copy[i] = b[i];                     //Resets board each move test
            }
            copy[move] = mark;
            if (winCheck(copy, mark)) {
                winningMoves.add(move);
            }
        }

        return winningMoves;
    }

    private int selectRandom(ArrayList<Integer> li) {
        Random rand = new Random();
        int lowerBound = 0;
        int upperBound = li.size();
        int rng = rand.nextInt(upperBound - lowerBound) + lowerBound;
        return li.get(rng);
    }


    private boolean winCheck(String[] b, String mark) {
        return ( (b[0] ==  b[1] && b[0] ==  b[2] && b[0] == mark) ||
                (b[3] ==  b[4] && b[3] ==  b[5] && b[3] == mark) ||
                (b[6] ==  b[7] && b[6] ==  b[8] && b[6] == mark) ||
                (b[0] ==  b[3] && b[0] ==  b[6] && b[0] == mark) ||
                (b[1] ==  b[4] && b[1] ==  b[7] && b[1] == mark) ||
                (b[2] ==  b[5] && b[2] ==  b[8] && b[2] == mark) ||
                (b[0] ==  b[4] && b[0] ==  b[8] && b[0] == mark) ||
                (b[2] ==  b[4] && b[2] ==  b[6] && b[2] == mark) );
    }

    private void playerWins() {
        playerPoints++;
        Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void botWins() {
        botPoints++;
        Toast.makeText(this, "You lose!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void tie() {
        Toast.makeText(this, "Tie!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText() {
        textViewPlayer.setText("YOU: " + playerPoints);
        textViewBot.setText("BOT: " + botPoints);
    }

    private void resetBoard() {
        for (int i = 0; i < 9; i++) {
           buttons[i].setText("");
           board[i] = "";
        }

        turnCount = 0;

        Random rand = new Random();
        int start = rand.nextInt(2);
        if (start == 0) {   //BOT Starts
            Toast.makeText(this, "BOT Starts", Toast.LENGTH_SHORT).show();
            botMove();
            turnCount++;
        } else {
            Toast.makeText(this, "You have the first move!", Toast.LENGTH_SHORT).show();
        }

        //playerTurn = true;
    }

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
        //outState.putBoolean("playerTurn", playerTurn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        turnCount = savedInstanceState.getInt("turnCount");
        playerPoints = savedInstanceState.getInt("playerPoints");
        botPoints = savedInstanceState.getInt("botPoints");
        //playerTurn = savedInstanceState.getBoolean("playerTurn");
    }
}
