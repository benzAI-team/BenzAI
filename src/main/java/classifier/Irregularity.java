package classifier;

import java.util.Locale;

public class Irregularity {

	private final int[] groups;
	private final double XI;

	public Irregularity(int[] groups, double XI) {
		this.groups = groups;
		this.XI = XI;
	}

	public int getGroup(int group) {
		return groups[group];
	}

	public double getXI() {
		return XI;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < groups.length; i++) {
			builder.append("N_").append(i + 1).append(" = ").append(groups[i]).append("\n");
		}
    
    if (XI == -1.0) {
      builder.append("XI = n/a");
    }
    else {
		Locale locale = new Locale( "en", "US" );
	    builder.append("XI = ").append(String.format(locale,"%1.2f",XI));
    }

		return builder.toString();
	}
}
