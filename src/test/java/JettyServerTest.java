import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

//JettyServer needs to run for tests to work
public class JettyServerTest {
    String resp;

    @Test
    void testConnection() {
        CountDownLatch lock = new CountDownLatch(1);
        HttpClient client = new HttpClient();
        try {
            client.start();
            client.POST("http://localhost:1337/")
                    .content(new StringContentProvider("end X"))
                    .send(new BufferingResponseListener() {
                        @Override
                        public void onComplete(Result result) {
                            if (!result.isFailed()) {
                                String[] response = new String(getContent()).split(" ");
                                resp = response[1].strip();
                                lock.countDown();
                            }
                        }
                    });
            lock.await(2000, TimeUnit.MILLISECONDS);
            assertEquals("X",resp);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    void testServer() {
        HttpClient client = new HttpClient();
        client.setMaxConnectionsPerDestination(41);
        CountDownLatch lock = new CountDownLatch(41);
        long expectedResult = 0L;
        List<Long> responses = new ArrayList<>();
        try {
            client.start();
            for (int i = 0; i < 40; i++) {
                expectedResult += i;
                client.POST("http://localhost:1337/")
                        .content(new StringContentProvider(Integer.toString(i)))
                        .send(new BufferingResponseListener() {
                            @Override
                            public void onComplete(Result result) {
                                if (!result.isFailed()) {
                                    String response = new String(getContent());
                                    Long responseResult = Long.parseLong(response.split(" ")[0]);
                                    responses.add(responseResult);
                                    lock.countDown();
                                }
                            }
                        });
            }
            Thread.sleep(2000); //give time for requests to be sent
            client.POST("http://localhost:1337/")
                    .content(new StringContentProvider("end X"))
                    .send(new BufferingResponseListener() {
                        @Override
                        public void onComplete(Result result) {
                            if (!result.isFailed()) {
                                String[] response = new String(getContent()).split(" ");
                                assertEquals("X", response[1].strip());
                                Long responseResult = Long.parseLong(response[0]);
                                responses.add(responseResult);
                                lock.countDown();
                            }
                        }
                    });
            lock.await(5,TimeUnit.SECONDS);
            for (Long l :
                    responses) {
                if(l != expectedResult){
                    fail();
                }
            }
        } catch (Exception e) {
            System.out.println("Caught exception in the client start: ");
            e.printStackTrace();
            fail();
        }

    }
}
