package org.miladhub.sse;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/AsyncServletScheduled", asyncSupported = true)
public class AsyncServletScheduled extends HttpServlet {
    private final Queue<AsyncContext> longReqs = new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        final Runnable notifier = () -> {
            final Iterator<AsyncContext> iterator = longReqs.iterator();
            //not using for : in to allow removing items while iterating
            while (iterator.hasNext()) {
                AsyncContext ac = iterator.next();
                Random random = new Random();
                final ServletResponse res = ac.getResponse();
                PrintWriter out;
                try {
                    out = res.getWriter();
                    int id = random.nextInt(100) + 1;
                    System.out.println("sending event #" + id);
                    out.write("id: " + id + "\n");
                    out.write("data: " + String.valueOf(id) + ", num of clients = " + longReqs.size() + "\n\n");
                    //checkError calls flush,
                    //and flush() does not throw IOException
                    if (out.checkError()) {
                        iterator.remove();
                    }
                } catch (IOException ignored) {
                    iterator.remove();
                }
            }
        };
        service = Executors.newScheduledThreadPool(10);
        service.scheduleAtFixedRate(notifier, 1, 1, TimeUnit.SECONDS);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");

        final AsyncContext ac = request.startAsync();
        ac.setTimeout(60 * 1000);
        ac.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws
                    IOException {
                longReqs.remove(ac);
            }

            @Override
            public void onTimeout(AsyncEvent event) throws
                    IOException {
                longReqs.remove(ac);
            }

            @Override
            public void onError(AsyncEvent event) throws
                    IOException {
                longReqs.remove(ac);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws
                    IOException {
            }
        });
        longReqs.add(ac);
    }
}