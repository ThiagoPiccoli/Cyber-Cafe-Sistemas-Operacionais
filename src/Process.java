public class Process {
    char type;//G for gamer, F for freelancer, S for student
    boolean isFirstDone;
    boolean isDone;
    int cycles;
    int queueTime;
    int totalTime;
    Process(char type, int cycles) {
        this.type = type;
        this.cycles = cycles;
    }
    public void processPrint() {
        System.out.println("Type: "+type);
        System.out.println("Cycles: "+cycles);

    }
}
