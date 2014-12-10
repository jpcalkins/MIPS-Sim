import java.util.*;
public class Memory{

	public int[] memory = new int[(int)Math.pow(2.0, 20.0)];
	public Boolean debugFlag;
	Scanner input = new Scanner(System.in);

	public Memory(){
		this.memory = memory;
		for(int i=0; i<memory.length; i++){
			memory[i] = 0;
		}
		this.debugFlag = false;
	}
	public void setMem(int index, int value){
		memory[index] = value;
	}
	public void setFlag(Boolean input){
		debugFlag = input;
	}
	public int getVal(int index){
		return memory[index];
	}
	public void printMem(){
		for(int i = 0; i<memory.length; i++){
			if(memory[i] != 0){
				System.out.println("Location: " + Integer.toHexString(i*4) + " Data: " + Integer.toHexString(memory[i]));
			}
		}
	}
	public int length(){
		return memory.length;
	}
	public void run(Registers register){
		try{
			int inst, temp, j, i, d, s, t, c, shamt, seImm;
			inst = memory[register.getPC()/4];
			while(register.getPC()<memory.length){
				temp = inst;
				j = temp & 0x3FFFFFF;
				i = temp & 0xFFFF;
				temp = temp >> 6;
				shamt = temp & 0x1F;
				temp = temp >> 5;
				d = temp & 0x1F;
				temp = temp >> 5;
				t = temp & 0x1F;
				temp = temp >> 5;
				s = temp & 0x1F;

				seImm = extendImm(i);

				if(debugFlag == true) debug(register, inst);

				if((inst & 0xFC0007FF) == 0x20){ //add
					register.setRegister(d, register.getRegister(s)+register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x20000000){ //addi
					register.setRegister(t, register.getRegister(s)+seImm);
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x24000000){ //addiu
					register.setRegister(t, register.getRegister(s)+seImm);
					register.advancePC(4);
				} else if((inst & 0xFC0007FF) == 0x21){ //addu
					register.setRegister(d, register.getRegister(s)+register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC0007FF) == 0x24){ //and
					register.setRegister(d, register.getRegister(s)&register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x30000000){ //andi
					register.setRegister(t, register.getRegister(s)&i);
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x10000000){ //beq
					if(register.getRegister(s) == register.getRegister(t)){
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC1F0000) == 0x4010000){ //bgez
					if(register.getRegister(s) >= 0){
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC1F0000) == 0x4110000){ //bgezal
					if(register.getRegister(s) >= 0){
						register.setRegister(31, register.getPC()+8);
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC1F0000) == 0x1C000000){ //bgtz
					if(register.getRegister(s) > 0){
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC1F0000) == 0x18000000){ //blez
					if(register.getRegister(s) <= 0){
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC1F0000) == 0x4000000){ //bltz
					if(register.getRegister(s) < 0){
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC1F0000) == 0x4100000){ //bltzal
					if(register.getRegister(s) < 0){
						register.setRegister(31, register.getPC()+8);
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x14000000){ //bne
					if(register.getRegister(s) != register.getRegister(t)){
						register.advancePC(seImm<<2);
					} else register.advancePC(4);
				} else if((inst & 0xFC00FFFF) == 0x1A){ //div
					register.setLO(register.getRegister(s)/register.getRegister(t));
					register.setHI(register.getRegister(s)%register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC00FFFF) == 0x1B){ //divu
					register.setLO(register.getRegister(s)/register.getRegister(t));
					register.setHI(register.getRegister(s)%register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x8000000){ //jump
					register.setPC(register.getNPC());
					register.setNPC((register.getPC() & 0xF0000000) | (i<<2));
				} else if((inst & 0xFC000000) == 0xC000000){ //jal
					register.setRegister(31, register.getPC()+8);
					register.setPC(register.getNPC());
					register.setNPC((register.getPC() & 0xF0000000) | (i<<2));
				} else if((inst & 0xFC1FFFFF) == 0x8){ //jr
					register.setPC(register.getNPC());
					register.setNPC(register.getRegister(s));
				} else if((inst & 0xFC000000) == 0x80000000){ //lb
					register.setRegister(t, memory[s+i]);
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x3C000000){ //lui
					register.setRegister(t, i<<16);
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x8C000000){ //lw
					register.setRegister(t, memory[s+i]);
					register.advancePC(4);
				} else if((inst & 0xFFFF07FF) == 0x10){ //mfhi
					register.setRegister(d, register.getHI());
					register.advancePC(4);
				} else if((inst & 0xFFFF07FF) == 0x12){ //mflo
					register.setRegister(d, register.getLO());
					register.advancePC(4);
				} else if((inst & 0xFC00FFFF) == 0x18){ //mult
					register.setLO(register.getRegister(s)*register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC00FFFF) == 0x19){ //multu
					register.setLO(register.getRegister(s)*register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC0007FF) == 0x25){ //or
					register.setRegister(d, register.getRegister(s)|register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x34000000){ //ori
					register.setRegister(t, register.getRegister(s)|i);
					register.advancePC(4);
				}  else if((inst & 0xFC000000) == 0xA0000000){ //sb
					memory[s+i] = 0xFF & register.getRegister(t);
					register.advancePC(4);
				} else if((inst & 0xFC00003F) == 0x00000000){ //sll
					register.setRegister(d, register.getRegister(t) << shamt);
					register.advancePC(4);
				} else if((inst & 0xFC00003F) == 0x4){ //sllv
					register.setRegister(d, register.getRegister(t) << s);
					register.advancePC(4);
				} else if((inst & 0xFC0007FF) == 0x2A){ //slt
					if(register.getRegister(s) < register.getRegister(t)){
						register.setRegister(d, 1);
						register.advancePC(4);
					} else{
						register.setRegister(d, 0);
						register.advancePC(4);
					}
				} else if((inst & 0xFC000000) == 0x28000000){ //slti
					if(register.getRegister(s) < i){
						register.setRegister(t, 1);
						register.advancePC(4);
					} else {
						register.setRegister(d, 0);
						register.advancePC(4);
					}
				} else if((inst & 0xFC000000) == 0x2C000000){ //sltiu
					if(register.getRegister(s) < i){
						register.setRegister(t, 1);
						register.advancePC(4);
					} else {
						register.setRegister(d, 0);
						register.advancePC(4);
					}
				} else if((inst & 0xFC0007FF) == 0x2B){ //sltu
					if(register.getRegister(s) < register.getRegister(t)){
						register.setRegister(d, 1);
						register.advancePC(4);
					} else {
						register.setRegister(d, 0);
						register.advancePC(4);
					}
				} else if((inst & 0xFC00003F) == 0x3){ //sra
					register.setRegister(d, seImm);
					register.advancePC(4);
				} else if((inst & 0xFC00003F) == 0x3){ //srl
					register.setRegister(d, register.getRegister(t)>>shamt);
					register.advancePC(4);
				} else if((inst & 0xFC0007FF) == 0x6){ //srlv
					register.setRegister(d, register.getRegister(t)>>register.getRegister(s));
					register.advancePC(4);
				} else if((inst & 0xFC0007FF) == 0x22){ //sub
					register.setRegister(d, register.getRegister(s)-register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC0007FF) == 0x23){ //subu
					register.setRegister(d, register.getRegister(s)-register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0xAC000000){ //sw
					memory[(register.getRegister(s)+seImm)/4] = register.getRegister(t);
					register.advancePC(4);
				} else if((inst & 0xFC00003F) == 0xC){ //syscall
					if(register.getRegister(2) == 1){
						System.out.print(register.getRegister(4));
					} else if(register.getRegister(2) == 4){
						int tem = register.getRegister(4);
						while(true){
							int word = memory[tem/4];
							int b = word>>((tem%4)*8) & 0xFF;
							if(b == 0){
								break;
							} else System.out.print((char)b);
							tem++;
						}
					} else if(register.getRegister(2) == 5){
						input = new Scanner(System.in);
						int numIn = input.nextInt();
						register.setRegister(2, numIn);
					} else if(register.getRegister(2) == 8){
						input = new Scanner(System.in);
						String strIn = input.nextLine();
					} else if(register.getRegister(2) == 9){
						int bytes = register.getRegister(4);
						Boolean fits = true;
						try{
							for(int zz = 0; zz<memory.length; zz++){
								if(memory[zz] == 0){
									for(int yy=zz; yy<=bytes; yy++){
										if(memory[yy] != 0){
											fits = false;
											zz+=yy;
											break;
										}			
									}
									if(fits == true){
										register.setRegister(2, zz);
										break;
									}
								}
							}
						} catch(IndexOutOfBoundsException e){
							System.out.println("Not enough available memory.");
						}
					} else if(register.getRegister(2) == 10){
						break;
					}
					register.advancePC(4);
				} else if((inst & 0xFC00003F) == 0x26){ //xor
					register.setRegister(d, register.getRegister(s)^register.getRegister(t));
					register.advancePC(4);
				} else if((inst & 0xFC000000) == 0x38000000){ //xori
					register.setRegister(t, register.getRegister(s)^i);
					register.advancePC(4);
				}
				inst = memory[register.getPC()/4];
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.exit(0);
		}
	}
	public void debug(Registers register, int instruction){
		input = new Scanner(System.in);
		System.out.println("PC: " + Integer.toHexString(register.getPC()) + " Instruction: " + Integer.toHexString(instruction));
		String command = input.nextLine();
		while(true){
			if(command.equals("")){
				return;
			} else if(command.toUpperCase().equals("REG")){
				register.printRegisters();
			} else if(command.toUpperCase().equals("MEM")){
				System.out.println("What memory location would you like to access?");
				int temp = input.nextInt();
				System.out.println("Memory location " + temp + " is: " + Integer.toHexString(memory[temp]));
			} else{
				System.out.println("Sorry I didn't catch that please enter reg, mem, or just press enter to proceed.");
			}
			command = input.nextLine();
		}
	}
	public int extendImm(int imm){
		if((imm & 0x8000) == 0){
			return imm;
		} else return (imm|0xFFFF0000);
	}
}