import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/*
TODO Report de uma fila com todos processos
Informações da simulação
1 seg tempo real = 8 min de simulação
60 seg tempo real = 8 horas de simulação
1 ciclo = 5 min de simulação = 625ms tempo real
Os clientes usam entre 2 a 10 ciclos os recursos

para execução:
input="nº de pcs, nº de headsets, nº de cadeiras, tempo de execução(s), tempo do ciclo(ms), tempo de tentativa de obtenção do semáforo(ms), tempo de espera para criação de processo, quantidade de processos,
preset da fila"
presets(para tentativa de geração de deadlocks, usando apenas um tipo de usuário):
R=Random
G=Só gamers
F=Só freelancers
S=Só estudantes

**decidimos deixar os usuários que estão dentro do café no momento em que ele é fechado terminarem suas tarefas para então terminar de executar a simulação
*/
public class App {
    public static void main(String[] args) throws InterruptedException {
        //args:
//        args = new String[9];
//        args[0]="10";
//        args[1]="6";
//        args[2]="8";
//        args[3]="60";
//        args[4]="625";
//        args[5]="500";
//        args[6]="500";
//        args[7]="200";
//        args[8]="G";
        //
        Semaphore pcs = new Semaphore(Integer.parseInt(args[0]));
        Semaphore headsets = new Semaphore(Integer.parseInt(args[1]));
        Semaphore chairs = new Semaphore(Integer.parseInt(args[2]));
        ProcessCreator creator = new ProcessCreator(Integer.parseInt(args[7]), Long.parseLong(args[4]), Long.parseLong(args[5]), Long.parseLong(args[6]), args[8].charAt(0), pcs, headsets, chairs);
        LinkedList<Process> processes = creator.getProcess();
        LinkedList<Process> finished_processes = new LinkedList<>();
        LinkedList<Thread> threads_running = new LinkedList<>();
        Clock clock=new Clock(Long.parseLong(args[3]));//thread para controlar o tempo que o programa irá rodar
        Thread clock_thread = new Thread(clock);
        Thread creator_thread = new Thread(creator);//thread para criar os "clientes" com um intervalo x entre um e outro, simula uma fila para a entrada no café
        creator_thread.setDaemon(true);//thread que cria processos acaba junta da thread principal
        clock_thread.start();
        creator_thread.start();
        while (creator_thread.isAlive() && processes.isEmpty()) {//aguarda até o primeiro objeto ser criado pela creator_thread
            Thread.sleep(50);
        }
        while ((creator_thread.isAlive() || !processes.isEmpty() || isRunning(threads_running))&& clock_thread.isAlive()) {
            if (processes.isEmpty()) {//aguardo para a criação da fila
                Thread.sleep(2000);
                break;
            }//se a fila estiver em criação fica em loop
            Thread.sleep(50);//aguarda um pouco para não ficar num 'loop' ocioso que gaste todos os recursos do sistema
            int aux = 0;
            while (processes.size() > aux+1) {//enquanto o auxiliar não achar um processo que não esteja em execução ele é incrementado
                if (processes.get(aux).isRunning()) {
                    aux++;
                } else break;
            }
            if (processes.get(aux).isDone() && !processes.get(aux).isRunning()) {//se está pronto remove da fila de processos e adiciona na fila de finalizados
                finished_processes.add(processes.get(aux));
                processes.remove(aux);
            } else {
                if(!processes.get(aux).isRunning()){

                    Process process = creator.getProcess().get(aux);
                    Thread t = new Thread(process);
                    t.start();
                    threads_running.add(t);
                }
            }
        }
        long queue_time_avg=0;
        long execution_time_avg=0;
        int pcs_count=0;
        int headsets_count=0;
        int chairs_count=0;
        System.out.println("Report dos processos:");
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
        System.out.println("Cadeiras usadas: "+chairs_count);
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