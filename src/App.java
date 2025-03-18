import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/*TODO estamos no caminho correto? como corrigir o erro da ultima execução? O que mais acrescentar? O semaphore gerencia o deadlock ou não?
TODO como checar starvation e deadlock?
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
        Thread thread = new Thread(creator);
        thread.start();
        Thread.sleep(200);

        while (thread.isAlive() || !processes.isEmpty() || isRunning(threads_running)) {
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
                Process process = creator.getProcess().get(aux);
                Thread t = new Thread(process);
                t.start();
                threads_running.add(t);
            }

        }
//        if (!processes.isEmpty()) {
//            while (!processes.isEmpty()) {
//                int aux = 0;
//                Thread.sleep(50);
//                while (processes.get(aux).isRunning()) {//se está em execução passa para o próximo processo
//                    aux++;
//                    Thread.sleep(50);
//                }
//                if (processes.get(aux).isDone()) {//se está pronto remove da fila de processos e adiciona na fila de finalizados
//                    finished_processes.add(processes.get(aux));
//                    processes.remove(aux);
//                }
//                Process process = creator.getProcess().get(aux);
//                Thread t = new Thread(process);
//                t.start();
//                //threadsRunning.add(t);
//            }
//        }
        for (Process process : finished_processes) {
            process.finalprocessPrint();
        }
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