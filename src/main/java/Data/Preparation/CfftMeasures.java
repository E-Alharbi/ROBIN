package Data.Preparation;

public class CfftMeasures {

	@Override
	public String toString() {
		return "CfftMeasures [RMSD=" + RMSD + ", Skew=" + Skew + ", Min=" + Min + ", Max=" + Max + "]";
	}
	public double RMSD;
	public double Skew;
	public double Min;
	public double Max;
}
