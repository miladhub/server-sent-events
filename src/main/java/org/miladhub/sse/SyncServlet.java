package org.miladhub.sse;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/SyncServlet")
public class SyncServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");

        PrintWriter writer = response.getWriter();

        for (int i = 0; i < 1000; i++) {
            writer.write("id: " + i + "\n");
            writer.write("data: " + System.currentTimeMillis() + "\n\n");
            writer.flush();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writer.close();
    }
}