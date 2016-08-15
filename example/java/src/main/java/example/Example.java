package example;

import com.bugsnag.Client;

class Example {
    public static void main(String[] args) throws Exception {
        new Client("your-api-key-here");

        throw new Exception("Something bad happened!");
    }
}
