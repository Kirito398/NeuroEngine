package presentation.views

import domain.interfaces.MainViewInterface
import presentation.presenters.MainPresenter
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*

class MainView : MainViewInterface.View {
    private val presenter: MainViewInterface.Presenter = MainPresenter()
    private lateinit var tablePanel: JPanel
    private lateinit var tableScroll: JScrollPane
    private lateinit var controlPanel: JPanel
    private lateinit var progressPanel: JPanel
    private lateinit var progressBar: JProgressBar
    private lateinit var mainPanel: JPanel
    private lateinit var mainWindow: JFrame
    private lateinit var startBtn: JButton
    private lateinit var learningBtn: JButton
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
        exitBtn = JButton("Выход")

        controlPanel = JPanel()
        controlPanel.layout = GridLayout(10, 1, 5, 5)
        controlPanel.add(startBtn)
        controlPanel.add(learningBtn)
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
        mainWindow.setSize(640, 480)
        mainWindow.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        mainWindow.add(mainPanel)
        mainWindow.isVisible = true
    }

    override fun setListeners() {
        startBtn.addActionListener {presenter.onStartBtnClicked()}
        learningBtn.addActionListener {presenter.onLearningBtnClicked()}
        exitBtn.addActionListener {presenter.onExitBtnClicked()}
    }

    override fun loadTrainingSet() {
        createTable(true)
    }
    override fun loadTestSet() {
        createTable(false)
    }

    private fun createTable(mode: Boolean) {
        val columnCount: Int
        val rowsCount: Int
        val path: String

        if (mode) {
            columnCount = 26
            rowsCount = 2100
            path = "TrainSet"
        } else {
            columnCount = 26
            rowsCount = 140
            path = "Test"
        }

        tablePanel.removeAll()
        tablePanel.layout = GridLayout(rowsCount, columnCount, 10,10)

        progressBar.minimum = 0
        progressBar.maximum = columnCount * rowsCount

        enableButtons(false)

        object: SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                for (i in 1 .. rowsCount) {
                    loadSet(path, columnCount, i)
                    updateBar(columnCount * i)
                }
                return null
            }

            override fun done() {
                enableButtons(true)
            }
        }.execute()
    }

    private fun loadSet(path: String, size: Int, numberOfSet: Int) {
        for (i in 0 until size) {
            var image = ImageIO.read(File("$path//$i//$i ($numberOfSet).jpg"))
            image = resize(image, 25, 25)
            val icon = ImageIcon(image)
            val label = JLabel()
            label.icon = icon
            label.text = "$i"
            label.isOpaque = true
            label.background = Color.GREEN
            tablePanel.add(label)
        }
        tablePanel.revalidate()
    }

    private fun resize(img: BufferedImage, newWeight: Int, newHeight: Int): BufferedImage {
        val temp = img.getScaledInstance(newWeight, newHeight, Image.SCALE_SMOOTH)
        val dimg = BufferedImage(newWeight, newHeight, BufferedImage.TYPE_INT_ARGB)

        val g2d = dimg.createGraphics()
        g2d.drawImage(temp, 0, 0, null)
        g2d.dispose()

        return dimg
    }

    private fun updateBar(newValue: Int) {
        progressBar.value = newValue
    }

    private fun enableButtons(enable: Boolean) {
        startBtn.isEnabled = enable
        learningBtn.isEnabled = enable
        exitBtn.isEnabled = enable
    }
}