package domain.listeners

import java.awt.image.BufferedImage

interface MainPresenterListener {
    fun updateProgressBar(newValue: Int)
    fun onLoadedSet(set: List<BufferedImage>)
    fun onAnswerReturned(answer: Int, numberOfImage: Int, numberOfSet: Int)
    fun cleanTable(rowCount: Int, columnCount: Int)
}