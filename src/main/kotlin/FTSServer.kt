package balbucio.fts

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.util.AbstractMap

class FTSServer(port: Int) : Runnable {

    private val serverSocket: ServerSocket = ServerSocket(port);
    private val filesAvaliable: MutableMap<String, AbstractMap.SimpleEntry<File, Boolean>> = mutableMapOf(); // FILE E PATH
    private val serverThread: Thread = Thread(this);
    private var running: Boolean = false;

    init {
        serverThread.name = "FTS-SERVER-THREAD"
        serverThread.start()
        running = true;
    }

    fun serveFile(file: File, path: String) {
        serveFile(file, path, true);
    }

    /**
     * Serve um arquivo
     */
    fun serveFile(file: File, path: String, rmAfterDownload: Boolean) {
        filesAvaliable.put(path, AbstractMap.SimpleEntry(file, rmAfterDownload));
    }

    /**
     * Remove um arquivo
     */
    fun removeFile(path: String){
        filesAvaliable.remove(path);
    }

    fun close(){
        running = false;
        serverSocket.close();
        serverThread.interrupt();
        filesAvaliable.clear();
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
                    val file = filesAvaliable[path]!!.key;
                    val rm = filesAvaliable[path]!!.value;
                    val byteArray = ByteArray(file!!.length().toInt());
                    val inputStreamFile = BufferedInputStream(FileInputStream(file));
                    val bufferedOut = BufferedOutputStream(out);
                    out.writeBoolean(true);
                    out.writeInt(byteArray.size);
                    var loc = 0;
                    while (loc != -1) {
                        bufferedOut.write(byteArray, 0, loc);
                        loc = inputStreamFile.read(byteArray);
                    }
                    bufferedOut.flush();
                    inputStreamFile.close();
                    if(rm){
                        filesAvaliable.remove(path);
                    }
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