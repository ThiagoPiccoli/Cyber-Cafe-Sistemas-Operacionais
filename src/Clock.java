public class Clock implements Runnable{
    long start_time;
    long end_time;
    Clock(long seconds){
        this.start_time= System.nanoTime();
        this.end_time = System.nanoTime()+(seconds*1000000000);
    }
    @Override
    public void run(){
        while(System.nanoTime()<end_time){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
