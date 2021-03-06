package de.uni.freiburg.iig.telematik.sewol.writer;

import java.io.IOException;
import java.nio.charset.Charset;

import de.invation.code.toval.file.EOLType;
import de.invation.code.toval.file.FileWriter;
import de.invation.code.toval.validate.CompatibilityException;
import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.sewol.format.AbstractLogFormat;
import de.uni.freiburg.iig.telematik.sewol.format.LogPerspective;
import de.uni.freiburg.iig.telematik.sewol.log.LogEntry;
import de.uni.freiburg.iig.telematik.sewol.log.LogTrace;
import de.uni.freiburg.iig.telematik.sewol.writer.PerspectiveException.PerspectiveError;


/**
 * LogWriter provides functionality for writing trace-oriented log files.
 * Deletes output file if it already exists.
 *
 */
public class LogWriter extends FileWriter{
	
	public static final String DEFAULT_LOG_PATH = "";
	public static final String DEFAULT_LOG_FILENAME = "LOG";
	
	protected AbstractLogFormat logFormat;
	protected LogPerspective logPerspective = LogPerspective.TRACE_PERSPECTIVE;
	private boolean headerWritten = false;
	
        private String comment = null;
	
	//------- Constructors -------------------------------------------------------------------
	
	/**
	 * Creates a new log writer.<br>
	 * The constructor uses default values for:<br>
	 * file name<br>
	 * path<br>
	 * charset<br>
	 * eol-string<br>
	 * @see #DEFAULT_FILE_NAME
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_CHARSET
	 * @see #DEFAULT_EOL_STRING
	 * @param logFormat
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 * @throws ParameterException if some parameters are <code>null</code> or file name is an empty string.
	 * @throws IOException if output file creation or header writing cause an exception.
	 */
	public LogWriter(AbstractLogFormat logFormat) 
			throws PerspectiveException, CompatibilityException, ParameterException, IOException{
		super();
		initialize(logFormat);
	}

	/**
	 * Creates a new log writer.<br>
	 * The constructor uses default values for:<br>
	 * file name<br>
	 * path<br>
	 * eol-string<br>
	 * @see #DEFAULT_FILE_NAME
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_EOL_STRING
	 * @param logFormat
	 * @param charset
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 * @throws ParameterException if some parameters are <code>null</code> or file name is an empty string.
	 * @throws IOException if output file creation or header writing cause an exception.
	 */
	public LogWriter(AbstractLogFormat logFormat, Charset charset) 
			throws PerspectiveException, CompatibilityException, ParameterException, IOException {
		super(charset);
		initialize(logFormat);
	}

	/**
	 * Creates a new log writer.<br>
	 * The constructor uses default values for:<br>
	 * path<br>
	 * eol-string<br>
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_EOL_STRING
	 * @param logFormat
	 * @param fileName
	 * @param charset
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 * @throws ParameterException if some parameters are <code>null</code> or file name is an empty string.
	 * @throws IOException if output file creation or header writing cause an exception.
	 */
	public LogWriter(AbstractLogFormat logFormat, String fileName, Charset charset)
			throws PerspectiveException, CompatibilityException, ParameterException, IOException {
		super(fileName, charset);
		initialize(logFormat);
	}

	/**
	 * Creates a new log writer.<br>
	 * The constructor uses default values for:<br>
	 * eol-string<br>
	 * @see #DEFAULT_EOL_STRING
	 * @param logFormat
	 * @param path
	 * @param fileName
	 * @param charset
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 * @throws ParameterException if some parameters are <code>null</code> or file name is an empty string.
	 * @throws IOException if output file creation or header writing cause an exception.
	 */
	public LogWriter(AbstractLogFormat logFormat, String path, String fileName, Charset charset)
			throws PerspectiveException, CompatibilityException, ParameterException, IOException {
		super(path, fileName, charset);
		initialize(logFormat);
	}

	/**
	 * Creates a new log writer.<br>
	 * The constructor uses default values for:<br>
	 * charset<br>
	 * eol-string<br>
	 * @see #DEFAULT_CHARSET
	 * @see #DEFAULT_EOL_STRING
	 * @param logFormat
	 * @param path
	 * @param fileName
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 * @throws ParameterException if some parameters are <code>null</code> or file name is an empty string.
	 * @throws IOException if output file creation or header writing cause an exception.
	 */
	public LogWriter(AbstractLogFormat logFormat, String path, String fileName)
			throws PerspectiveException, CompatibilityException, ParameterException, IOException {
		super(path, fileName);
		initialize(logFormat);
	}

	/**
	 * Creates a new log writer.<br>
	 * The constructor uses default values for:<br>
	 * path<br>
	 * charset<br>
	 * eol-string<br>
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_CHARSET
	 * @see #DEFAULT_EOL_STRING
	 * @param logFormat
	 * @param fileName
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 * @throws ParameterException if some parameters are <code>null</code> or file name is an empty string.
	 * @throws IOException if output file creation or header writing cause an exception.
	 */
	public LogWriter(AbstractLogFormat logFormat, String fileName) 
			throws PerspectiveException, CompatibilityException, ParameterException, IOException {
		super(fileName);
		initialize(logFormat);
	}


