package balbucio.fts

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.Socket

class FTSClient(private var ip: String, private var port: Int) {

    fun getSocket(): Socket {
        return Socket(ip, port);
    }

    fun requestFile(path: String, pathTo: File): Boolean {
        if(pathTo.parentFile != null && !pathTo.parentFile.exists() && pathTo.isDirectory){
            pathTo.parentFile.mkdirs();
        }
        pathTo.createNewFile();
        val socket = getSocket();
        val instream = DataInputStream(socket.getInputStream());
        val outstream = DataOutputStream(socket.getOutputStream());
        outstream.writeInt(1);
        outstream.writeUTF(path);
        outstream.flush();

        val rsp = instream.readBoolean();
        val size: Int = instream.readInt();
        if (rsp) {
            val fileOutputStream = BufferedOutputStream(FileOutputStream(pathTo));
            val bufferedInputStream = BufferedInputStream(instream);
            val byteArray = ByteArray(size);
            var read = 0;
            var current = 0;
            read = bufferedInputStream.read(byteArray, 0, byteArray.size);
            current = read;

            do {
                read = bufferedInputStream.read(byteArray, current, (byteArray.size - current));

                if (read >= 0)
                    current += read;
            } while (read > 0);

            fileOutputStream.write(byteArray, 0, current);
            fileOutputStream.flush();
            fileOutputStream.close();
        }

        return rsp;
    }

}