package domain.interactors

import domain.interfaces.MainViewInterface
import domain.listeners.MainPresenterListener
import domain.listeners.MainRepositoryListener
import domain.listeners.NetworkProcessListener
import java.awt.Color
import java.awt.image.BufferedImage

class MainInteractor(private val repository: MainViewInterface.Repository) : MainRepositoryListener, NetworkProcessListener{
    private val networkInteractor = NetworkInteractor()
    private var mainPresenterListener: MainPresenterListener? = null
    private var currentNumberOfSet = 0
    private var currentNumberOfImage = 0

    init {
        networkInteractor.setNetworkProcessListener(this)
        repository.setMainRepositoryListener(this)
        //networkInteractor.addLayer(4, 625)
        //networkInteractor.addLayer(10, 4)
        networkInteractor.addLayer(800,625)
        networkInteractor.addLayer(400, 800)
        networkInteractor.addLayer(26, 400)
    }

    fun startLearning(rowCount: Int, columnCount: Int) {
        networkInteractor.learn(repository.getTrainingSet(rowCount, columnCount))
    }

    fun startTesting(rowCount: Int, columnCount: Int) {
        val testSets = repository.getTestingSet(rowCount, columnCount)

        for ((setIndex, currentSet) in testSets.withIndex()) {
            currentNumberOfSet = setIndex
            mainPresenterListener?.onSetChanged(setIndex, testSets.size)
            for ((imageIndex, image) in currentSet.withIndex()) {
                currentNumberOfImage = imageIndex
                networkInteractor.sendSignal(image)
            }
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

    fun setEpsilon(epsilon: Double) {
        networkInteractor.setEpsilon(epsilon)
    }

    fun setAlpha(alpha: Double) {
        networkInteractor.setAlpha(alpha)
    }

    fun setEraCount(eraCount: Int) {
        networkInteractor.setEraCount(eraCount)
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

    override fun loadedSetCount(count: Int, setSize: Int) {
        mainPresenterListener?.loadSetCount(count, setSize)
    }

    /** NetworkProcessListener **/
    override fun onNewLayerAdded(addedLayerNumber: Int) {
        mainPresenterListener?.onNewLayerAdded(addedLayerNumber)
    }

    override fun currentLayer(currentLayerNumber: Int) {
        mainPresenterListener?.currentLayer(currentLayerNumber)
    }

    override fun networkAnswer(answer: Int, currentNumberOfImage: Int, currentNumberOfSet: Int) {
        if (currentNumberOfImage == -1 || currentNumberOfSet == -1)
            mainPresenterListener?.networkAnswer(answer, this.currentNumberOfImage, this.currentNumberOfSet)
        else
            mainPresenterListener?.networkAnswer(answer, currentNumberOfImage, currentNumberOfSet)
    }

    override fun onEraChanged(currentEraNumber: Int) {
        mainPresenterListener?.onEraChanged(currentEraNumber)
    }

    override fun onSetChanged(currentSetNumber: Int, setSize: Int) {
        mainPresenterListener?.onSetChanged(currentSetNumber, setSize)
    }
}