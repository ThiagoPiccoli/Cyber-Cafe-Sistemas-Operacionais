import java.util.LinkedList;

public class ProcessCreator implements Runnable {
    private LinkedList<Process> processes = new LinkedList<>();

    @Override
    public void run() {
        for (int aux = 0; aux < 100; aux++) {
            try {
                Thread.sleep(100);

                char type = ' ';
                int cycles = (int) Math.round((Math.random() * 8)) + 2;
                int intType = (int) Math.round((Math.random() * 2));

                if (intType == 0) {
                    type = 'G';
                } else if (intType == 1) {
                    type = 'F';
                } else if (intType == 2) {
                    type = 'S';
                }
//                Process process = new Process(type, cycles);
                processes.add(new Process(type, cycles));
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        }

    }

    public LinkedList<Process> getProcess() {
        return processes;
    }
}