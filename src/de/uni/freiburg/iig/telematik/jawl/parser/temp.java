package de.uni.freiburg.iig.telematik.jawl.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.invation.code.toval.parser.ParserException;
import de.invation.code.toval.parser.ParserException.ErrorCode;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.jawl.log.LogEntry;
import de.uni.freiburg.iig.telematik.jawl.log.LogTrace;
import de.uni.freiburg.iig.telematik.jawl.parser.petrify.PetrifyParser;
import de.uni.freiburg.iig.telematik.jawl.parser.plain.PlainParser;
import de.uni.freiburg.iig.telematik.jawl.parser.xes.XESLogParser;

public class temp {
	
	public static List<List<LogTrace<LogEntry>>> parse(File file) throws IOException, ParserException, ParameterException {
		return parse(file, false);
	}

	public static List<List<LogTrace<LogEntry>>> parse(File file, boolean onlyDistinctTraces) throws IOException, ParserException, ParameterException {
		validateFile(file);
		LogParsingFormat format = guessFormat(file);
		if(format == null)
			throw new ParserException(ErrorCode.UNKNOWN_FILE_EXTENSION);
		LogParserInterface parser = getParser(file, format);
		return parser.parse(file, onlyDistinctTraces);
	}
	
	public static List<List<LogTrace<LogEntry>>> parse(String fileName) throws IOException, ParserException, ParameterException {
		return parse(fileName, false);
	}
	
	public static List<List<LogTrace<LogEntry>>> parse(String fileName, boolean onlyDistinctTraces) throws IOException, ParserException, ParameterException {
		Validate.notNull(fileName);
		return parse(prepareFile(fileName), onlyDistinctTraces);
	}
	
	public static List<List<LogTrace<LogEntry>>> parse(File file, LogParsingFormat format) throws IOException, ParserException, ParameterException {
		return parse(file, format, false);
	}
	
	public static List<List<LogTrace<LogEntry>>> parse(File file, LogParsingFormat format, boolean onlyDistinctTraces) throws IOException, ParserException, ParameterException {
		validateFile(file);
		Validate.notNull(format);
		LogParserInterface parser = getParser(file, format);
		return parser.parse(file, onlyDistinctTraces);
	}
	
	public static List<List<LogTrace<LogEntry>>> parse(String fileName, LogParsingFormat format) throws IOException, ParserException, ParameterException {
		Validate.notNull(fileName);
		return parse(prepareFile(fileName), format);
	}
	
	public static synchronized LogParserInterface getParser(File file, LogParsingFormat format) throws ParserException {
		switch(format){
		case XES: return new XESLogParser();
		case PETRIFY: return new PetrifyParser();
		case PLAIN_SPACE: return new PlainParser("\\s");
		case PLAIN_TAB: return new PlainParser("\\t");
		default: break;
		}
		throw new ParserException(ErrorCode.UNSUPPORTED_FORMAT);
	}
	
	private static File prepareFile(String fileName) throws IOException{
		File file = new File(fileName);
		validateFile(file);
		return file;
	}
	
	private static void validateFile(File file) throws IOException{
		if(!file.exists())
			throw new IOException("I/O Error on opening file: File does not exist!");
		if(file.isDirectory())
			throw new IOException("I/O Error on opening file: File is a directory!");
		if(!file.canRead())
			throw new IOException("I/O Error on opening file: Unable to read file!");
	}
	
	public static LogParsingFormat guessFormat(File file){
		for(LogParsingFormat format: LogParsingFormat.values()){
			if(file.getName().endsWith(format.getFileFormat().getFileExtension())){
				return format;
			}
		}
		return null;
	}

}