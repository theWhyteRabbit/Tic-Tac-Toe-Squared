package com.diusrex.tictactoe;

public class TicTacToeEngine {

    public static boolean applyMove(BoardStatus board, Move move) {
        if (isInvalidMove(board, move))
            return false;

        board.setBoxOwner(move.getPosition(), move.getPlayer());
        updateSectionOwner(board, move);
        return true;
    }

    private static boolean isInvalidMove(BoardStatus board, Move move) {
        return isOutsideBounds(board, move) || isOutOfOrder(board, move)
                || isInWrongSection(board, move) || isAlreadyOwned(board, move)
                || move.getPlayer() == Player.Unowned;
    }

    private static boolean isOutsideBounds(BoardStatus board, Move move) {
        return !board.isInsideBounds(move.getPosition());
    }

    private static boolean isOutOfOrder(BoardStatus board, Move move) {
        int p1Count = board.getPlayerCount(Player.Player_1);
        int p2Count = board.getPlayerCount(Player.Player_2);

        if (move.getPlayer() == Player.Player_1)
            ++p1Count;

        else
            ++p2Count;

        // The two times where it will fail
        return p1Count < p2Count || p1Count > p2Count + 1;
    }

    private static boolean isAlreadyOwned(BoardStatus board, Move move) {
        return board.getBoxOwner(move.getPosition()) != Player.Unowned;
    }

    private static boolean isInWrongSection(BoardStatus board, Move move) {
        SectionPosition requiredSection = board.getSectionToPlayIn();
        SectionPosition actualSection = move.getSectionIn();

        // Checks if it is in the correct section
        if (requiredSection.equals(actualSection))
            return false;

        // The only other option is if it is full
        return !sectionIsFull(board, requiredSection);
    }

    private static boolean sectionIsFull(BoardStatus board,
            SectionPosition requiredSection) {
        BoxPosition offset = requiredSection.getTopLeftPosition();

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                BoxPosition positionInSection = new BoxPosition(x, y);
                if (board.getBoxOwner(positionInSection.increaseBy(offset)) == Player.Unowned)
                    return false;
            }
        }

        return true;
    }

    private static void updateSectionOwner(BoardStatus board, Move move) {
        SectionPosition changedSection = move.getSectionIn();
        // Cannot take a section from other player
        if (board.getSectionOwner(changedSection) != Player.Unowned)
            return;

        BoxPosition pos = move.getPosition();

        if (isHorizontalComplete(board, pos) || isVerticalComplete(board, pos)
                || isDiagonalComplete(board, pos))
            board.setSectionOwner(changedSection, move.getPlayer());
    }

    private static boolean isHorizontalComplete(BoardStatus board,
            BoxPosition pos) {
        BoxPosition startPos = new BoxPosition(
                convertToStartingSectionPos(pos.getX()), pos.getY());
        BoxPosition increase = new BoxPosition(1, 0);

        return sectionLineOwnedByPlayer(board, startPos, increase);
    }

    private static boolean isVerticalComplete(BoardStatus board, BoxPosition pos) {
        BoxPosition startPos = new BoxPosition(pos.getX(),
                convertToStartingSectionPos(pos.getY()));
        BoxPosition increase = new BoxPosition(0, 1);

        return sectionLineOwnedByPlayer(board, startPos, increase);
    }

    private static boolean isDiagonalComplete(BoardStatus board, BoxPosition pos) {
        // From (0, 0) to (2, 0) within section
        BoxPosition startPos = new BoxPosition(
                convertToStartingSectionPos(pos.getX()),
                convertToStartingSectionPos(pos.getY()));
        BoxPosition increase = new BoxPosition(1, 1);
        if (sectionLineOwnedByPlayer(board, startPos, increase))
            return true;

        // From (0, 2) to (2, 0) within section
        startPos = startPos.increaseBy(new BoxPosition(0, 2));
        increase = new BoxPosition(1, -1);
        return sectionLineOwnedByPlayer(board, startPos, increase);
    }

    private static int convertToStartingSectionPos(int x) {
        return (x / BoardStatus.SIZE_OF_SECTION) * BoardStatus.SIZE_OF_SECTION;
    }

    private static boolean sectionLineOwnedByPlayer(BoardStatus board,
            BoxPosition startPos, BoxPosition increase) {
        Player expectedPlayer = board.getBoxOwner(startPos);
        if (expectedPlayer == Player.Unowned)
            return false;

        BoxPosition currentPos = startPos;
        for (int i = 0; i < 2; ++i) {
            currentPos = currentPos.increaseBy(increase);
            if (board.getBoxOwner(currentPos) != expectedPlayer)
                return false;
        }

        return true;
    }

    public static SectionPosition getSectionToPlayInNext(BoxPosition pos) {
        SectionPosition sectionIn = pos.getSectionIn();
        pos = pos.decreaseBy(sectionIn.getTopLeftPosition());

        return new SectionPosition(pos.getX(), pos.getY());
    }

    public static Player getWinner(BoardStatus board) {
        Player winner = getHorizontalWinnerOrUnowned(board);
        if (winner != Player.Unowned)
            return winner;

        winner = getVerticalWinnerOrUnowned(board);
        if (winner != Player.Unowned)
            return winner;
        
        return getDiagnonalWinnerOrUnowned(board);
    }

    private static Player getHorizontalWinnerOrUnowned(BoardStatus board) {
        SectionPosition increase = new SectionPosition(1, 0);

        for (int startY = 0; startY < 3; ++startY) {
            SectionPosition startPos = new SectionPosition(0, startY);

            Player winner = getLineWinnerOrUnowned(board, startPos, increase);
            if (winner != Player.Unowned)
                return winner;
        }

        return Player.Unowned;
    }

    private static Player getVerticalWinnerOrUnowned(BoardStatus board) {
        SectionPosition increase = new SectionPosition(0, 1);

        for (int startX = 0; startX < 3; ++startX) {
            SectionPosition startPos = new SectionPosition(startX, 0);

            Player winner = getLineWinnerOrUnowned(board, startPos, increase);
            if (winner != Player.Unowned)
                return winner;
        }

        return Player.Unowned;
    }

    private static Player getDiagnonalWinnerOrUnowned(BoardStatus board) {
        SectionPosition increase = new SectionPosition(1, 1);

        SectionPosition startPos = new SectionPosition(0, 0);

        Player winner = getLineWinnerOrUnowned(board, startPos, increase);
        if (winner != Player.Unowned)
            return winner;

        increase = new SectionPosition(-1,  1);
        startPos = new SectionPosition(2, 0);
        
        return getLineWinnerOrUnowned(board, startPos, increase);
    }
    
    private static Player getLineWinnerOrUnowned(BoardStatus board,
            SectionPosition startPos, SectionPosition increase) {
        Player expectedPlayer = board.getSectionOwner(startPos);

        SectionPosition currentPos = startPos.increaseBy(increase);
        for (int i = 0; i < 2; ++i, currentPos = currentPos
                .increaseBy(increase)) {
            if (board.getSectionOwner(currentPos) != expectedPlayer)
                return Player.Unowned;
        }

        return expectedPlayer;
    }
}
