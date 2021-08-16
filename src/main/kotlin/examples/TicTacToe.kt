package examples

import java.util.Collections.disjoint
import java.util.stream.Collectors.toSet
import java.util.stream.Stream.concat

class TicTacToe(private var crossFields: MutableSet<Int> = mutableSetOf(), private var circleFields: MutableSet<Int> = mutableSetOf()) {

    var player = Player.CROSS
        private set

    init {
        require(disjoint(crossFields, circleFields) || takenFields().size >= BOARD_SIZE)
    }

    val isGameOver: Boolean
        get() = allFieldsAreTaken() || playerTookWinningSequence()

    private fun allFieldsAreTaken() = BOARD_SIZE - takenFields().size == 0

    private fun takenFields() = concat(crossFields.stream(), circleFields.stream()).collect(toSet())

    private fun playerTookWinningSequence() = winningSequences.any { playerFields().containsAll(it) }

    fun makeMove(field: Int): Boolean {
        if (isTaken(field) || !isOnBoard(field)) {
            return false
        }
        playerFields().add(field)
        player = player.reverse()
        return true
    }

    private fun isTaken(field: Int) = takenFields().contains(field)

    private fun isOnBoard(field: Int) = field < BOARD_SIZE + 1

    private fun playerFields() = if (player === Player.CROSS) crossFields else circleFields

    fun getCrossFields(): Set<Int> = crossFields

    fun getCircleFields(): Set<Int> = circleFields

    companion object {

        private const val BOARD_SIZE = 9

        private val winningSequences = setOf(
            setOf(1, 2, 3), setOf(4, 5, 6), setOf(7, 8, 9),
            setOf(1, 4, 7), setOf(2, 5, 8), setOf(3, 6, 9),
            setOf(1, 5, 9), setOf(3, 5, 7)
        )

    }

}