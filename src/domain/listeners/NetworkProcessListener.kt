package domain.listeners

interface NetworkProcessListener {
    fun onNewLayerAdded(addedLayerNumber: Int)
    fun currentLayer(currentLayerNumber: Int)
    fun networkAnswer(answer: Int, currentNumberOfImage: Int = -1, currentNumberOfSet: Int = -1)
    fun onEraChanged(currentEraNumber: Int)
    fun onSetChanged(currentSetNumber: Int, setSize: Int)
    fun onProcessStart(setSize: Int)
}