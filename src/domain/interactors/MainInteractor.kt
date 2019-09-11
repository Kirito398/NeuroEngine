package domain.interactors

class MainInteractor {
    private val networkInteractor = NetworkInteractor()

    init {
        networkInteractor.addLayer(5,5)
        networkInteractor.addLayer(10, 5)
    }

    fun showNetwork() {
        networkInteractor.showNetwork()
    }

    fun sendSignal(signal: MutableList<Double>) {
        val answer = networkInteractor.sendSignal(signal)

        println("Answer: ")
        for (item in answer) {
            print("$item ")
        }
    }
}