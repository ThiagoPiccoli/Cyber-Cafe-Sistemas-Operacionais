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

    private final Semaphore pcs;
    private final Semaphore headsets;
    private final Semaphore chairs;

    private final long startTime; // Tempo de início em ns
    private long queueTime; // Tempo na fila
    private long firstExecTime;
    private long lastExecTime;
    private long releaseTime;
    private long totalTime; // Tempo total
    private long executionTime; // Tempo execução

    Process(char type, int cycles, Semaphore pcs, Semaphore headsets, Semaphore chairs) {
        this.isRunning = false;
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
        isRunning = true;
        if (isFirstDone && !isDone) {
            if (type == 'G') {
                try {
                    if (chairs.availablePermits() >= 1) {
                        lastExecTime = System.nanoTime();
                        chairs.acquire();
                        isRunning = true;
//                        System.out.println("Recursos adquiridos! Executando processo...");
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        executionTime += execTime;//ajustar tempo total
                        chairs.release();
                        releaseTime = System.nanoTime();
                        isRunning = false;
                        isDone = true;
                        totalTime = System.nanoTime()-startTime;//*não estamos levando o tempo de execução total da thread em consideração
                        queueTime = totalTime-(cycles*CYCLE_TIME);
//                        System.out.println("Execução completa, recursos liberados!");
                    } else {
//                        System.out.println("pc não disponivel, execução interrompida!");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (type == 'F') {
                try {
                    if (headsets.availablePermits() >= 1) {
                        lastExecTime = System.nanoTime();
                        headsets.acquire();
                        isRunning = true;
//                        System.out.println("Recursos adquiridos! Executando processo...");
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        executionTime += execTime;//ajustar tempo total
                        headsets.release();
                        releaseTime = System.nanoTime();
                        isRunning = false;
                        isDone = true;
                        isFirstDone = true;
                        totalTime = System.nanoTime()-startTime;//*não estamos levando o tempo de execução total da thread em consideração
                        queueTime = totalTime-(cycles*CYCLE_TIME);

//                        System.out.println("Execução completa, recursos liberados!");
                    } else {
//                        System.out.println("pc não disponivel, execução interrompida!");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (type == 'S') {//está aparentemente correto, mas TODO ainda temos que reposicionar na fila
            try {
                if (pcs.availablePermits() >= 1) {
                    firstExecTime = System.nanoTime();
                    lastExecTime = System.nanoTime();
                    pcs.acquire();
                    isRunning = true;
//                    System.out.println("Recursos adquiridos! Executando processo...");
                    long execTime = System.nanoTime();
                    executionTime();
                    execTime = System.nanoTime() - execTime;
                    executionTime += execTime;//ajustar tempo total
                    pcs.release();
                    releaseTime = System.nanoTime();
                    isRunning = false;
                    isDone = true;
                    isFirstDone = true;
                    totalTime = System.nanoTime()-startTime;//*não estamos levando o tempo de execução total da thread em consideração
                    queueTime = totalTime-(cycles*CYCLE_TIME);

//                    System.out.println("Execução completa, recursos liberados!");
                } else {
//                    System.out.println("pc não disponivel, execução interrompida!");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (type == 'G') {
            try {
                if (pcs.availablePermits() >= 1) {//erro de conseguir e no momento de executar não ter mais a permit
                    pcs.acquire();
                    isRunning = true;
//                    System.out.println("pc disponivel!");
                    if (headsets.availablePermits() >= 1) {
                        firstExecTime = System.nanoTime();
                        headsets.acquire();
//                        System.out.println("headset disponivel!");
//                        System.out.println("Recursos adquiridos! Executando processo...");
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        executionTime += execTime;//ajustar tempo total
                        pcs.release();
                        headsets.release();
                        releaseTime = System.nanoTime();
                        isRunning = false;
//                        System.out.println("Execução completa, recursos liberados!");
                    } else {
//                        System.out.println("Execução bloqueada!");
                        pcs.release();
                    }
                } else {
//                    System.out.println("Execução bloqueada!");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (type == 'F') {
            try {
                if (pcs.availablePermits() >= 1) {//erro de conseguir e no momento de executar não ter mais a permit
                    isRunning = true;
                    pcs.acquire();
//                    System.out.println("pc disponivel!");
                    if (chairs.availablePermits() >= 1) {
                        firstExecTime = System.nanoTime();
                        chairs.acquire();
//                        System.out.println("headset disponivel!");
//                        System.out.println("Recursos adquiridos! Executando processo...");
                        long execTime = System.nanoTime();
                        executionTime();
                        execTime = System.nanoTime() - execTime;
                        executionTime += execTime;//ajustar tempo total
                        pcs.release();
                        chairs.release();
                        releaseTime = System.nanoTime();
                        isRunning = false;
//                        System.out.println("Execução completa, recursos liberados!");
                    } else {
//                        System.out.println("Execução bloqueada!");
                        pcs.release();
                    }
                } else {
//                    System.out.println("Execução bloqueada!");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void executionTime() {
//        System.out.println("Executando thread: " + this.type + " - " + this.cycles + " - " + this.queueTime);
        long startExec = System.nanoTime();
        try {
            Thread.sleep(CYCLE_TIME * cycles);//espera o tempo de execução da "tarefa"
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executionTime += (System.nanoTime() - startExec);
        this.isFirstDone = true;
    }

    public void finalprocessPrint() {
        System.out.print("Type: " + type);
        System.out.print(" Cycles: " + cycles);
        System.out.print(" Start Time: " + this.startTime / 1000000L + "ms ");
        System.out.print(" Execution Time: " + executionTime / 1000000L + "ms ");
        System.out.print(" Queue Time: " + queueTime / 1000000L + "ms ");
        System.out.println(" Total Time: " + totalTime / 1000000L + "ms ");
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
}