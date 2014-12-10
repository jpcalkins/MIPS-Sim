public class Registers{
	
	public int[] registers = new int[32];
	public int pc = 0;
	public int nPC = 4;
	public int lo;
	public int hi;

	public Registers(){
		this.registers = registers;
		for(int i = 0; i< registers.length; i++){
			registers[i] = 0;
		}
		this.pc = pc;
		this.nPC = nPC;
		this.lo = lo;
		this.hi = hi;
	}
	public void printRegisters(){
		for (int i=0; i<registers.length; i++){
			if(registers[i] != 0){
				System.out.println("Register " + i + ": " + Integer.toHexString(registers[i]));
			}
		}
		System.out.println("PC: " + Integer.toHexString(pc));
		System.out.println("NPC: " + Integer.toHexString(nPC));
		System.out.println("LO: " + lo);
		System.out.println("HI: " + hi);
	}
	public void setRegister(int index, int value){
		registers[index] = value;
	}
	public int getRegister(int index){
		return registers[index];
	}
	public void setPC(int value){
		pc = value;
	}
	public int getPC(){
		return pc;
	}
	public void setNPC(int value){
		nPC = value;
	}
	public int getNPC(){
		return nPC;
	}
	public void setLO(int value){
		lo = value;
	}
	public int getLO(){
		return lo;
	}
	public void setHI(int value){
		hi = value;
	}
	public int getHI(){
		return hi;
	}
	public void advancePC(int offset){
		pc = nPC;
		nPC += offset;
	}
}