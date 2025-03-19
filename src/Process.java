import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Process implements Runnable {
    final long CYCLE_TIME=625;
    final long TRY_AQUIRE_TIME=1000;

    private final char type; // G for gamer, F for freelancer, S for student
    private boolean is_first_done;
    private boolean is_done;
    private boolean is_running;
    private long cycles;
    //Tempos
    private final long start_time; // Tempo de início em ns
    private long total_time; // Tempo total
    private long execution_time; // Tempo execução
    //Semáforos
    private final Semaphore pcs;
    private final Semaphore headsets;
    private final Semaphore chairs;
    Process(char type, int cycles, Semaphore pcs, Semaphore headsets, Semaphore chairs) {
        this.type = type;
        this.is_first_done = false;
        this.is_done = false;
        this.is_running = false;
        this.cycles = cycles;
        this.start_time = System.nanoTime(); // Pegando o tempo atual no momento da criação da thread
        this.total_time = 0;
        this.execution_time = 0;
        this.pcs = pcs;
        this.headsets = headsets;
        this.chairs = chairs;
    }

    @Override
    public void run() {
        is_running = true;
        if (is_first_done && !is_done) {//Primeira parte pronta
            if (type == 'G') {
                try {
                    if(chairs.tryAcquire(TRY_AQUIRE_TIME, TimeUnit.MILLISECONDS)){
                        System.out.println("Chairs acquired");
                        try {
                            long startExec = System.nanoTime();
                            Thread.sleep(CYCLE_TIME * cycles);//espera o tempo de execução da "tarefa"
                            long finalExec = System.nanoTime();
                            this.execution_time += (finalExec - startExec);
                            if (this.is_first_done) {
                                System.out.println( Thread.currentThread().threadId() +"-"+ execution_time/1000000L);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        chairs.release();
                        System.out.println("Chairs released");
                        total_time = System.nanoTime()-start_time;
                        is_done = true;
                    }else System.out.println("Request denied!");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (type == 'F') {
                try {
                    if(headsets.tryAcquire(TRY_AQUIRE_TIME, TimeUnit.MILLISECONDS)){
                        System.out.println("Headsets acquired");
                        try {
                            long startExec = System.nanoTime();
                            Thread.sleep(CYCLE_TIME * cycles);//espera o tempo de execução da "tarefa"
                            long finalExec = System.nanoTime();
                            this.execution_time += (finalExec - startExec);
                            if (this.is_first_done) {
                                System.out.println( Thread.currentThread().threadId() +"-"+ execution_time/1000000L);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        headsets.release();
                        System.out.println("Headsets released");
                        total_time = System.nanoTime()-start_time;
                        is_done = true;
                    }else System.out.println("Request denied!");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (type == 'S') {//está aparentemente correto, mas TODO ainda temos que reposicionar na fila
            try {
                if (pcs.tryAcquire(TRY_AQUIRE_TIME, TimeUnit.MILLISECONDS)) {
                    System.out.println("pcs acquired");
                    try {
                        long startExec = System.nanoTime();
                        Thread.sleep(CYCLE_TIME * cycles);//espera o tempo de execução da "tarefa"
                        long finalExec = System.nanoTime();
                        this.execution_time += (finalExec - startExec);
                            System.out.println( Thread.currentThread().threadId() +"-"+ execution_time/1000000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    is_done = true;
                    pcs.release();
                    System.out.println("pcs released");
                    total_time = System.nanoTime() - start_time;
                    is_done = true;
                }else System.out.println("Request denied!");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (type == 'G') {
            try {
                if(pcs.tryAcquire(TRY_AQUIRE_TIME, TimeUnit.MILLISECONDS) && headsets.tryAcquire(TRY_AQUIRE_TIME, TimeUnit.MILLISECONDS)){
                    System.out.println("Pcs and Headsets acquired");
                    try {
                        long startExec = System.nanoTime();
                        Thread.sleep(CYCLE_TIME * cycles);//espera o tempo de execução da "tarefa"
                        long finalExec = System.nanoTime();
                        this.execution_time += (finalExec - startExec);
                            System.out.println( Thread.currentThread().threadId() +"-"+ execution_time/1000000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    is_first_done = true;
                    pcs.release();
                    headsets.release();
                    System.out.println("Pcs and Headsets released");
                    total_time = System.nanoTime()-start_time;
                }else System.out.println("Request denied!");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (type == 'F') {
            try {
                if(pcs.tryAcquire(TRY_AQUIRE_TIME, TimeUnit.MILLISECONDS) && chairs.tryAcquire(TRY_AQUIRE_TIME, TimeUnit.MILLISECONDS)){
                    System.out.println("Pcs and Chairs acquired");
                    try {
                        long startExec = System.nanoTime();
                        Thread.sleep(CYCLE_TIME * cycles);//espera o tempo de execução da "tarefa"
                        long finalExec = System.nanoTime();
                        this.execution_time += (finalExec - startExec);
                            System.out.println( Thread.currentThread().threadId() +"-"+ execution_time/1000000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    is_first_done = true;
                    pcs.release();
                    chairs.release();
                    System.out.println("Pcs and Chairs released");
                    total_time = System.nanoTime()-start_time;
                }else System.out.println("Request denied!");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        is_running = false;
    }

    public void finalprocessPrint() {
        System.out.print("Type: " + type);
        System.out.print(" Cycles: " + cycles);
        System.out.print(" Start Time: " + this.start_time / 1000000L + "ms ");
        System.out.print(" Execution Time: " + this.execution_time /1000000L+ "ms ");
        System.out.print(" Queue Time: " + (total_time-execution_time) /1000000L + "ms ");
        System.out.println(" Total Time: " + total_time /1000000L + "ms ");
    }

    public char getType() {
        return type;
    }

    public boolean isDone() {
        return is_done;
    }
    public long getQueueTimeMs(){
        return (total_time-execution_time)/1000000L;
    }
    public long getExecutionTimeMs(){
        return (execution_time)/1000000L;
    }
    public boolean isRunning() {
        return is_running;
    }
}