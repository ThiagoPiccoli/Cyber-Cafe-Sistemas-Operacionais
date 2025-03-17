import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/*TODO Como evitar o starvation ex.: se uma fila for grande o sufiente e o café fechar antes ocorreu starvation?
TODO como checar starvation e deadlock?
*/
public class App{
    public static void main(String[] args) throws InterruptedException {
        Semaphore pcs = new Semaphore(10);
        Semaphore headsets = new Semaphore(6);
        Semaphore chairs = new Semaphore(8);
        ProcessCreator creator = new ProcessCreator(pcs, headsets, chairs);
        LinkedList<Process> processes = creator.getProcess();
        LinkedList<Process> finished_processes = new LinkedList<>();
        LinkedList<Thread> threadsRunning = new LinkedList<>();
        Thread thread = new Thread(creator);
        thread.start();

        while (thread.isAlive()) {//se a fila estiver em criação fica em loop
            Thread.sleep(500);
            while (!processes.isEmpty()) {//se estiver vazio aguarda um tempo
                int aux = 0;
                while (processes.size() > aux+1){
                    if(processes.get(aux).isRunning()){
                        aux++;
                    }else break;
                }
                if (processes.get(aux).isDone()) {//se está pronto remove da fila de processos e adiciona na fila de finalizados
                    finished_processes.add(processes.get(aux));
                    processes.remove(aux);
                }else{
                    Process process = creator.getProcess().get(aux);
                    Thread t = new Thread(process);
                    t.start();
                }
            }

        }
        if (!processes.isEmpty()) {
            while (!processes.isEmpty()) {
                int aux = 0;
                Thread.sleep(50);
                while (processes.get(aux).isRunning()) {//se está em execução passa para o próximo processo
                    aux++;
                    Thread.sleep(50);
                }
                if (processes.get(aux).isDone() && !processes.get(aux).isRunning()) {//se está pronto remove da fila de processos e adiciona na fila de finalizados
                    finished_processes.add(processes.get(aux));
                    processes.remove(aux);
                }
                Process process = creator.getProcess().get(aux);
                Thread t = new Thread(process);
                t.start();
                //threadsRunning.add(t);
            }
        }
        for (Process process : finished_processes) {
            process.finalprocessPrint();
        }
    }
}