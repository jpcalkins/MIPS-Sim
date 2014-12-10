//Homework 6 due Fri.

import java.util.*;
import java.io.*;

public class MIPS_Sim{

	public static Memory memory = new Memory();
	public static Registers register = new Registers();

	public static void main (String[] args) throws FileNotFoundException{
		System.out.println("MIPS Simulator, written by Jacob Calkins.");
		System.out.println("Would you like to run program in debug mode? Enter yes or no");
		Scanner input = new Scanner(System.in);
		String userChoice = input.next();
		if(userChoice.toUpperCase().equals("YES")){
			memory.setFlag(true);
			System.out.println("The PC and instruction will now print every time. You can enter reg to see the values in the registers, you can enter mem to see a value in memory, or just press enter to proceed to the next iteration.");
		} else if(userChoice.toUpperCase().equals("NO")){
			memory.setFlag(false);
		} else{
			System.out.println("Sorry that was an incorrect input. Please enter yes or no.");
			userChoice = input.next();
			if(userChoice.toUpperCase().equals("YES")){
			memory.setFlag(true);
			} else if(userChoice.toUpperCase().equals("NO")){
				memory.setFlag(false);
			}
		}

		Scanner scan = new Scanner(new File(args[0]));
		while(scan.hasNext()){
			String line = scan.nextLine();
			parseLine(line);
		}
		memory.run(register);
	}
	public static void parseLine(String line){

		String[] stuff = line.split("[ \t\n\r]+");
		
		if(line.length()==0) return;

		for(int i=0; i<stuff.length; i++){
			if(stuff[i].charAt(0) == '['){
				String directive = stuff[i].substring(1, stuff[i].length()-1);
				if(directive.charAt(0) == 'P'){
					register.setPC((int)Long.parseLong(stuff[i+1].substring(2), 16));
				} else if(directive.charAt(0) == 'R'){
					register.setRegister(Integer.parseInt(directive.substring(1)), (int)Long.parseLong(stuff[i+1].substring(2), 16));
				} else if(directive.charAt(0) == '0'){
					memory.setMem((int)Long.parseLong(directive.substring(2), 16)/4, (int)Long.parseLong(stuff[i+1].substring(2), 16));
					if(stuff[i+2].charAt(0) == '0'){
						int temp = 1;
						for(int j=i+2; j<stuff.length; j++){
							if(stuff[j].charAt(0) != '[' && stuff[j].charAt(0) == '0'){
								memory.setMem((int)Long.parseLong(directive.substring(2), 16)/4+temp, (int)Long.parseLong(stuff[j].substring(2), 16));
								temp++;
							}
							if(stuff[j] == stuff[stuff.length-1]){
								return;
							}
						}
					}
				}
			}
			else break;
		}
	}
}