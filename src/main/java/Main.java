import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser("./src/test/resources/correctJson.json");
            for (ArrayList<String> strings : parser.getTable()) {
                for (String el : strings) {
                    System.out.print(el + " ");
                }
                System.out.println();
            }
        }
        catch (IOException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
