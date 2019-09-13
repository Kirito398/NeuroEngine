package domain.interactors

import domain.listeners.NetworkProcessListener
import domain.models.NeuronModel
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.logging.Logger
import kotlin.math.exp

class NetworkInteractor {
    private val log = Logger.getLogger("NetworkInteractor")
    private val layers = mutableListOf<MutableList<NeuronModel>>()
    private var E = 0.2
    private var A = 0.1
    private var eraCount = 1

    private var listener: NetworkProcessListener? = null

    private var inputSignal = mutableListOf<Double>()
    private var currentSignal = mutableListOf<Double>()
    private var networkAnswer = 0

    fun setNetworkProcessListener(listener: NetworkProcessListener) {
        this.listener = listener
    }

    fun addLayer(neuronCount: Int, inputSignalCount: Int) {
        val newLayer = mutableListOf<NeuronModel>()

        for (i in 1 .. neuronCount) {
            newLayer.add(NeuronModel(inputSignalCount))
            listener?.onNewLayerAdded(i)
        }

        layers.add(newLayer)
    }

    fun sendSignal(signal: BufferedImage): Int {
        inputSignal = makeSignal(signal)
        currentSignal = inputSignal

        for ((index, layer) in layers.withIndex()) {
            currentSignal = calculateSignalFromLayer(currentSignal, layer)
            listener?.currentLayer(index)
        }

        getNetworkAnswer()
        return networkAnswer
    }

    fun learn(trainingSet: List<List<BufferedImage>>) {
        for (era in 1 .. eraCount) {
            listener?.onEraChanged(era)
            for ((currentNumberOfSet, currentSet) in trainingSet.withIndex()) {
                listener?.onSetChanged(currentNumberOfSet, trainingSet.size)
                for ((currentNumberOfImage, image) in currentSet.withIndex()) {
                    val answer = sendSignal(image)
                    listener?.networkAnswer(answer, currentNumberOfImage, currentNumberOfSet)
                    learn(if (answer == currentNumberOfImage) 1.0 else 0.0)
                }
            }
        }
    }

    fun setEraCount(count: Int) {
        log.info("Era count changed: $count")
        eraCount = count
    }

    fun setEpsilon(epsilon: Double) {
        log.info("Epsilon changed: $epsilon")
        E = epsilon
    }

    fun setAlpha(alpha: Double) {
        log.info("Alpha changed: $alpha")
        A = alpha
    }

    private fun learn(idealAnswer: Double) {
        val deltaOut = (idealAnswer - currentSignal[networkAnswer]) * (1.0 - currentSignal[networkAnswer]) * currentSignal[networkAnswer]

        for (neuron in layers.last()) {
            val grad = neuron.value * deltaOut
            val deltaWeight = E * grad + A * neuron.delta
            neuron.delta = ((1 - neuron.value) * neuron.value) * (neuron.getWeights()[networkAnswer] * deltaOut)
            neuron.getWeights()[networkAnswer] += deltaWeight
        }

        for (layer in layers.size - 2 until 0) {
            for (neuron in layers[layer]) {
                val proizv = (1 - neuron.value) * neuron.value
                var summ = 0.0
                var grad: Double
                for ((index, weight) in neuron.getWeights().withIndex()) {
                    val nextNeuron = layers[layer + 1][index]
                    summ += (weight * nextNeuron.delta)
                    grad = neuron.value * nextNeuron.delta
                    val deltaWeight = E * grad + A * neuron.delta
                    neuron.getWeights()[index] += deltaWeight
                }
                neuron.delta = proizv * summ
            }
        }

        for ((index, signal) in inputSignal.withIndex()) {
            for (neuron in layers.first()) {
                val grad = signal * neuron.delta
                val deltaWeight = E * grad
                neuron.getWeights()[index] += deltaWeight
            }
        }
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
        listener?.networkAnswer(networkAnswer)
    }

    private fun makeSignal(image: BufferedImage): MutableList<Double> {
        val signal = mutableListOf<Double>()

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val color = Color(image.getRGB(x, y))
                val pixel = (color.red + color.green + color.blue) / 3.0
                signal.add(if (pixel != 0.0) 1.0 / pixel else pixel)
            }
        }

        return signal
    }
}