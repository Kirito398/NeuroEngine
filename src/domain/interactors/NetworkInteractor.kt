package domain.interactors

import domain.interfaces.MainViewInterface
import domain.listeners.NetworkProcessListener
import domain.models.NeuronModel
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
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

    init {
        /*val neuron1 = NeuronModel(2)
        val neuron2 = NeuronModel(2)
        val neuron3 = NeuronModel(2)

        neuron1.setWeight(mutableListOf(0.45, -0.12))
        neuron2.setWeight(mutableListOf(0.78, 0.13))
        neuron3.setWeight(mutableListOf(1.5, -2.3))

        layers.add(mutableListOf(neuron1, neuron2))
        layers.add(mutableListOf(neuron3))*/
    }

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

    fun addLayer(layer: MutableList<NeuronModel>) {
        layers.add(layer)
    }

    fun sendSignal(signal: MutableList<Double>): Int {
        inputSignal = signal
        currentSignal = signal

        for ((index, layer) in layers.withIndex()) {
            currentSignal = calculateSignalFromLayer(currentSignal, layer)
            listener?.currentLayer(index)
        }

        getNetworkAnswer()
        return networkAnswer
    }

    fun startLearn(trainingSet: List<List<BufferedImage>>) {
        listener?.onProcessStart(trainingSet.size)
        for (era in 1 .. eraCount) {
            listener?.onEraChanged(era)
            for ((currentNumberOfSet, currentSet) in trainingSet.withIndex()) {
                listener?.onSetChanged(currentNumberOfSet, trainingSet.size)
                for ((currentNumberOfImage, image) in currentSet.withIndex()) {
                    val answer = sendSignal(makeSignal(image))
                    listener?.networkAnswer(answer, currentNumberOfImage, currentNumberOfSet)
                    learn(if (answer == currentNumberOfImage) 1.0 else 0.0)
                }
            }
        }

        //sendSignal(mutableListOf(1.0, 0.0))
        //learn(1.0)
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

    fun save(repository: MainViewInterface.Repository) {
        repository.save(layers, E, A)
        log.info("Saved!")
    }

    fun clearLayers() {
        for (layer in layers) {
            layer.clear()
        }
        layers.clear()
    }

    private fun learn(idealAnswer: Double) {
        val deltaOut = (idealAnswer - currentSignal[networkAnswer]) * (1.0 - currentSignal[networkAnswer]) * currentSignal[networkAnswer]

        val studentNeuron = layers.last()[networkAnswer]
        for ((index, neuron) in layers[layers.size - 2].withIndex()) {
            val grad = neuron.value * deltaOut
            val deltaWeight = E * grad + A * studentNeuron.getDeltaWeights()[index]
            studentNeuron.getDeltaWeights()[index] = deltaWeight
            neuron.delta = ((1 - neuron.value) * neuron.value) * (studentNeuron.getWeights()[index] * deltaOut)
            studentNeuron.updateWeight(deltaWeight, index)
        }

        for (layer in layers.size - 2 until 0) {
            for (neuron in layers[layer - 1]) {
                neuron.delta = 0.0
            }

            for (studentNeuron in layers[layer]) {
                for ((index, neuron) in layers[layer - 1].withIndex()) {
                    val grad = neuron.value * studentNeuron.delta
                    val deltaWeight = E * grad + A * studentNeuron.getDeltaWeights()[index]
                    studentNeuron.getDeltaWeights()[index] = deltaWeight
                    neuron.delta += ((1 - neuron.value) * neuron.value) * (studentNeuron.getWeights()[index] * studentNeuron.delta)
                    studentNeuron.updateWeight(deltaWeight, index)
                }
            }

            val size = layers[layer].size
            for (neuron in layers[layer - 1]) {
                neuron.delta /= size
            }
        }

        for (studentNeuron in layers.first()) {
            for ((index, signal) in inputSignal.withIndex()) {
                val grad = signal * studentNeuron.delta
                val deltaWeight = E * grad + A * studentNeuron.getDeltaWeights()[index]
                studentNeuron.getDeltaWeights()[index] = deltaWeight
                studentNeuron.updateWeight(deltaWeight, index)
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

        neuron.value = activateFunction(result / length)

        return neuron.value
    }

    private fun activateFunction(x: Double): Double  = 1.0 / (1.0 + exp(-x))

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

    companion object {
        fun makeSignal(image: BufferedImage): MutableList<Double> {
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
}