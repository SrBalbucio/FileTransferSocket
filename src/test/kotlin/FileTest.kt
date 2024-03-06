import balbucio.fts.FTSClient
import balbucio.fts.FTSServer
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test

class FileTest {

    val client = FTSClient("localhost", 25566);
    val server = FTSServer(25566);

    @BeforeTest
    fun pre() {
        server.serveFile(File("static.zip"), "static");
    }
    @Test
    fun download(){
        client.requestFile("static", File("copyStatic.zip"));
    }

}