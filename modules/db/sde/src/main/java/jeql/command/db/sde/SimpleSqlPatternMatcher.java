package jeql.command.db.sde;

public class SimpleSqlPatternMatcher 
{
	public static final int MATCH_EXACT = 0;
	public static final int MATCH_PREFIX = 1;
	public static final int MATCH_INFIX = 2;
	public static final int MATCH_SUFFIX = 3;
	public static final int MATCH_ANY = 4;
	
	private String pattern;
	private String fixedData;
	private String fixedDataLower = null;
	private int matchStrategy = MATCH_EXACT;
	
	public SimpleSqlPatternMatcher(String pattern)
	{
		this.pattern = pattern;
		prepare(pattern);
	}
	
	private void prepare(String pattern)
	{
		if (pattern.equals("%")) {
			matchStrategy = MATCH_ANY;
			return;
		}
		boolean hasPrefixWildCard = pattern.startsWith("%");
		boolean hasSuffixWildCard = pattern.endsWith("%");
		
		if (hasPrefixWildCard && hasSuffixWildCard && pattern.length() >= 2) {
			fixedData = pattern.substring(1, pattern.length() - 1);
			matchStrategy = MATCH_INFIX;
			return;
		}
		if (hasPrefixWildCard) {
			fixedData = pattern.substring(1, pattern.length());
			matchStrategy = MATCH_SUFFIX;
			return;
		}
		if (hasSuffixWildCard) {
			fixedData = pattern.substring(0, pattern.length() - 1);
			matchStrategy = MATCH_PREFIX;
			return;
		}
		fixedData = pattern;
		matchStrategy = MATCH_EXACT;
	}
	
	public boolean match(String input)
	{
		return match(input, fixedData);
	}
	
	public boolean matchIgnoreCase(String input)
	{
		if (fixedDataLower == null)
			fixedDataLower = fixedData.toLowerCase();
		String inputLower = input.toLowerCase();
		return match(inputLower, fixedDataLower);
	}
	
	private boolean match(String input, String fixedData)
	{
		switch (matchStrategy) {
		case MATCH_ANY:
			return true;
		case MATCH_EXACT:
			return fixedData.equals(input);
		case MATCH_PREFIX:
			return input.startsWith(fixedData);
		case MATCH_SUFFIX:
			return input.endsWith(fixedData);
		case MATCH_INFIX:
			return input.indexOf(fixedData) >= 0;
		}
		throw new IllegalStateException("Unknown match state");

	}
}
