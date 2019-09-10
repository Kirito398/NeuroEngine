package domain.interactors

import domain.models.NeuronModel
import java.math.BigDecimal


class NetworkInteractor {
    private val layers = mutableListOf<MutableList<NeuronModel>>()

    fun addLayer(neuronCount: Int, inputSignalCount: Int) {
        val newLayer = mutableListOf<NeuronModel>()

        for (i in 1 .. neuronCount)
            newLayer.add(NeuronModel(inputSignalCount))

        layers.add(newLayer)
    }

    fun sendSignal(signal: MutableList<BigDecimal>): MutableList<BigDecimal> {
        var currentSignal = signal

        for (layer in layers)
            currentSignal = calculateSignalFromLayer(currentSignal, layer)

        return currentSignal
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

    private fun calculateSignalFromLayer(signal: MutableList<BigDecimal>, layer: MutableList<NeuronModel>): MutableList<BigDecimal> {
        val newSignal = mutableListOf<BigDecimal>()

        for (neuron in layer)
            newSignal.add(calculateAnswerFromNeuron(signal, neuron))

        return newSignal
    }

    private fun calculateAnswerFromNeuron(signal: MutableList<BigDecimal>, neuron: NeuronModel): BigDecimal {
        val weights = neuron.getWeights()
        val length = minOf(signal.size, weights.size)

        var result = BigDecimal.valueOf(0)
        for (i in 0 until length)
            result += signal[i] * weights[i]

        return activateFunction(result)
    }

    private fun activateFunction(x: BigDecimal): BigDecimal {
        return x / (x.abs() + BigDecimal.valueOf(1))
    }
}