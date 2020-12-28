package dev.garage.rpm.map.strategy

sealed class CommandStrategy {

    //Command will be added to the end of the commands queue.
    object AddStrategy : CommandStrategy()

    //Command will be added to the end of the commands queue. If commands queue contains a command of the same type,
    //then existing command will be removed.
    object AddSingleStrategy : CommandStrategy()

    //This strategy will clear current commands queue and then the given command will be put in.
    object SingleStrategy : CommandStrategy()

    //Command will be saved in the commands queue, but will be removed after its first execution
    object OncePerformStrategy : CommandStrategy()
}
