import domain.interactors.MainInteractor
import java.math.BigDecimal

fun main() {
    val interactor = MainInteractor()

    interactor.showNetwork()

    val signal = mutableListOf<BigDecimal>()

    for (i in 1 .. 5) {
        signal.add(BigDecimal(Math.random()))
    }

    interactor.sendSignal(signal)
}