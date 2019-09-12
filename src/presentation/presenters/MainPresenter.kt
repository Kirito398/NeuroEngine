package presentation.presenters

import domain.interactors.MainInteractor
import domain.interfaces.MainViewInterface
import domain.listeners.MainPresenterListener
import java.awt.image.BufferedImage
import java.util.logging.Logger
import javax.swing.SwingWorker

class MainPresenter(val interactor: MainInteractor) : MainViewInterface.Presenter, MainPresenterListener{
    private val log = Logger.getLogger("MainPresenter")
    private val stringAnswer = arrayListOf("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","W","Z")
    private lateinit var view: MainViewInterface.View

    override fun setView(view: MainViewInterface.View) {
        this.view = view
    }

    override fun init() {
        view.createGUI()
        view.setListeners()
        interactor.setMainPresenterListener(this)
    }

    override fun onLoadTestSetBtnClicked(rowCount: Int, columnCount: Int) {
        log.info("onLoadTestSetBtnClicked!")
        resetUIComponents(rowCount, columnCount)
        view.cleanTable(rowCount, columnCount)

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                interactor.loadTestingSet(rowCount, columnCount)
                return null
            }

            override fun done() {
                view.enableButtons(true)
            }
        }.execute()
    }

    override fun onLoadTrainingSetBtnClicked(rowCount: Int, columnCount: Int) {
        log.info("onLoadTrainingSetBtnClicked!")
        resetUIComponents(rowCount, columnCount)
        view.cleanTable(rowCount, columnCount)

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                interactor.loadTrainingSet(rowCount, columnCount)
                return null
            }

            override fun done() {
                view.enableButtons(true)
            }
        }.execute()
    }

    override fun onStartBtnClicked(rowCount: Int, columnCount: Int) {
        log.info("onStartBtnClicked!")
        resetUIComponents(rowCount, columnCount)

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                interactor.startTesting(rowCount, columnCount)
                return null
            }

            override fun done() {
                view.enableButtons(true)
            }
        }.execute()
    }

    override fun onLearningBtnClicked(rowCount: Int, columnCount: Int) {
        log.info("onLearningBtnClicked!")
        resetUIComponents(rowCount, columnCount)

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                interactor.startLearning(rowCount, columnCount)
                return null
            }

            override fun done() {
                view.enableButtons(true)
            }
        }.execute()
    }

    override fun onExitBtnClicked() {
        log.info("onExitBtnClicked!")
    }

    /** MainPresenterListener **/
    override fun updateProgressBar(newValue: Int) {
        view.updateBar(newValue)
    }

    override fun onLoadedSet(set: List<BufferedImage>) {
        view.onLoadedSet(set)
    }

    override fun onAnswerReturned(answer: Int, numberOfImage: Int, numberOfSet: Int) {
        view.updateTableItem(numberOfSet, numberOfImage, getStringAnswer(answer), answer == numberOfImage)
    }

    override fun cleanTable(rowCount: Int, columnCount: Int) {
        view.cleanTable(rowCount, columnCount)
    }

    private fun getStringAnswer(answer: Int): String {
        return stringAnswer[answer]
    }

    private fun resetUIComponents(rowCount: Int, columnCount: Int) {
        view.resetProgressBar(rowCount * columnCount, 0)
        view.enableButtons(false)
        view.eraseTableColor()
    }
}