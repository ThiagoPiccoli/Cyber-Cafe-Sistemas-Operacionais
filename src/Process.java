import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Process implements Runnable {
    char type; // G for gamer, F for freelancer, S for student
    boolean isFirstDone;
    boolean isDone;
    int cycles;

    long startTime; // Tempo de início em ns
    long queueTime; // Tempo na fila
    long totalTime; // Tempo total
    long executionTime; // Tempo execução
    Process(char type, int cycles) {
        this.type = type;
        this.cycles = cycles;
        this.queueTime = 0;
        this.totalTime = 0;
        this.executionTime = 0;
        this.isFirstDone = false;
        this.isDone = false;
        this.startTime = System.nanoTime(); // Pegando o tempo atual no momento da criação da thread
    }

    @Override
    public void run() {
        if(isFirstDone && !isDone) {
            if(type == 'G' || type == 'F') {
                isDone = true;
                executionTime();
                queueTime=executionTime-startTime;
                totalTime = executionTime + queueTime;//*não estamos levando o tempo de execução total da thread em consideração
            }
        }
        else if (type == 'S') {
            executionTime();
            isDone = true;
            queueTime=executionTime-startTime;
            totalTime = executionTime + queueTime;//*não estamos levando o tempo de execução total da thread em consideração
        }
        else if (type == 'G') {
            executionTime();
        }
        else if (type == 'F') {
            executionTime();
        }
    }

    private void executionTime() {
        System.out.println("Executando thread: " + this.type + " - " + this.cycles + " - " + this.queueTime);
        isFirstDone =  true;
        long execTime = System.nanoTime();
        try {
            Thread.sleep(100L *cycles);//espera o tempo de execução da "tarefa"
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        execTime = System.nanoTime() - execTime;
        executionTime += execTime;//ajustar tempo total
    }

    public void processPrint() {
        System.out.println("Type: " + type);
        System.out.println("Cycles: " + cycles);
        System.out.println("Start Time: " + this.startTime/1000000 + "ms ");
    }
}