	//------- Getters and Setters ------------------------------------------------------------

	@Override
	public String getFileExtension(){
		return logFormat.getFileExtension();
	}

	@Override
	public String getDefaultFileName() {
		return DEFAULT_LOG_FILENAME;
	}

	@Override
	public String getDefaultPath() {
		return DEFAULT_LOG_PATH;
	}

	public AbstractLogFormat getLogFormat(){
		return logFormat;
	}

        public String getComment() {
                return comment;
        }

	/**
	 * Sets the log format for writing process traces (e.g. MXML).
	 * @param logFormat The log format to set.
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 */
	private void setLogFormat(AbstractLogFormat logFormat) throws PerspectiveException, CompatibilityException{
		if(!logFormat.supportsCharset(charset))
			throw new CompatibilityException("Log format \""+logFormat.getName()+"\" does not support charset \""+charset.name()+"\"");
		if(!logFormat.supportsLogPerspective(logPerspective))
			throw new PerspectiveException(PerspectiveError.INCOMPATIBLE_LOGFORMAT);
		this.logFormat = logFormat;
	}

        /**
         * Sets a comment to add to the log.
         * @param comment 
         */
        public void setComment(String comment) {
                this.comment = comment;
        }

	//------- Methods for setting up the log writer ------------------------------------------

	/**
	 * Initializes the log writer on basis of the given log format.<br>
	 * It ensures the proper creation of the output file and writes the file header.
	 * @param logFormat
	 * @throws IOException if output file creation or header writing cause an exception.
	 * @throws CompatibilityException if the charset of the log writer is not supported by the log format.
	 * @throws PerspectiveException if the log format does not support the writers' log perspective.
	 */
	protected final void initialize(AbstractLogFormat logFormat) throws PerspectiveException, IOException, CompatibilityException {
		setLogFormat(logFormat);
		try {
			setEOLString(EOLType.LF);
		} catch (ParameterException e) {
			// Is only thrown if setEOLString() is called with a null-parameter.
			// Cannot happen, since EOLType.LF is not null
			throw new RuntimeException(e);
		}
	}
	
	
	//------- Functionality ------------------------------------------------------------------
	
	/**
	 * This method is only allowed in the trace perspective.
         * @param <E>
	 * @param logTrace The log trace to write.
         * @throws PerspectiveException
	 * @throws IOException 
	 */
	public <E extends LogEntry> void writeTrace(LogTrace<E> logTrace) throws PerspectiveException, IOException{
		if(logPerspective == LogPerspective.ACTIVITY_PERSPECTIVE)
			throw new PerspectiveException(PerspectiveError.WRITE_TRACE_IN_ACTIVITY_PERSPECTIVE);
		
		prepare();
		if(!headerWritten){
			write(logFormat.getFileHeader());
			headerWritten = true;
		}
		output.write(logFormat.getTraceAsString(logTrace));
	}
	
	/**
	 * This method is only allowed in the activity perspective.
	 * @param logEntry The log entry to write.
         * @param caseNumber
         * @throws PerspectiveException
	 * @throws IOException 
	 */
	public void writeEntry(LogEntry logEntry, int caseNumber) throws PerspectiveException, IOException{
		if(logPerspective == LogPerspective.TRACE_PERSPECTIVE)
			throw new PerspectiveException(PerspectiveError.WRITE_ACTIVITY_IN_TRACE_PERSPECTIVE);
		
		prepare();
		if(!headerWritten){
			write(logFormat.getFileHeader());
			headerWritten = true;
		}
		output.write(logFormat.getEntryAsString(logEntry, caseNumber));
	}
	
	@Override
	public void closeFile() throws IOException {
                if (comment != null) {
                        output.write(logFormat.formatComment(comment));
                }
                if (output != null) {
                        output.write(logFormat.getFileFooter());
                        super.closeFile();
                }
        }
	
//	
//	//------- Test
//	
//	@SuppressWarnings("rawtypes")
//	public static void main(String[] args) throws Exception{
//		LogEntry en1 = new LogEntry("A");
//		LogEntry en2 = new LogEntry("B");
//		LogEntry en3 = new LogEntry("C");
//                en3.addMetaAttribute(new DataAttribute("key", "val"));
//		LogTrace tr1 = new LogTrace(1);
//		tr1.addEntry(en1);
//		tr1.addEntry(en2);
//		tr1.addEntry(en3);
//		LogWriter writer = new LogWriter(new MXMLLogFormat("bla"), "/home/alange/WriterTest");
//		writer.writeTrace(tr1);
//		writer.closeFile();
//	}

}
