package domain.interfaces

import domain.listeners.MainRepositoryListener
import java.awt.image.BufferedImage

interface MainViewInterface {
    interface View {
        fun createGUI()
        fun setListeners()
        fun cleanTable(newRowsCount: Int, newColumnCount: Int)
        fun resetProgressBar(max: Int, min: Int)
        fun enableButtons(enable: Boolean)
        fun updateBar(newValue: Int)
        fun onLoadedSet(set: List<BufferedImage>)
        fun updateTableItem(numberOfSet: Int, numberOfImage: Int, answer: String, isCorrect: Boolean)
        fun eraseTableColor()
    }

    interface Presenter {
        fun setView (view: View)
        fun init()
        fun onStartBtnClicked(rowCount: Int, columnCount: Int)
        fun onLearningBtnClicked(rowCount: Int, columnCount: Int)
        fun onLoadTestSetBtnClicked(rowCount: Int, columnCount: Int)
        fun onLoadTrainingSetBtnClicked(rowCount: Int, columnCount: Int)
        fun onExitBtnClicked()
    }

    interface Repository {
        fun setMainRepositoryListener(listener: MainRepositoryListener)
        fun loadTrainingSet(rowCount: Int, columnCount: Int)
        fun getTrainingSet(rowCount: Int, columnCount: Int): List<List<BufferedImage>>
        fun loadTestingSet(rowCount: Int, columnCount: Int)
        fun getTestingSet(rowCount: Int, columnCount: Int): List<List<BufferedImage>>
    }
}