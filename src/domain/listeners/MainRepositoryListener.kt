package domain.listeners

import java.awt.image.BufferedImage

interface MainRepositoryListener {
    fun updateProgressBar(newValue: Int)
    fun onLoadedSet(set: List<BufferedImage>)
    fun cleanTable(rowCount: Int, columnCount: Int)
    fun loadedSetCount(count: Int, setSize: Int)
}