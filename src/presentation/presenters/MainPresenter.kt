package presentation.presenters

import domain.interfaces.MainViewInterface
import java.util.logging.Logger

class MainPresenter : MainViewInterface.Presenter {
    private val log = Logger.getLogger("MainPresenter")
    private lateinit var view: MainViewInterface.View

    override fun setView(view: MainViewInterface.View) {
        this.view = view
    }

    override fun init() {
        view.createGUI()
        view.setListeners()
    }

    override fun onStartBtnClicked() {
        log.info("onStartBtnClicked!")
        view.loadTestSet()
    }

    override fun onLearningBtnClicked() {
        log.info("onLearningBtnClicked!")
        view.loadTrainingSet()
    }

    override fun onExitBtnClicked() {
        log.info("onExitBtnClicked!")
    }

}