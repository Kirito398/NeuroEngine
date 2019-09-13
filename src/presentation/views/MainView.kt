package presentation.views

import domain.interactors.MainInteractor
import domain.interfaces.MainViewInterface
import presentation.presenters.MainPresenter
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.*

class MainView(interactor: MainInteractor) : MainViewInterface.View {
    private val presenter: MainViewInterface.Presenter = MainPresenter(interactor)
    private val tableItems = mutableListOf<MutableList<JLabel>>()
    private lateinit var tablePanel: JPanel
    private lateinit var tableScroll: JScrollPane
    private lateinit var controlPanel: JPanel
    private lateinit var progressPanel: JPanel
    private lateinit var progressBar: JProgressBar
    private lateinit var mainPanel: JPanel
    private lateinit var mainWindow: JFrame
    private lateinit var startBtn: JButton
    private lateinit var learningBtn: JButton
    private lateinit var loadTestSetBtn: JButton
    private lateinit var loadTrainingSetBtn: JButton
    private lateinit var exitBtn: JButton

    init {
        presenter.setView(this)
        presenter.init()
    }

    override fun createGUI() {
        tablePanel = JPanel()
        tableScroll = JScrollPane(tablePanel)
        tableScroll.preferredSize = tablePanel.preferredSize

        startBtn = JButton("Старт")
        learningBtn = JButton("Обучение")
        loadTestSetBtn = JButton("Загрузить тестовый набор")
        loadTrainingSetBtn = JButton("Загрузить тренировочный набор")
        exitBtn = JButton("Выход")

        controlPanel = JPanel()
        controlPanel.layout = GridLayout(25, 1, 5, 5)
        controlPanel.add(startBtn)
        controlPanel.add(learningBtn)
        controlPanel.add(loadTestSetBtn)
        controlPanel.add(loadTrainingSetBtn)
        controlPanel.add(exitBtn)

        progressBar = JProgressBar()
        progressPanel = JPanel()
        progressPanel.layout = GridLayout(1,1,5,5)
        progressPanel.add(progressBar)

        mainPanel = JPanel()
        mainPanel.layout = BorderLayout()
        mainPanel.border = BorderFactory.createEmptyBorder(5,5,5,5)
        mainPanel.add(tableScroll, BorderLayout.CENTER)
        mainPanel.add(controlPanel, BorderLayout.EAST)
        mainPanel.add(progressPanel, BorderLayout.SOUTH)

        mainWindow = JFrame("Neuro Engine")
        mainWindow.minimumSize = Dimension(640, 480)
        mainWindow.setSize(1680, 1000)
        mainWindow.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        mainWindow.add(mainPanel)
        mainWindow.isVisible = true
    }

    override fun setListeners() {
        startBtn.addActionListener {presenter.onStartBtnClicked(140, 26)}
        learningBtn.addActionListener {presenter.onLearningBtnClicked(2100, 26)}
        loadTestSetBtn.addActionListener {presenter.onLoadTestSetBtnClicked(140, 26)}
        loadTrainingSetBtn.addActionListener {presenter.onLoadTrainingSetBtnClicked(2100, 26)}
        exitBtn.addActionListener {presenter.onExitBtnClicked()}
    }

    override fun cleanTable(newRowsCount: Int, newColumnCount: Int) {
        tablePanel.removeAll()
        tablePanel.layout = GridLayout(newRowsCount, newColumnCount, 10,10)
        tableItems.clear()
    }

    override fun resetProgressBar(max: Int, min: Int) {
        progressBar.minimum = min
        progressBar.maximum = max
    }

    override fun enableButtons(enable: Boolean) {
        startBtn.isEnabled = enable
        learningBtn.isEnabled = enable
        loadTrainingSetBtn.isEnabled = enable
        loadTestSetBtn.isEnabled = enable
        exitBtn.isEnabled = enable
    }

    override fun updateBar(newValue: Int) {
        progressBar.value = newValue
    }

    override fun onLoadedSet(set: List<BufferedImage>) {
        val items = mutableListOf<JLabel>()
        for((index, image) in set.withIndex()) {
            val icon = ImageIcon(image)
            val label = JLabel()
            label.icon = icon
            label.text = "$index"
            label.isOpaque = true
            tablePanel.add(label)
            items.add(label)
        }
        tableItems.add(items)
        tablePanel.revalidate()
    }

    override fun updateTableItem(numberOfSet: Int, numberOfImage: Int, answer: String, isCorrect: Boolean) {
        val item = tableItems[numberOfSet][numberOfImage]
        item.text = answer
        item.background = if (isCorrect) Color.GREEN else Color.RED

        tableScroll.verticalScrollBar.value = tableScroll.verticalScrollBar.maximum / 140 * numberOfSet
    }

    override fun eraseTableColor() {
        for (row in tableItems) {
            for (item in row) {
                item.background = Color.WHITE
            }
        }
    }
}