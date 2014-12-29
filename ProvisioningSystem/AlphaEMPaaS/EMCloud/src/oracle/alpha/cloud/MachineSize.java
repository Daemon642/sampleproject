package oracle.alpha.cloud;

public class MachineSize {
    private String name;
    private int memory;
    private int cores;
    private int capacity;
    private int coresAdder;
    private int memoryAdder;
    private int capacityAdder;
    
    public MachineSize() {
        super();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getMemory() {
        return memory;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getCores() {
        return cores;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCoresAdder(int coresAdder) {
        this.coresAdder = coresAdder;
    }

    public int getCoresAdder() {
        return coresAdder;
    }

    public void setMemoryAdder(int memoryAdder) {
        this.memoryAdder = memoryAdder;
    }

    public int getMemoryAdder() {
        return memoryAdder;
    }

    public void setCapacityAdder(int capacityAdder) {
        this.capacityAdder = capacityAdder;
    }

    public int getCapacityAdder() {
        return capacityAdder;
    }
    
    @Override
    public String toString () {
        return ("MachineSize = Name:" + this.getName() + "  Memory: " + this.getMemory() + "  Cores: " + this.getCores() + "  Capacity: " + this.getCapacity() + "  CoresAdder: " + this.getCoresAdder() + "  MemoryAdder: " + this.getMemoryAdder() + "  CapacityAdder: " + this.getCapacityAdder());
    }
}
