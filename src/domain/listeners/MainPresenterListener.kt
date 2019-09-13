package domain.listeners

import java.awt.image.BufferedImage

interface MainPresenterListener : NetworkProcessListener {
    fun updateProgressBar(newValue: Int)
    fun onLoadedSet(set: List<BufferedImage>)
    fun loadSetCount(count: Int, setSize: Int)
    fun cleanTable(rowCount: Int, columnCount: Int)
}