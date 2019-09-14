package domain.interfaces

import domain.listeners.MainRepositoryListener
import java.awt.image.BufferedImage

interface MainViewInterface {
    interface View {
        fun createGUI()
        fun setListeners()
        fun cleanTable(newRowsCount: Int, newColumnCount: Int)
        fun enableButtons(enable: Boolean)
        fun updateBar(newValue: Int)
        fun onLoadedSet(set: List<BufferedImage>)
        fun updateTableItem(numberOfSet: Int, numberOfImage: Int, answer: String, isCorrect: Boolean)
        fun eraseTableColor()
        fun setStatusText(text: String)
        fun setBarSize(min: Int, max: Int)
    }

    interface Presenter {
        fun setView (view: View)
        fun init(testSetSize: Int, trainingSetSize: Int, classesCount: Int)
        fun onStartBtnClicked(eraCount: Int, epsilon: Double, alpha: Double)
        fun onLearningBtnClicked(eraCount: Int, epsilon: Double, alpha: Double)
        fun onLoadTestSetBtnClicked()
        fun onLoadTrainingSetBtnClicked()
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