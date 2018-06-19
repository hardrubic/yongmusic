package com.hardrubic.music.biz.command

object RemoteControl {
    fun executeCommand(command: Command) {
        command.execute()
    }
}