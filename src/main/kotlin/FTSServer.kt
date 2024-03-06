package balbucio.fts

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket

class FTSServer(port: Int) : Runnable {

    private val serverSocket: ServerSocket = ServerSocket(port);
    private val filesAvaliable: MutableMap<String, File> = mutableMapOf(); // FILE E PATH
    private val serverThread: Thread = Thread(this);

    init {
        serverThread.name = "FTS-SERVER-THREAD"
        serverThread.start()
    }

    fun serveFile(file: File, path: String) {
        filesAvaliable.put(path, file);
    }

    /**
     * 1 = PEDIDO DE ARQUIVO
     */

    override fun run() {
        while (true) {
            val socket = serverSocket.accept();
            val input = DataInputStream(socket.getInputStream());
            val out = DataOutputStream(socket.getOutputStream());
            val operationCode = input.readInt();
            if (operationCode == 1) {
                val path = input.readUTF();
                if (filesAvaliable.containsKey(path)) {
                    val file = filesAvaliable[path];
                    val byteArray = ByteArray(file!!.length().toInt());
                    val inputStreamFile = BufferedInputStream(FileInputStream(file));
                    val bufferedOut = BufferedOutputStream(out);
                    out.writeBoolean(true);
                    out.writeInt(byteArray.size);
                    var loc = 0;
                    while(loc != -1){
                        bufferedOut.write(byteArray, 0, loc);
                        loc = inputStreamFile.read(byteArray);
                    }
                    bufferedOut.flush();
                    inputStreamFile.close();
                } else {
                    out.writeBoolean(false);
                }
            }

            input.close();
            out.flush();
            out.close();
        }
    }

}