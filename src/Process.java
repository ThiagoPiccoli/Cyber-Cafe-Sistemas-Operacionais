import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Process implements Runnable {
    final long CYCLE_TIME=100;
    final long TRY_AQUIRE_TIME=1000;
    private final char type; // G for gamer, F for freelancer, S for student
    private boolean is_first_done;
    private boolean is_done;
    private boolean is_running;
    private long cycles;
    private String id;

    private final Semaphore pcs;
    private final Semaphore headsets;
    private final Semaphore chairs;

    private final long start_time; // Tempo de início em ns
    private long queue_time; // Tempo na fila
    private long total_time; // Tempo total
    private long execution_time; // Tempo execução

    Process(char type, int cycles, Semaphore pcs, Semaphore headsets, Semaphore chairs) {
        this.is_running = false;
        this.id="";
        this.type = type;
        this.cycles = cycles;
        this.queue_time = 0;
        this.total_time = 0;
        this.execution_time = 0;
        this.is_first_done = false;
        this.is_done = false;
        this.pcs = pcs;
        this.headsets = headsets;
        this.chairs = chairs;
        this.start_time = System.nanoTime(); // Pegando o tempo atual no momento da criação da thread
    }

    @Override
    public void run() {
        long startRunTime=System.nanoTime();
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

    public void executionTime() {
//        System.out.println("Executando thread: " + this.type + " - " + this.cycles + " - " + this.queueTime);
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
        this.is_first_done = true;
    }

    public void finalprocessPrint() {
        System.out.print("Type: " + type);
        System.out.print(" Cycles: " + cycles);
        System.out.print(" Start Time: " + this.start_time / 1000000L + "ms ");
        System.out.print(" Execution Time: " + this.execution_time /1000000L+ "ms ");
        System.out.print(" Queue Time: " + (total_time-execution_time) /1000000L + "ms ");
        System.out.println(" Total Time: " + total_time /1000000L + "ms ");
    }

    public boolean freelancerRequest(long time){
        try {
            return pcs.tryAcquire(time, TimeUnit.MILLISECONDS) && chairs.tryAcquire(time, TimeUnit.MILLISECONDS);//tenta adquirir as licensas pelo tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean gamerRequest(long time){
        try {
            return pcs.tryAcquire(time, TimeUnit.MILLISECONDS) && headsets.tryAcquire(time, TimeUnit.MILLISECONDS);//tenta adquirir as licensas pelo tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean pcsRequest(long time){
        try {
            return pcs.tryAcquire(time, TimeUnit.MILLISECONDS);//tenta adquirir as licensas pelo tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean chairsRequest(long time){
        try {
            return chairs.tryAcquire(time, TimeUnit.MILLISECONDS);//tenta adquirir as licensas pelo tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean headsetsRequest(long time){
        try {
            return headsets.tryAcquire(time, TimeUnit.MILLISECONDS);//tenta adquirir as licensas pelo tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFirstDone() {
        return is_first_done;
    }

    public boolean isDone() {
        return is_done;
    }

    public char getType() {
        return type;
    }

    public boolean isRunning() {
        return is_running;
    }
    public void setId(String id){
        this.id=id;
    }
}