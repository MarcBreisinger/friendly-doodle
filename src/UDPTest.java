import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class UDPTest {

    EchoClient client;
    
	@Before
	public void setup() {
		new EchoServer().start();
		client = new EchoClient();
	}
	
	@Test
	public void whenCanSendAndREceivePacket_thenCorrect() {
		String echo = client.sendEcho("hello server");
		assertEquals("hello server", echo);
		echo = client.sendEcho("server is working");
		assertFalse(echo.equals("hello server"));
	}
	
	@After
	public void tearDown() {
		client.sendEcho("end");
		client.close();
	}

}
