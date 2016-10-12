package instructions;

import machine.MachineState;
import machine.SREG;

import org.apache.log4j.Logger;

/**
 * LSL - Logical Shift Left
 * Rd <- Rd << 1 | C <- MSB(Rd) | PC <- PC + 1
 * @author Michael Miller
 *
 */
public class InstrLSL extends Instr {
	
	private static final Logger logger = Logger.getLogger(InstrLSL.class);
	private int msb = 0x0080;
	private int hAdd = 0x0008;
	private int rd;
	
	public InstrLSL(MachineState machine, int rd) throws MalformedInstruction {
		super(machine);
		if(rd < 0 || rd > 31)
		{
			throw new MalformedInstruction("Invalid register number rd(" + rd + ")");
		}
		this.rd = rd;
	}

	@Override
	public String toString() {
		return "LSL rd(" + this.rd + ")";
	}

	@Override
	public void execute() {
		logger.debug("Computing the LSL of rd(" + rd + ").");
		int oldVal = this.machine.getRegister(rd);
		int newVal = oldVal << 1;
		
		this.event.setRd(rd, newVal);
		SREG newSreg = this.machine.getSREG();
		newSreg.setH((oldVal & hAdd) == hAdd);
		newSreg.setN((newVal & msb) == msb);
		newSreg.setZ(newVal == 0);
		newSreg.setC((oldVal & msb) == msb);
		newSreg.setS(newSreg.isN() ^ newSreg.isV());
		newSreg.setV(newSreg.isN() ^ newSreg.isC());
		
		this.event.setSREG(newSreg);
		this.event.setPC(this.machine.getPC() + 1);
	}
}