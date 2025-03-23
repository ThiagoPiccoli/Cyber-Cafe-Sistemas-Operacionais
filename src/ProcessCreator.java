import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ProcessCreator implements Runnable {
    private LinkedList<Process> processes = new LinkedList<>();
    private int process_count;
    private long cycle_time;
    private long try_acquire_time;
    private long queue_wait_time;
    private char queue_type;
    private Semaphore headsets;
    private Semaphore chairs;
    private Semaphore pcs;

    ProcessCreator(int process_count, long cycle_time, long try_acquire_time, long queue_wait_time, char queue_type, Semaphore pcs, Semaphore headsets, Semaphore chairs) {
        this.process_count = process_count;
        this.cycle_time = cycle_time;
        this.try_acquire_time = try_acquire_time;
        this.queue_wait_time = queue_wait_time;
        this.queue_type = queue_type;
        this.headsets = headsets;
        this.chairs = chairs;
        this.pcs = pcs;
    }

    @Override
    public void run() {
        try {
            if (queue_type == 'G') {
                for (int aux = 0; aux < process_count; aux++) {
                    Thread.sleep(queue_wait_time);//tempo de aguardo entre a geração de uma thread e outra
                    char type = 'G';
                    int cycles = (int) Math.round((Math.random() * 8)) + 2;//define ciclos de 2 a 10
                    processes.add(new Process(type, cycles, pcs, headsets, chairs, cycle_time, try_acquire_time));
                }
            } else if (queue_type == 'F') {
                for (int aux = 0; aux < process_count; aux++) {
                    Thread.sleep(queue_wait_time);//tempo de aguardo entre a geração de uma thread e outra
                    char type = 'F';
                    int cycles = (int) Math.round((Math.random() * 8)) + 2;//define ciclos de 2 a 10
                    processes.add(new Process(type, cycles, pcs, headsets, chairs, cycle_time, try_acquire_time));
                }
            } else if (queue_type == 'S') {
                for (int aux = 0; aux < process_count; aux++) {
                    Thread.sleep(queue_wait_time);//tempo de aguardo entre a geração de uma thread e outra
                    char type = 'S';
                    int cycles = (int) Math.round((Math.random() * 8)) + 2;//define ciclos de 2 a 10
                    processes.add(new Process(type, cycles, pcs, headsets, chairs, cycle_time, try_acquire_time));
                }
            } else {
                for (int aux = 0; aux < process_count; aux++) {
                    Thread.sleep(queue_wait_time);//tempo de aguardo entre a geração de uma thread e outra
                    char type = ' ';
                    int cycles = (int) Math.round((Math.random() * 8)) + 2;//define ciclos de 2 a 10
                    int intType = (int) Math.round((Math.random() * 2));//define o tipo
                    if (intType == 0) {
                        type = 'G';
                    } else if (intType == 1) {
                        type = 'F';
                    } else if (intType == 2) {
                        type = 'S';
                    }
                    processes.add(new Process(type, cycles, pcs, headsets, chairs, cycle_time, try_acquire_time));
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }
    }

    public LinkedList<Process> getProcess() {
        return processes;
    }
}