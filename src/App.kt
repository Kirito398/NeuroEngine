import data.repositories.MainRepository
import domain.interactors.MainInteractor
import presentation.views.MainView

fun main(args: Array<String>) {
    /*val interactor = MainInteractor()

    interactor.showNetwork()

    val signal = mutableListOf<Double>()

    for (i in 1 .. 5) {
        signal.add(Math.random())
    }

    interactor.sendSignal(signal)*/

    val mainRepository = MainRepository()
    val mainWindow = MainView(MainInteractor(mainRepository))
}