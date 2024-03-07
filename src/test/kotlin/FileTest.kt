import balbucio.fts.FTSClient
import balbucio.fts.FTSServer
import org.junit.jupiter.api.BeforeAll
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFails

class FileTest {

    val client = FTSClient("localhost", 25566);
    val server = FTSServer(25566);

    @BeforeTest
    fun pre() {
        server.serveFile(File("static.zip"), "static");
    }

    @AfterTest
    fun after() {
        server.close();
    }
    @Test
    fun download(){
        client.requestFile("static", File("copyStatic.zip"));
    }

    @Test
    fun downloadAndRm(){
        server.serveFile(File("static.zip"), "static2", true);
        client.requestFile("static2", File("copyStatic.zip"));
        assertFails {
            client.requestFile("static2", File("copyStatic.zip"));
        }
    }

}