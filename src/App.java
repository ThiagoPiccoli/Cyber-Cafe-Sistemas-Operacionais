import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
/*TODO Como evitar o starvation ex.: se uma fila for grande o sufiente e o café fechar antes ocorreu starvation?
TODO como checar starvation e deadlock?
*/
public class App extends RuntimeException {
    public static void main(String[] args) throws InterruptedException {
        ProcessCreator creator = new ProcessCreator();
        LinkedList<Process> processes = creator.getProcess();
        Thread thread = new Thread(creator);
        thread.start();
        Semaphore pcs = new Semaphore(10);
        Semaphore headsets = new Semaphore(6);
        Semaphore chairs = new Semaphore(8);
//        if(processes.getFirst().type=='S'){
//
//        }else if(processes.getFirst().type=='F'){}


//
//        if(pcs.tryAcquire() && headsets.tryAcquire()){
//            int aux=0;
//            while(processes.get(aux).type!='G'){
//                aux++;
//            }
//
//        }


        for (int aux = 0; aux < 100; aux++) {
            try {
                Thread.sleep(100);
                // Verifica se já existe um processo no índice 'aux'
                while (aux >= creator.getProcess().size()) {
                    Thread.sleep(50); // Espera até que a lista tenha o índice disponível
                    System.out.println("Aguardando threads!");
                }

                // Obtém o processo quando ele já está na lista
                Process process = creator.getProcess().get(aux);
                process.processPrint();
//                Thread.sleep(1);

            } catch (IndexOutOfBoundsException e) {
                // Captura erro (se ocorrer) e espera antes de tentar novamente
                System.out.println("Índice não disponível, aguardando...");
                Thread.sleep(100);
            }
        }
    }
}