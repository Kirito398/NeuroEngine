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
    private var epsilonCount = 0.7
    private var alphaCount = 0.3
    private var eraCount = 1
    private var testSetSize = 140
    private var trainingSetSize = 2100
    private var classesCount = 26
    private var currentSetSize = 140

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
    private lateinit var saveNeuronWebBtn: JButton
    private lateinit var loadNeuronWebBtn: JButton
    private lateinit var exitBtn: JButton
    private lateinit var eraCountLabel: JLabel
    private lateinit var epsilonCountLabel: JLabel
    private lateinit var alphaCountLabel: JLabel
    private lateinit var statusTextLabel: JLabel
    private lateinit var errorTextLabel: JLabel
    private lateinit var prevErrorTextLabel: JLabel
    private lateinit var currentEraTextLabel: JLabel
    private lateinit var epsilonCountTextField: JTextField
    private lateinit var alphaCountTextField: JTextField
    private lateinit var eraCountTextField: JTextField

    init {
        presenter.setView(this)
        presenter.init(testSetSize, trainingSetSize, classesCount)
    }

    override fun createGUI() {
        tablePanel = JPanel()
        tableScroll = JScrollPane(tablePanel)
        tableScroll.preferredSize = tablePanel.preferredSize

        startBtn = JButton("Старт")
        learningBtn = JButton("Обучение")
        loadTestSetBtn = JButton("Загрузить тестовый набор")
        loadTrainingSetBtn = JButton("Загрузить тренировочный набор")
        saveNeuronWebBtn = JButton("Сохранить сеть")
        loadNeuronWebBtn = JButton("Загрузить сеть")
        exitBtn = JButton("Выход")

        epsilonCountLabel = JLabel("Скорость обучения:")
        alphaCountLabel = JLabel("Момент обучения:")
        eraCountLabel = JLabel("Количество эпох:")
        statusTextLabel = JLabel("")
        errorTextLabel = JLabel("Текущая ошибка: -")
        prevErrorTextLabel = JLabel("Предыдущая ошибка: -")
        currentEraTextLabel = JLabel("Текущая эпоха: -")

        epsilonCountTextField = JTextField(epsilonCount.toString())
        alphaCountTextField = JTextField(alphaCount.toString())
        eraCountTextField = JTextField(eraCount.toString())

        controlPanel = JPanel()
        controlPanel.layout = GridLayout(classesCount - 1, 1, 5, 5)
        controlPanel.run {
            add(startBtn)
            add(learningBtn)
            add(loadTestSetBtn)
            add(loadTrainingSetBtn)
            add(saveNeuronWebBtn)
            add(loadNeuronWebBtn)
            add(epsilonCountLabel)
            add(epsilonCountTextField)
            add(alphaCountLabel)
            add(alphaCountTextField)
            add(eraCountLabel)
            add(eraCountTextField)
            add(currentEraTextLabel)
            add(prevErrorTextLabel)
            add(errorTextLabel)
            add(exitBtn)
        }

        progressBar = JProgressBar()
        progressPanel = JPanel()
        progressPanel.layout = GridLayout(2,1,5,5)
        progressPanel.add(statusTextLabel)
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
        startBtn.addActionListener {
            eraCount = eraCountTextField.text.toInt()
            epsilonCount = epsilonCountTextField.text.toDouble()
            alphaCount = alphaCountTextField.text.toDouble()
            currentSetSize = testSetSize
            presenter.onStartBtnClicked(eraCount, epsilonCount, alphaCount)
        }

        learningBtn.addActionListener {
            eraCount = eraCountTextField.text.toInt()
            epsilonCount = epsilonCountTextField.text.toDouble()
            alphaCount = alphaCountTextField.text.toDouble()
            currentSetSize = trainingSetSize
            presenter.onLearningBtnClicked(eraCount, epsilonCount, alphaCount)
        }

        loadTestSetBtn.addActionListener {presenter.onLoadTestSetBtnClicked()}
        loadTrainingSetBtn.addActionListener {presenter.onLoadTrainingSetBtnClicked()}
        saveNeuronWebBtn.addActionListener { presenter.onSaveBtnClicked() }
        loadNeuronWebBtn.addActionListener { presenter.onLoadBtnClicked() }
        exitBtn.addActionListener {presenter.onExitBtnClicked()}
    }

    override fun cleanTable(newRowsCount: Int, newColumnCount: Int) {
        tablePanel.removeAll()
        tablePanel.layout = GridLayout(newRowsCount, newColumnCount, 10,10)
        tableItems.clear()
    }

    override fun enableButtons(enable: Boolean) {
        startBtn.isEnabled = enable
        learningBtn.isEnabled = enable
        loadTrainingSetBtn.isEnabled = enable
        loadTestSetBtn.isEnabled = enable
        exitBtn.isEnabled = enable
        eraCountTextField.isEnabled = enable
        epsilonCountTextField.isEnabled = enable
        alphaCountTextField.isEnabled = enable
        loadNeuronWebBtn.isEnabled = enable
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

        //tableScroll.verticalScrollBar.value = tableScroll.verticalScrollBar.maximum / currentSetSize * numberOfSet
        tableScroll.verticalScrollBar.value = tablePanel.components[currentSetSize * (numberOfSet + 1)  + numberOfImage].y
    }

    override fun eraseTableColor() {
        for (row in tableItems)
            for (item in row)
                item.background = Color.WHITE
    }

    override fun setStatusText(text: String) {
        statusTextLabel.text = text
    }

    override fun setBarSize(min: Int, max: Int) {
        progressBar.minimum = min
        progressBar.maximum = max
        progressBar.value = min
    }

    override fun setErrorValue(value: String) {
        errorTextLabel.text = value
    }

    override fun setPrevErrorValue(value: String) {
        prevErrorTextLabel.text = value
    }

    override fun setCurrentEra(value: String) {
        currentEraTextLabel.text = value
    }
}