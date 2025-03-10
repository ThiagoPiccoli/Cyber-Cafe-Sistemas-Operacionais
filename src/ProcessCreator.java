public class ProcessCreator implements Runnable {

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                Thread.sleep(10);
                char type=' ';
                int cycles = (int)Math.round((Math.random()*8))+2;
                int intType = (int)Math.round((Math.random()*2));
                if(intType == 0){
                    type = 'G';
                }else if(intType == 1){
                    type = 'F';
                }else if(intType == 2){
                    type = 's';
                }
                Process newProcess = new Process(type,cycles);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        }
    }
}