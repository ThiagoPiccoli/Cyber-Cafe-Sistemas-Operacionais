import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Semaphore;


public class Process implements Runnable {
    final long CYCLE_TIME=100;
    private final char type; // G for gamer, F for freelancer, S for student
    private boolean isFirstDone;
    private boolean isDone;
    private boolean isRunning;
    private long cycles;
    private String id;

    private final Semaphore pcs;
    private final Semaphore headsets;
    private final Semaphore chairs;

    private final long startTime; // Tempo de início em ns
    private long queueTime; // Tempo na fila
    private long totalTime; // Tempo total
    private long executionTime; // Tempo execução

    Process(char type, int cycles, Semaphore pcs, Semaphore headsets, Semaphore chairs) {
        this.isRunning = false;
        this.id="";
        this.type = type;
        this.cycles = cycles;
        this.queueTime = 0;
        this.totalTime = 0;
        this.executionTime = 0;
        this.isFirstDone = false;
        this.isDone = false;
        this.pcs = pcs;
        this.headsets = headsets;
        this.chairs = chairs;
        this.startTime = System.nanoTime(); // Pegando o tempo atual no momento da criação da thread
    }

    @Override
    public void run() {
        long startRunTime=System.nanoTime();
        isRunning = true;
        if (isFirstDone && !isDone) {
            if (type == 'G') {
                try {
                    if (chairs.availablePermits() >= 1) {
                        chairs.acquire();
                        isRunning = true;
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        chairs.release();
                        isDone = true;
                        totalTime = System.nanoTime()-startTime;//*não estamos levando o tempo de execução total da thread em consideração
                        queueTime = totalTime-(cycles*CYCLE_TIME);
                    } else {
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (type == 'F') {
                try {
                    if (headsets.availablePermits() >= 1) {
                        headsets.acquire();
                        isRunning = true;
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        headsets.release();
                        isDone = true;
                        isFirstDone = true;
                        totalTime = System.nanoTime()-startTime;//*não estamos levando o tempo de execução total da thread em consideração
                        queueTime = totalTime-(cycles*CYCLE_TIME);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (type == 'S') {//está aparentemente correto, mas TODO ainda temos que reposicionar na fila
            try {
                if (pcs.availablePermits() >= 1) {
                    pcs.acquire();
                    isRunning = true;
                    long execTime = System.nanoTime();
                    executionTime();
                    execTime = System.nanoTime() - execTime;
                    pcs.release();
                    isDone = true;
                    isFirstDone = true;
                    totalTime = System.nanoTime()-startTime;//*não estamos levando o tempo de execução total da thread em consideração
                    queueTime = totalTime-(cycles*CYCLE_TIME);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (type == 'G') {
            try {
                if (pcs.availablePermits() >= 1) {//erro de conseguir e no momento de executar não ter mais a permit
                    pcs.acquire();
                    isRunning = true;
                    if (headsets.availablePermits() >= 1) {
                        headsets.acquire();
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        pcs.release();
                        headsets.release();
                    } else {
                        pcs.release();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (type == 'F') {
            try {
                if (pcs.availablePermits() >= 1) {//erro de conseguir e no momento de executar não ter mais a permit
                    isRunning = true;
                    pcs.acquire();
                    if (chairs.availablePermits() >= 1) {
                        chairs.acquire();
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        pcs.release();
                        chairs.release();
                    } else {
                        pcs.release();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        isRunning = false;
    }

    public void executionTime() {
//        System.out.println("Executando thread: " + this.type + " - " + this.cycles + " - " + this.queueTime);
;
        try {
            long startExec = System.nanoTime();
            Thread.sleep(CYCLE_TIME * cycles);//espera o tempo de execução da "tarefa"
            long finalExec = System.nanoTime();
            this.executionTime += (finalExec - startExec);
            if (this.isFirstDone) {

                System.out.println( Thread.currentThread().threadId() +"-"+ executionTime/1000000L);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.isFirstDone = true;
    }

    public void finalprocessPrint() {
        System.out.print("Type: " + type);
        System.out.print(" Cycles: " + cycles);
        System.out.print(" Start Time: " + this.startTime / 1000000L + "ms ");
        System.out.print(" Execution Time: " + this.executionTime /1000000L+ "ms ");
        System.out.print(" Queue Time: " + queueTime /1000000L + "ms ");
        System.out.println(" Total Time: " + totalTime /1000000L + "ms ");
    }

    public boolean freelancerRequest(){
        try {
            if (pcs.availablePermits() >= 1) {//erro de conseguir e no momento de executar não ter mais a permit
                isRunning = true;
                pcs.acquire();
                if (chairs.availablePermits() >= 1) {
                    chairs.acquire();
                    long execTime = System.nanoTime();
                    executionTime();
                    execTime = System.nanoTime() - execTime;
                    pcs.release();
                    chairs.release();
                } else {
                    pcs.release();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFirstDone() {
        return isFirstDone;
    }

    public boolean isDone() {
        return isDone;
    }

    public char getType() {
        return type;
    }

    public boolean isRunning() {
        return isRunning;
    }
    public void setId(String id){
        this.id=id;
    }
}