package domain.models

data class NeuronModel(val inputSignalCount: Int) {
    private val weights = mutableListOf<Double>()

    init {
        for (i in 1 .. inputSignalCount)
            weights.add(Math.random())
    }

    fun getWeights() : MutableList<Double> {
        return weights
    }
}