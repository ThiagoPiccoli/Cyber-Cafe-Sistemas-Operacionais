import java.util.concurrent.TimeUnit;

public class App{
    public static void main(String[] args) throws InterruptedException {
        ProcessCreator creator = new ProcessCreator();
        Thread thread = new Thread(creator);
        thread.setDaemon(true);
        thread.start();
//        try {
//            thread.join(); // Espera a thread terminar
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Thread.sleep(200);
        Process process = creator.getProcess().getLast();
        process.processPrint();
        for (int aux = 0; aux < 10; aux++) {
            Thread.sleep(10);
            process = creator.getProcess().getLast();
            process.processPrint();
        }
    }
}