package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class Executor {
    protected BufferedReader socketReader;
    protected PrintWriter socketWriter;

    public String execute(String action) {
        socketWriter.println(action);
        socketWriter.flush();
        return readAnswer();
    }

    private String readAnswer() {
        String answer = null;
        try {
            answer = socketReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
