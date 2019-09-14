package domain.models

data class NeuronModel(private val inputSignalCount: Int) {
    private var weights = mutableListOf<Double>()
    private val deltaWeights = mutableListOf<Double>()
    var value = 0.0
    var delta = 0.0

    init {
        for (i in 1 .. inputSignalCount) {
            weights.add(Math.random())
            deltaWeights.add(0.0)
        }
    }

    fun getWeights() : MutableList<Double> {
        return weights
    }

    fun getDeltaWeights() : MutableList<Double> {
        return deltaWeights
    }

    fun setWeight(weights: MutableList<Double>) {
        this.weights = weights
    }

    fun updateWeight(deltaWeight: Double, index: Int) {
        weights[index] += deltaWeight
    }
}