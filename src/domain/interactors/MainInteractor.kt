package domain.interactors

import domain.interfaces.MainViewInterface
import domain.listeners.MainPresenterListener
import domain.listeners.MainRepositoryListener
import java.awt.Color
import java.awt.image.BufferedImage

class MainInteractor(private val repository: MainViewInterface.Repository) : MainRepositoryListener{
    private val networkInteractor = NetworkInteractor()
    private var mainPresenterListener: MainPresenterListener? = null

    companion object {
        private var instance: MainInteractor? = null

        fun getInstance(repository: MainViewInterface.Repository): MainInteractor {
            if (instance == null)
                instance = MainInteractor(repository)
            return instance!!
        }
    }

    init {
        networkInteractor.addLayer(100,784)
        networkInteractor.addLayer(50, 100)
        networkInteractor.addLayer(26, 50)
        repository.setMainRepositoryListener(this)
    }

    fun startLearning(rowCount: Int, columnCount: Int) {
        val trainingSet = repository.getTrainingSet(rowCount, columnCount)

        for ((currentNumberOfSet, currentSet) in trainingSet.withIndex()) {
            for ((index, image) in currentSet.withIndex())
                sendSignal(makeSignal(image), index, currentNumberOfSet)
        }
    }

    fun startTesting(rowCount: Int, columnCount: Int) {
        val testSets = repository.getTestingSet(rowCount, columnCount)

        for ((currentNumberOfSet, currentSet) in testSets.withIndex()) {
            for ((index, image) in currentSet.withIndex())
                sendSignal(makeSignal(image), index, currentNumberOfSet)
        }
    }

    fun setMainPresenterListener(listener: MainPresenterListener) {
        mainPresenterListener = listener
    }

    fun loadTrainingSet(rowCount: Int, columnCount: Int) {
        repository.loadTrainingSet(rowCount, columnCount)
    }

    fun loadTestingSet(rowCount: Int, columnCount: Int) {
        repository.loadTestingSet(rowCount, columnCount)
    }

    /** MainRepositoryListener **/
    override fun updateProgressBar(newValue: Int) {
        mainPresenterListener?.updateProgressBar(newValue)
    }

    override fun onLoadedSet(set: List<BufferedImage>) {
        mainPresenterListener?.onLoadedSet(set)
    }

    override fun cleanTable(rowCount: Int, columnCount: Int) {
        mainPresenterListener?.cleanTable(rowCount, columnCount)
    }

    private fun sendSignal(signal: MutableList<Double>, numberOfImage: Int, numberOfSet: Int) {
        val answer = networkInteractor.sendSignal(signal)

        println("Number of set: $numberOfSet, Number of image: $numberOfImage")
        println("Answer: ")
        for (item in answer) {
            print("$item ")
        }
        print("\n")

        var max = 0.0
        var maxI = 0
        for ((index, item) in answer.withIndex()) {
            if (item > max) {
                max = item
                maxI = index
            }
        }

        mainPresenterListener?.onAnswerReturned(maxI, numberOfImage, numberOfSet)
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