package domain.listeners

import domain.models.NeuronModel
import java.awt.image.BufferedImage

interface MainRepositoryListener {
    fun updateProgressBar(newValue: Int)
    fun onLoadedSet(set: List<BufferedImage>)
    fun cleanTable(rowCount: Int, columnCount: Int)
    fun loadedSetCount(count: Int, setSize: Int)
    fun onLoadedNeuronWeb(epsilon: Double, alpha: Double, layers: MutableList<MutableList<NeuronModel>>)
}