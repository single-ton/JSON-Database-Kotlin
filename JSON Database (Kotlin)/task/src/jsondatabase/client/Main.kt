package jsondatabase.client

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val controller = Controller()
    controller.ProcessMainInput(args)

}

class Controller{
    val address = "127.0.0.1"
    val port = 23456
    var socket:Socket
    init {
        while(true){
            try {
                socket = Socket(InetAddress.getByName(address), port)
                println("Client started!")
                break
            }
            catch (e:Exception){
                Thread.sleep(100)
            }
        }

    }
    val input = DataInputStream(socket.getInputStream())
    val output = DataOutputStream(socket.getOutputStream())
    var ui = ClientUi(this, output, input)

    fun ProcessMainInput(args:Array<String>){
        if(args.size>1&& args[0]=="-t"){
            if(args[1]=="exit")
                ui.exitServer()
            else if(args[1]=="get")
                ui.getCommand(args[3])
            else if(args[1]=="set")
                ui.setCommand(args[3], args[5])
            else if(args[1]=="delete")
                ui.deleteCommand(args[3])
        }
        socket.close()
    }


}
class ClientUi(val controller: Controller,
               val output: DataOutputStream,
               val input: DataInputStream){
    val receivedMessage = "Received: %s"
    val sentMessage = "Sent: %s"
    fun received(message:String){
        println(String.format(receivedMessage, message))
    }
    fun sent(message:String){
        writeToserver(message)
        println(String.format(sentMessage, message))
    }
    fun writeToserver(message:String){
        output.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }
    fun getCommand(index:String){
        sent(String.format("get %s", index))
        val fromServer = input.readLine()
        received(fromServer)
    }
    fun setCommand(index:String, message:String){
        sent(String.format("set %s %s", index, message))
        val fromServer = input.readLine()
        received(fromServer)
    }
    fun deleteCommand(index:String){
        sent(String.format("delete %s", index))
        val fromServer = input.readLine()
        received(fromServer)
    }
    fun exitServer(){
        sent("exit")
        val fromServer = input.readLine()
        received(fromServer)
    }
}