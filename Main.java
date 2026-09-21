import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Main {

    // ====== CHANGE THESE TO MATCH YOUR MYSQL ======
    static final String DB_URL = "jdbc:mysql://localhost:3306/student_db";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "root";
    // ==============================================

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", ex -> send(ex, formPage()));
        server.createContext("/students", ex -> send(ex, studentsPage()));
        server.createContext("/style.css", ex -> send(ex, Files.readString(Path.of("style.css")), "text/css"));

        server.createContext("/add", ex -> {
            Map<String, String> f = readForm(ex);
            try (Connection c = connect();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO students (name, email, course) VALUES (?, ?, ?)")) {
                ps.setString(1, f.get("name"));
                ps.setString(2, f.get("email"));
                ps.setString(3, f.get("course"));
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            redirect(ex, "/students");
        });

        server.createContext("/delete", ex -> {
            Map<String, String> f = readForm(ex);
            try (Connection c = connect();
                 PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE id = ?")) {
                ps.setInt(1, Integer.parseInt(f.get("id")));
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            redirect(ex, "/students");
        });

        server.start();
        System.out.println("Running at http://localhost:8080");
    }

    static Connection connect() throws Exception {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // ---------- Pages ----------

    static String formPage() {
        return page("<h1>Student Management</h1>"
                + "<form method='post' action='/add'>"
                + "<label>Student Name <input type='text' name='name' required></label>"
                + "<label>Email <input type='email' name='email' required></label>"
                + "<label>Course <input type='text' name='course' required></label>"
                + "<button type='submit'>Add Student</button> "
                + "<a class='btn' href='/students'>View Students</a>"
                + "</form>");
    }

    static String studentsPage() {
        StringBuilder rows = new StringBuilder();
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM students ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.append("<tr><td>").append(rs.getInt("id")).append("</td>")
                    .append("<td>").append(esc(rs.getString("name"))).append("</td>")
                    .append("<td>").append(esc(rs.getString("email"))).append("</td>")
                    .append("<td>").append(esc(rs.getString("course"))).append("</td>")
                    .append("<td><form method='post' action='/delete'>")
                    .append("<input type='hidden' name='id' value='").append(rs.getInt("id")).append("'>")
                    .append("<button class='red' type='submit'>Delete</button></form></td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            rows.append("<tr><td colspan='5'>Database error: ").append(esc(e.getMessage())).append("</td></tr>");
        }
        return page("<h1>All Students</h1>"
                + "<table><tr><th>ID</th><th>Name</th><th>Email</th><th>Course</th><th>Action</th></tr>"
                + rows + "</table>"
                + "<a class='btn' href='/'>Back to Form</a>");
    }

    static String page(String body) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Student Management</title>"
                + "<link rel='stylesheet' href='/style.css'></head><body><div class='box'>"
                + body + "</div></body></html>";
    }

    // ---------- Small helpers ----------

    static Map<String, String> readForm(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    static void send(HttpExchange ex, String html) throws IOException {
        send(ex, html, "text/html; charset=UTF-8");
    }

    static void send(HttpExchange ex, String body, String type) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    static void redirect(HttpExchange ex, String to) throws IOException {
        ex.getResponseHeaders().set("Location", to);
        ex.sendResponseHeaders(303, -1);
        ex.close();
    }

    static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;");
    }
}
