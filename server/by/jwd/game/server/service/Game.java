package by.jwd.game.server.service;

public class Game {

    private volatile char[] board = new char[]{'1', '2', '3',
            '4', '5', '6',
            '7', '8', '9'};


    public synchronized String getBoard() {
        StringBuilder boardForClient = new StringBuilder();
        int count = 0;
        for (int i = 0; i < 9; i++) {
            boardForClient.append(board[i]).append(" | ").append(board[i + 1]).append(" | ").append(board[i + 2]).append("\n");
            count++;
            i = i + 2;
            if (i != 8) {
                boardForClient.append("---------\n");
                count++;
            }
        }
        return boardForClient.toString();
    }

    public synchronized String replace(int number, char type) {
        for (int i = 0; i < board.length; i++) {
            if (number == board[i] - '0') {
                board[i] = type;
            }
        }
        return getBoard();
    }

    public boolean checkWin() {
        return checkWinHorizontal() || checkWinVertical() || checkWinDiagonal();

    }

    private boolean checkWinHorizontal() {
        if (Character.valueOf(board[0]).equals(board[1]) && Character.valueOf(board[1]).equals(board[2])) {
            return true;
        } else if (Character.valueOf(board[3]).equals(board[4]) && Character.valueOf(board[4]).equals(board[5])) {
            return true;
        } else if (Character.valueOf(board[6]).equals(board[7]) && Character.valueOf(board[7]).equals(board[8])) {
            return true;
        }
        return false;
    }

    private boolean checkWinVertical() {
        if (Character.valueOf(board[0]).equals(board[3]) && Character.valueOf(board[3]).equals(board[6])) {
            return true;
        } else if (Character.valueOf(board[1]).equals(board[4]) && Character.valueOf(board[4]).equals(board[7])) {
            return true;
        } else if (Character.valueOf(board[2]).equals(board[5]) && Character.valueOf(board[5]).equals(board[8])) {
            return true;
        }
        return false;
    }

    private boolean checkWinDiagonal() {
        if (Character.valueOf(board[0]).equals(board[4]) && Character.valueOf(board[4]).equals(board[8])) {
            return true;
        } else if (Character.valueOf(board[2]).equals(board[4]) && Character.valueOf(board[4]).equals(board[6])) {
            return true;
        }
        return false;
    }

}
