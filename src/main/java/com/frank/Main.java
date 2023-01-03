package com.frank;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.PropertyListFormatException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Init");
        String dbFile = null;
        String outFile = null;
        try {
            dbFile = args[0]; // 0 is first arg
            outFile = args[1]; // 0 is first arg
            if (dbFile.contains("-h") || dbFile.contains("--help")) {
                throw new Exception();
            }
        } catch (Exception ignored) {
            System.err.println("Usage: java -jar <jar> <dbFile> <outFile>");
            System.exit(1);
        }
        Connection conn = connect(dbFile);
        String sql = "SELECT \"zAddAssetAttr-Reverse Location Data/Orig-Asset/HEX NSKeyed Plist\" from ZASSET";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileOutputStream fos = new FileOutputStream(outFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            PrintWriter writer = new PrintWriter(osw)) {

            while (rs.next()) {
                byte[] bytes;
                bytes = rs.getBytes(1); // resultset is 1 based
                if (bytes == null) {
                    writer.println();
                    continue;
                }
                var res = BinaryPropertyListParser.parse(bytes);
                var r2 = res.toJavaObject();

                HashMap root = (HashMap) r2;
                var list = (Object[]) root.get("$objects");
                var l1 = Arrays.stream(list).filter(o -> o instanceof String).map(o -> (String) o).toList();
                var st = l1.get(l1.size() - 2);
                st = st.replaceAll("–", "-");
                writer.println(st);

            }

        }
    }

    private static Connection connect(String dbFile) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + dbFile;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return conn;
    }
}