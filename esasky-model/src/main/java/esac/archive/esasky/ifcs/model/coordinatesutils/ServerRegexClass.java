package esac.archive.esasky.ifcs.model.coordinatesutils;

import java.util.regex.Pattern;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.RegexClass;

public class ServerRegexClass implements RegexClass{
	
	public boolean test(String pattern, String stringToTest) {
		Pattern javaPattern = Pattern.compile(pattern);
		return javaPattern.matcher(stringToTest).find();
	}
}
