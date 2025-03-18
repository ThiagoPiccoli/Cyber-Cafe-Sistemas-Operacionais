import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ProcessCreator implements Runnable {
    private LinkedList<Process> processes = new LinkedList<>();
    private Semaphore headsets;
    private Semaphore chairs;
    private Semaphore pcs;
    ProcessCreator(Semaphore pcs, Semaphore headsets, Semaphore chairs) {
        this.headsets = headsets;
        this.chairs = chairs;
        this.pcs = pcs;
    }
    @Override
    public void run() {
        for (int aux = 0; aux < 100; aux++) {
            try {
                Thread.sleep(50L);
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
                processes.add(new Process(type, cycles,pcs, headsets, chairs));
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        }
    }
    public LinkedList<Process> getProcess() {
        return processes;
    }
}