package domain.models

import java.math.BigDecimal

data class NeuronModel(val inputSignalCount: Int) {
    private val weights = mutableListOf<BigDecimal>()

    init {
        for (i in 1 .. inputSignalCount)
            weights.add(BigDecimal(Math.random()))
    }

    fun getWeights() : MutableList<BigDecimal> {
        return weights
    }
}