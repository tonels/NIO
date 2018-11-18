import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.Selector;

import org.junit.Test;

public class MyTest {

	@Test
	public void getSeletor(){
		try {
			Selector selector = Selector.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Channel channel = new Channel() {
			
			@Override
			public boolean isOpen() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void close() throws IOException {
				// TODO Auto-generated method stub
				
			}
		};
		
		
	}
	
}
