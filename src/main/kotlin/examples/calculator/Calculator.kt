package examples.calculator

class Calculator {

    fun add(leftHandSide: Int, rightHandSide: Int) = leftHandSide + rightHandSide

    fun substract(leftHandSide: Int, rightHandSide: Int) = leftHandSide - rightHandSide

    fun divide(leftHandSide: Int, rightHandSide: Int): Int {
        require(rightHandSide != 0)
        return leftHandSide / rightHandSide
    }

}