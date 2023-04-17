package jsondatabase.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.nio.charset.Charset
import kotlin.system.exitProcess

fun main() {
    val controller = Controller()
    controller.ProcessMainInput()

}
class Controller{
    val exitCommand = "exit"
    val getCommand = "get"
    val setCommand = "set"
    val deleteCommand = "delete"
    val address = "127.0.0.1"
    val port = 23456
    lateinit var server:ServerSocket
    init{

        while(true)
        {
            try {
                server = ServerSocket(port, 50, InetAddress.getByName(address))
                println("Server started!")
                break
            }catch (e:Exception){
                Thread.sleep(100)
            }
        }

    }
    var socket = server.accept()
    var input = DataInputStream(socket.getInputStream())
    var output = DataOutputStream(socket.getOutputStream())
    var ui = ServerUi(this, output)
    val dataUi = Ui()
    fun ProcessMainInput() {
        var command:String
        do{
            command = input.readLine()
            if(command==exitCommand) {
                ui.sent("OK")
                output.flush()
                socket.close()
                exitProcess(0)
            }
            else if(command.startsWith(getCommand)){
                val index = command.split(" ")[1].toInt()
                ui.sent(dataUi.get(index))
            }else if(command.startsWith(setCommand)){
                val index = command.split(" ")[1].toInt()
                val message = command.removePrefix(command.substring(0,command.indexOf(index.toString()+" ")))
                ui.sent(dataUi.set(index,message))
            }else if(command.startsWith(deleteCommand)){
                val index = command.split(" ")[1].toInt()
                ui.sent(dataUi.delete(index))
            }
            socket = server.accept()
            input = DataInputStream(socket.getInputStream())
            output = DataOutputStream(socket.getOutputStream())
            ui = ServerUi(this, output)

        }while(command!=exitCommand)
    }
}
class ServerUi(val controller: Controller, val output: OutputStream){
    val receivedMessage = "Received: %s"
    val sentMessage = "Sent: %s"
    fun received(message:String){
        println(String.format(receivedMessage, message))
    }
    fun sent(message:String){
        writeToserver(message)
    }
    fun writeToserver(message:String){
        output.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }
}
class Ui{
    val database = mutableMapOf<Int, String>()
    fun ProcessInput(){
        var input=""
        do{
            input = readln()
            if(input.startsWith("set")){
                val cp = input.split(" ")
                set(cp[1].toInt(),input.substring(cp.indexOf(cp[2])))
            }
            else if(input.startsWith("get")){
                val cp = input.split(" ")
                get(cp[1].toInt())
            }
            else if(input.startsWith("delete")){
                val cp = input.split(" ")
                delete(cp[1].toInt())
            }
        }while(input!="exit")
    }
    fun set(index:Int, str:String):String{
        if(index>100||index<0) {
            return("Error")
        }
        database[index]=str
        return("OK")
    }
    fun get(index:Int):String{
        if (database.containsKey(index))
            if(database[index]!="")
                return database[index]!!
            else
                return "ERROR"
        else
            return "ERROR"
    }
    fun delete(index:Int):String{
        if(index>100||index<0) {
            return("ERROR")
        }
        if (database.containsKey(index))
            database[index]=""
        return("OK")
    }
}