package com.shadow.socket;

import com.shadow.socket.netty.server.SocketServer;
import com.shadow.socket.netty.server.SocketServerBuilder;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author nevermore on 2014/11/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class SocketTest {
    SocketServer server;

    @Test
    public void test() throws InterruptedException {
        server = SocketServerBuilder.newBuilder().port(8888).build();
        try {
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        server.shutdown();
    }
}
