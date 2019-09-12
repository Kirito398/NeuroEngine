package domain.interactors

import domain.models.NeuronModel
import kotlin.math.exp

class NetworkInteractor {
    private val layers = mutableListOf<MutableList<NeuronModel>>()
    private val E = 0.7
    private val A = 0.0
    var currentSignal = mutableListOf<Double>()
    var networkAnswer = 0

    fun addLayer(neuronCount: Int, inputSignalCount: Int) {
        val newLayer = mutableListOf<NeuronModel>()

        for (i in 1 .. neuronCount)
            newLayer.add(NeuronModel(inputSignalCount))

        layers.add(newLayer)
    }

    fun sendSignal(signal: MutableList<Double>): Int {
        currentSignal = signal

        for (layer in layers)
            currentSignal = calculateSignalFromLayer(currentSignal, layer)

        getNetworkAnswer()
        return networkAnswer
    }

    fun learn(idealAnswer: Int) {
        val deltaOut = (idealAnswer - networkAnswer) * (1.0 - networkAnswer) * networkAnswer

        for (neuron in layers.last()) {
            val grad = neuron.value * deltaOut
            val deltaWeight = E * grad + A * neuron.delta
            neuron.delta = ((1 - neuron.value) * neuron.value) * (neuron.getWeights()[networkAnswer] * deltaOut)
            neuron.getWeights()[networkAnswer] += deltaWeight
        }
    }

    fun showNetwork() {
        for ((layerIndex, layer) in layers.withIndex()) {
            println("Layer $layerIndex ---------------------------------> ")
            for ((neuronIndex, neuron) in layer.withIndex()) {
                println("Neuron $neuronIndex:")
                showNeuron(neuron)
            }
        }
    }

    fun showNeuron(neuron: NeuronModel) {
        for (item in neuron.getWeights())
            print("$item ")
        print("\n")
    }

    private fun calculateSignalFromLayer(signal: MutableList<Double>, layer: MutableList<NeuronModel>): MutableList<Double> {
        val newSignal = mutableListOf<Double>()

        for (neuron in layer)
            newSignal.add(calculateAnswerFromNeuron(signal, neuron))

        return newSignal
    }

    private fun calculateAnswerFromNeuron(signal: MutableList<Double>, neuron: NeuronModel): Double {
        val weights = neuron.getWeights()
        val length = minOf(signal.size, weights.size)

        var result = 0.0
        for (i in 0 until length)
            result += signal[i] * weights[i]

        neuron.value = activateFunction(result)

        return neuron.value
    }

    private fun activateFunction(x: Double): Double {
        return 1.0 / (1.0 + exp(-x))
    }

    private fun getNetworkAnswer() {
        var max = 0.0
        var maxI = 0
        for ((index, item) in currentSignal.withIndex()) {
            if (item > max) {
                max = item
                maxI = index
            }
        }
        networkAnswer = maxI
    }
}