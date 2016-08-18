package example;

import com.bugsnag.Client;

class Example {
    public static void main(String[] args) throws Exception {
        Client bugsnag = new Client("your-api-key-here");

        bugsnag.setAsynchronousNotification(false);

        throw new Exception("Something bad happened!");
    }
}
