package examples.tictactoe

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TicTacToeTests  {

    private var ticTacToe = TicTacToe()

    @Test
    fun should_end_game_when_all_fields_are_taken() {
        ticTacToe = TicTacToe(mutableSetOf(1, 3, 5, 8), mutableSetOf(2, 4, 6, 7))
        ticTacToe.makeMove(9)
        assertTrue(ticTacToe.isGameOver)
    }

    @Test
    fun should_end_game_when_player_took_winning_sequence() {
        ticTacToe = TicTacToe(mutableSetOf(1, 2, 3), mutableSetOf(4, 8, 9))
        assertTrue(ticTacToe.isGameOver)
    }

    @Test
    fun should_allow_only_free_fields_to_be_taken() {
        ticTacToe.makeMove(1)
        assertFalse(ticTacToe.makeMove(1))
    }

    @Test
    fun should_allow_only_on_board_fields_to_be_taken() {
        assertFalse(ticTacToe.makeMove(10))
    }

    @Test
    fun should_change_player_after_field_is_taken() {
        val player = ticTacToe.player
        ticTacToe.makeMove(1)
        assertNotEquals(ticTacToe.player, player)
    }

    @Test
    fun should_not_change_player_after_field_is_not_taken() {
        ticTacToe.makeMove(1)
        val player = ticTacToe.player
        ticTacToe.makeMove(1)
        assertEquals(ticTacToe.player, player)
    }

    @Test
    fun should_throw_exception_when_initial_game_state_is_invalid() {
        assertThrows(IllegalArgumentException::class.java) { TicTacToe(mutableSetOf(1, 3, 5, 8), mutableSetOf(1, 4, 6, 7, 9)) }
    }

}