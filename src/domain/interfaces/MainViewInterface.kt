package domain.interfaces

interface MainViewInterface {
    interface View {
        fun createGUI()
        fun setListeners()
        fun loadTestSet()
        fun loadTrainingSet()
    }

    interface Presenter {
        fun setView (view: View)
        fun init()
        fun onStartBtnClicked()
        fun onLearningBtnClicked()
        fun onExitBtnClicked()
    }
}