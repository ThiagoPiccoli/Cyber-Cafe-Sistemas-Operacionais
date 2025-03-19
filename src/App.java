import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/*
TODO Completar o report/tirar a depuração
TODO Criar uma fila para tentar forçar um deadlock
TODO Report de uma fila com todos processos
1 seg tempo real = 8 min de simulação
1 ciclo = 5 min de simulação = 625ms tempo real
*/

public class App {
    public static void main(String[] args) throws InterruptedException {
        Semaphore pcs = new Semaphore(10);
        Semaphore headsets = new Semaphore(6);
        Semaphore chairs = new Semaphore(8);
        ProcessCreator creator = new ProcessCreator(pcs, headsets, chairs);
        LinkedList<Process> processes = creator.getProcess();
        LinkedList<Process> finished_processes = new LinkedList<>();
        LinkedList<Thread> threads_running = new LinkedList<>();
        Clock clock=new Clock(20);
        Thread clock_thread = new Thread(clock);
        Thread creator_thread = new Thread(creator);
        creator_thread.setDaemon(true);
        clock_thread.start();
        creator_thread.start();
        while (creator_thread.isAlive() && processes.isEmpty()) {//aguarda até o primeiro objeto ser criado pela creator_thread
        }
        while ((creator_thread.isAlive() || !processes.isEmpty() || isRunning(threads_running))&& clock_thread.isAlive()) {
            if (processes.isEmpty()) {
                Thread.sleep(2000);
                break;
            }//se a fila estiver em criação fica em loop
            Thread.sleep(50);//se estiver vazio aguarda um tempo
            int aux = 0;
            while (processes.size() > aux+1) {
                if (processes.get(aux).isRunning()) {
                    aux++;
                } else break;
            }
            if (processes.get(aux).isDone() && !processes.get(aux).isRunning()) {//se está pronto remove da fila de processos e adiciona na fila de finalizados
                finished_processes.add(processes.get(aux));
                processes.remove(aux);
                System.out.println("teste");
            } else {
                if(!processes.get(aux).isRunning()){

                    Process process = creator.getProcess().get(aux);
                    Thread t = new Thread(process);
                    t.start();
//                    t.setDaemon(true);
                    threads_running.add(t);
                }
            }
        }
        long queue_time_avg=0;
        long execution_time_avg=0;
        int pcs_count=0;
        int headsets_count=0;
        int chairs_count=0;
        for (Process process : finished_processes) {
            if(process.getType()=='G'){
                headsets_count++;
                chairs_count++;
                pcs_count++;
            }else if(process.getType()=='F'){
                headsets_count++;
                chairs_count++;
                pcs_count++;
            }else{
                pcs_count++;
            }
            process.finalprocessPrint();
            queue_time_avg+=process.getQueueTimeMs();
            execution_time_avg+=process.getExecutionTimeMs();
        }
        System.out.println("Clientes atendidos: " + finished_processes.size());
        System.out.println("Clientes não atendidos: " + processes.size());
        System.out.println("Tempo médio de fila: "+queue_time_avg/ finished_processes.size()+"ms");
        System.out.println("Tempo médio de execução: "+execution_time_avg/ finished_processes.size()+"ms");
        System.out.println("Pcs usados: "+pcs_count);
        System.out.println("Headsets usados: "+headsets_count);
        System.out.println("Chairs usados: "+chairs_count);
    }
    public static boolean isRunning(LinkedList<Thread> threads_running) {
        LinkedList<Thread> toRemove = new LinkedList<>();
        boolean running = false;

        for (Thread t : threads_running) {
            if (!t.isAlive()) {
                toRemove.add(t); // Marca a thread para remoção
            } else {
                running = true; // Se pelo menos uma thread estiver ativa, retorna true
            }
        }
        // Remove as threads finalizadas após a iteração para evitar erros de concorrência
        threads_running.removeAll(toRemove);
        return running;
    }
}