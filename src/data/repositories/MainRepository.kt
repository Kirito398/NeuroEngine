package data.repositories

import domain.interfaces.MainViewInterface
import domain.listeners.MainRepositoryListener
import domain.models.NeuronModel
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class MainRepository() : MainViewInterface.Repository {
    private var mainRepositoryListener: MainRepositoryListener? = null
    private var trainingSet: MutableList<MutableList<BufferedImage>>? = null
    private var testingSet: MutableList<MutableList<BufferedImage>>? = null

    override fun loadTrainingSet(rowCount: Int, columnCount: Int) {
        trainingSet?.clear()
        trainingSet = loadSet("TrainSet", rowCount, columnCount)
    }

    override fun getTrainingSet(rowCount: Int, columnCount: Int): MutableList<MutableList<BufferedImage>> {
        if (trainingSet == null) {
            mainRepositoryListener?.cleanTable(rowCount, columnCount)
            loadTrainingSet(rowCount, columnCount)
        }
        return trainingSet!!
    }

    override fun loadTestingSet(rowCount: Int, columnCount: Int) {
        testingSet?.clear()
        testingSet = loadSet("Test", rowCount, columnCount)
    }

    override fun getTestingSet(rowCount: Int, columnCount: Int): MutableList<MutableList<BufferedImage>> {
        if (testingSet == null) {
            mainRepositoryListener?.cleanTable(rowCount, columnCount)
            loadTestingSet(rowCount, columnCount)
        }
        return testingSet!!
    }

    override fun setMainRepositoryListener(listener: MainRepositoryListener) {
        mainRepositoryListener = listener
    }

    override fun save(layers: List<List<NeuronModel>>, epsilon: Double, alpha: Double) {
        val outputName = "data.txt"
        val writer = File(outputName).bufferedWriter()

        writer.write("$epsilon $alpha")

        for(layer in layers) {
            for (neuron in layer) {
                writer.newLine()
                for (weight in neuron.getWeights()) {
                    writer.write(weight.toString())
                    writer.write(" ")
                }
            }
            writer.newLine()
        }
    }

    override fun load() {
        val inputName = "data.txt"

        val lines = File(inputName).readLines()
        val epsilon = lines.first().split(" ")[0].toDouble()
        val alpha = lines.first().split(" ")[1].toDouble()

        val layers = mutableListOf<MutableList<NeuronModel>>()
        var layer = mutableListOf<NeuronModel>()
        for (line in lines) {
            if (line == lines.first())
                continue

            if (line.isEmpty()) {
                layers.add(layer)
                layer = mutableListOf<NeuronModel>()
                continue
            }

            val weights = mutableListOf<Double>()
            for (weight in line.split(" "))
                if(weight.isNotEmpty()) weights.add(weight.toDouble())

            val neuron = NeuronModel(weights.size)
            neuron.setWeight(weights)

            layer.add(neuron)
        }
        layers.add(layer)

        mainRepositoryListener?.onLoadedNeuronWeb(epsilon, alpha, layers)
    }

    private fun loadSet(path: String, rowCount: Int, columnCount: Int): MutableList<MutableList<BufferedImage>> {
        val loadedSets = mutableListOf<MutableList<BufferedImage>>()
        for (currentNumberOfSet in 1 .. rowCount) {
            val set = loadSetByNumber(path, columnCount, currentNumberOfSet)
            mainRepositoryListener?.updateProgressBar(columnCount * currentNumberOfSet)
            mainRepositoryListener?.onLoadedSet(set)
            mainRepositoryListener?.loadedSetCount(currentNumberOfSet, rowCount)
            loadedSets.add(set)
        }

        return loadedSets
    }

    private fun loadSetByNumber(path: String, size: Int, numberOfSet: Int): MutableList<BufferedImage> {
        val loadedSet = mutableListOf<BufferedImage>()

        for (i in 0 until size) {
            val image = ImageIO.read(File("$path//$i//$i ($numberOfSet).jpg"))
            loadedSet.add(resize(image, 25, 25))
        }

        return loadedSet
    }

    private fun resize(img: BufferedImage, newWeight: Int, newHeight: Int): BufferedImage {
        val temp = img.getScaledInstance(newWeight, newHeight, Image.SCALE_SMOOTH)
        val dimg = BufferedImage(newWeight, newHeight, BufferedImage.TYPE_INT_ARGB)

        val g2d = dimg.createGraphics()
        g2d.drawImage(temp, 0, 0, null)
        g2d.dispose()

        return dimg
    }
}