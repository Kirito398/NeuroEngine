package domain.interactors

import domain.interfaces.MainViewInterface
import domain.listeners.MainPresenterListener
import domain.listeners.MainRepositoryListener
import domain.listeners.NetworkProcessListener
import domain.models.NeuronModel
import java.awt.image.BufferedImage

class MainInteractor(private val repository: MainViewInterface.Repository) : MainRepositoryListener, NetworkProcessListener{
    private val networkInteractor = NetworkInteractor()
    private var mainPresenterListener: MainPresenterListener? = null
    private var currentNumberOfSet = 0
    private var currentNumberOfImage = 0

    init {
        networkInteractor.setNetworkProcessListener(this)
        repository.setMainRepositoryListener(this)
        networkInteractor.addLayer(500,625)
        networkInteractor.addLayer(250, 500)
        networkInteractor.addLayer(26, 500)
    }

    fun startLearning(rowCount: Int, columnCount: Int) {
        networkInteractor.startLearn(repository.getTrainingSet(rowCount, columnCount))
        //networkInteractor.startLearn(listOf<List<BufferedImage>>())
    }

    fun startTesting(rowCount: Int, columnCount: Int) {
        val testSets = repository.getTestingSet(rowCount, columnCount)
        mainPresenterListener?.onProcessStart(testSets.size)

        for ((setIndex, currentSet) in testSets.withIndex()) {
            currentNumberOfSet = setIndex
            mainPresenterListener?.onSetChanged(setIndex, testSets.size)
            for ((imageIndex, image) in currentSet.withIndex()) {
                currentNumberOfImage = imageIndex
                networkInteractor.sendSignal(NetworkInteractor.makeSignal(image))
            }
        }

        //networkInteractor.sendSignal(mutableListOf(1.0, 0.0))
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

    fun saveNeuronWeb() {
        networkInteractor.save(repository)
    }

    fun loadNeuronWeb() {
        repository.load()
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

    override fun onLoadedNeuronWeb(epsilon: Double, alpha: Double, layers: MutableList<MutableList<NeuronModel>>) {
        networkInteractor.setEpsilon(epsilon)
        networkInteractor.setAlpha(alpha)

        networkInteractor.clearLayers()

        for (layer in layers) {
            networkInteractor.addLayer(layer)
        }

        mainPresenterListener?.onLoadedNeuronWeb()
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

    override fun onProcessStart(setSize: Int) {
        mainPresenterListener?.onProcessStart(setSize)
    }
}