package org.miladhub.sse;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/AsyncServlet", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");

        final AsyncContext asyncContext = request.startAsync(request, response);

        asyncContext.start(() -> {
            try {
                PrintWriter writer = asyncContext.getResponse().getWriter();
                for (int i = 0; i < 1000; i++) {
                    System.out.println("sending event #" + i);
                    writer.write("id: " + i + "\n");
                    writer.write("data: " + System.currentTimeMillis() + "\n\n");
                    writer.flush();
                    Thread.sleep(1000);
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                asyncContext.complete();
            }
        });
    }
}