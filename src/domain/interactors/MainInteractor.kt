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
        networkInteractor.addLayer(800,784)
        networkInteractor.addLayer(400, 800)
        networkInteractor.addLayer(26, 400)
        repository.setMainRepositoryListener(this)
    }

    fun startLearning(rowCount: Int, columnCount: Int) {
        val trainingSet = repository.getTrainingSet(rowCount, columnCount)

        for ((currentNumberOfSet, currentSet) in trainingSet.withIndex()) {
            for ((index, image) in currentSet.withIndex()) {
                val answer = sendSignal(makeSignal(image), index, currentNumberOfSet)
                networkInteractor.learn(if (answer == index) 1.0 else 0.0)
            }
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

    private fun sendSignal(signal: MutableList<Double>, numberOfImage: Int, numberOfSet: Int): Int {
        val answer = networkInteractor.sendSignal(signal)

        /*println("Number of set: $numberOfSet, Number of image: $numberOfImage")
        println("Answer: ")
        for (item in answer) {
            print("$item ")
        }
        print("\n")*/

        mainPresenterListener?.onAnswerReturned(answer, numberOfImage, numberOfSet)
        return answer
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