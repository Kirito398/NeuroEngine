package domain.models

data class NeuronModel(private val inputSignalCount: Int) {
    private val weights = mutableListOf<Double>()
    var value = 0.0
    var delta = 0.0

    init {
        for (i in 1 .. inputSignalCount)
            weights.add(Math.random())
    }

    fun getWeights() : MutableList<Double> {
        return weights
    }
}