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
    private var testSetSize = 0
    private var trainingSetSize = 0
    private var classesCount = 0

    override fun setView(view: MainViewInterface.View) {
        this.view = view
    }

    override fun init(testSetSize: Int, trainingSetSize: Int, classesCount: Int) {
        this.testSetSize = testSetSize
        this.trainingSetSize = trainingSetSize
        this.classesCount = classesCount
        view.createGUI()
        view.setListeners()
        interactor.setMainPresenterListener(this)
    }

    override fun onLoadTestSetBtnClicked() {
        log.info("onLoadTestSetBtnClicked!")

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                resetUIComponents(testSetSize, classesCount)
                view.cleanTable(testSetSize, classesCount)
                interactor.loadTestingSet(testSetSize, classesCount)
                return null
            }

            override fun done() {
                view.enableButtons(true)
            }
        }.execute()
    }

    override fun onLoadTrainingSetBtnClicked() {
        log.info("onLoadTrainingSetBtnClicked!")

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                resetUIComponents(trainingSetSize, classesCount)
                view.cleanTable(trainingSetSize, classesCount)
                interactor.loadTrainingSet(trainingSetSize, classesCount)
                return null
            }

            override fun done() {
                view.enableButtons(true)
            }
        }.execute()
    }

    override fun onStartBtnClicked(eraCount: Int, epsilon: Double, alpha: Double) {
        log.info("onStartBtnClicked!")
        interactor.setEraCount(eraCount)
        interactor.setEpsilon(epsilon)
        interactor.setAlpha(alpha)

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                resetUIComponents(testSetSize, classesCount)
                interactor.startTesting(testSetSize, classesCount)
                return null
            }

            override fun done() {
                view.enableButtons(true)
            }
        }.execute()
    }

    override fun onLearningBtnClicked(eraCount: Int, epsilon: Double, alpha: Double) {
        log.info("onLearningBtnClicked!")
        interactor.setEraCount(eraCount)
        interactor.setEpsilon(epsilon)
        interactor.setAlpha(alpha)

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                resetUIComponents(trainingSetSize, classesCount)
                interactor.startLearning(trainingSetSize, classesCount)
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

    override fun cleanTable(rowCount: Int, columnCount: Int) {
        view.cleanTable(rowCount, columnCount)
    }

    override fun onNewLayerAdded(addedLayerNumber: Int) {
        view.setStatusText("Добавлен новый слой, номер: $addedLayerNumber")
    }

    override fun currentLayer(currentLayerNumber: Int) {
        //view.setStatusText("Текущий слой: $currentLayerNumber")
    }

    override fun networkAnswer(answer: Int, currentNumberOfImage: Int, currentNumberOfSet: Int) {
        view.updateTableItem(currentNumberOfSet, currentNumberOfImage, getStringAnswer(answer), answer == currentNumberOfImage)
    }

    override fun onEraChanged(currentEraNumber: Int) {
        view.eraseTableColor()
        view.setStatusText("Текущая эпоха: $currentEraNumber")
    }

    override fun loadSetCount(count: Int, setSize: Int) {
        view.setStatusText("Загрузка сета $count из $setSize")
    }

    override fun onSetChanged(currentSetNumber: Int, setSize: Int) {
        view.setStatusText("Текущий сет ${currentSetNumber + 1} из $setSize")
        view.updateBar(currentSetNumber + 1)
    }

    override fun onProcessStart(setSize: Int) {
        view.setBarSize(0, setSize)
    }

    private fun getStringAnswer(answer: Int): String {
        return stringAnswer[answer]
    }

    private fun resetUIComponents(rowCount: Int, columnCount: Int) {
        view.setBarSize(0, rowCount * columnCount)
        view.enableButtons(false)
        view.eraseTableColor()
    }
}