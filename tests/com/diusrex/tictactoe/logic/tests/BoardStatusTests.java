package com.diusrex.tictactoe.logic.tests;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.diusrex.tictactoe.data_structures.BoardStatus;
import com.diusrex.tictactoe.data_structures.BoxPosition;
import com.diusrex.tictactoe.data_structures.Move;
import com.diusrex.tictactoe.data_structures.Player;
import com.diusrex.tictactoe.data_structures.SectionPosition;

public class BoardStatusTests {
    BoardStatus board;

    @Before
    public void setup() {
        board = new BoardStatus();
    }

    @Test
    public void testGetNextPlayer() {
        Assert.assertEquals(Player.Player_1, board.getNextPlayer());

        TestUtils.applyMoveToBoard(board, new Move(board.getSectionToPlayIn(), BoxPosition.make(1, 1), Player.Player_1));

        Assert.assertEquals(Player.Player_2, board.getNextPlayer());
    }

    @Test
    public void allPositionsStartAsUnowned() {
        for (SectionPosition section : SectionPosition.allSections()) {
            for (BoxPosition box : BoxPosition.allBoxesInSection()) {
                Assert.assertEquals(Player.Unowned, board.getBoxOwner(section, box));
            }
        }
    }

    @Test
    public void testIsNotInsideBoundsBox() {
        SectionPosition validSection = SectionPosition.make(0, 0);
        BoxPosition invalidPos = BoxPosition.make(0, BoardStatus.SIZE_OF_SECTION);
        Assert.assertFalse(board.isInsideBounds(validSection, invalidPos));

        invalidPos = BoxPosition.make(BoardStatus.SIZE_OF_SECTION, 0);
        Assert.assertFalse(board.isInsideBounds(validSection, invalidPos));

        invalidPos = BoxPosition.make(-1, 0);
        Assert.assertFalse(board.isInsideBounds(validSection, invalidPos));

        invalidPos = BoxPosition.make(0, -1);
        Assert.assertFalse(board.isInsideBounds(validSection, invalidPos));
    }

    @Test
    public void testIsNotInsideBoundsSection() {
        SectionPosition invalidSection = SectionPosition.make(BoardStatus.NUMBER_OF_SECTIONS_PER_SIDE, 0);
        BoxPosition validPos = BoxPosition.make(0, 0);
        Assert.assertFalse(board.isInsideBounds(invalidSection, validPos));

        invalidSection = SectionPosition.make(0, BoardStatus.NUMBER_OF_SECTIONS_PER_SIDE);
        Assert.assertFalse(board.isInsideBounds(invalidSection, validPos));

        invalidSection = SectionPosition.make(-1, 0);
        Assert.assertFalse(board.isInsideBounds(invalidSection, validPos));

        invalidSection = SectionPosition.make(0, -1);
        Assert.assertFalse(board.isInsideBounds(invalidSection, validPos));
    }

    @Test
    public void testIsInsideBounds() {
        /* Test all sections */
        for (SectionPosition section : SectionPosition.allSections()) {
            for (BoxPosition boxPosition : BoxPosition.allBoxesInSection()) {
                Assert.assertTrue(board.isInsideBounds(section, boxPosition));
            }
        }
    }

    @Test
    public void testAddingMovesToStack() {
        Assert.assertEquals(0, getMovesSize());

        SectionPosition sectionPos = SectionPosition.make(0, 0);
        BoxPosition movePos = BoxPosition.make(0, 0);
        Move move = new Move(sectionPos, movePos, Player.Player_1);

        board.applyMove(move);

        Assert.assertEquals(1, getMovesSize());
        Assert.assertEquals(move, board.getAllMoves().peek());
    }

    private int getMovesSize() {
        return board.getAllMoves().size();
    }
}
