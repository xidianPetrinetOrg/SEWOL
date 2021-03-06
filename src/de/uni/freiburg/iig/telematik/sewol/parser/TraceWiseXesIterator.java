package de.uni.freiburg.iig.telematik.sewol.parser;

import java.io.IOException;
import java.util.Iterator;

import de.invation.code.toval.file.FileReader;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

/**
 * <p>
 * The TraceWiseXesIterator is an {@link Iterator} implementation that splits large XES files into smaller ones. For not keeping the whole document in memory, the source file gets streamed.
 * </p>
 * <p>
 * The fragment size has a direct impact on the memory usage and the performance of the following parsing process. By choosing a large fragment size, the parser might need a lot of memory and might need to write out some shadow maps for swapping (see NikeFS of OpenXES). For a low fragment size one must keep in mind that the parser reloads the extensions regularly, what slows down the parsing process enormously and also fills up the shadow maps. For many
 * traces with few entries a fragment size of more than 10000 can be sufficient, where a fragment size of 1000 can bring a good performance with less very large traces. The default value of a fragment size of 5000 should bring a good tradeoff of memory usage and performance.
 * </p>
 * 
 * @version 1.0
 * @author Thomas Stocker
 * @author Adrian Lange
 */
public class TraceWiseXesIterator implements Iterator<LogFragment> {

	/** Specifies the default number of traces for the iterator */
	public static final int DEFAULT_FRAGMENT_SIZE = 5000;

	private final String LOG_END = "</log>";

	private FileReader fileReader = null;
	private String header = null;
	private Boolean hasNextTrace = null;
	private String lastTraceStart = null;
	private int fragmentSize = 1;

	/**
	 * Creates a new TraceWiseXesIterator with the default fragment size.
	 * 
	 * @param logFile
	 *            Path to the log file to read
         * @throws IOException
	 */
	public TraceWiseXesIterator(String logFile) throws ParameterException, IOException {
		this(logFile, DEFAULT_FRAGMENT_SIZE);
	}

	/**
	 * Creates a new TraceWiseXesIterator with the specified fragment size.
	 * 
	 * @param logFile
	 *            Path to the log file to read
	 * @param fragmentSize
	 *            The number of traces for the iterator
         * @throws IOException
	 */
	public TraceWiseXesIterator(String logFile, int fragmentSize) throws ParameterException, IOException {
		Validate.exists(logFile);
		Validate.positive(fragmentSize);
		fileReader = new FileReader(logFile);
		header = nextStringFragment();
		this.fragmentSize = fragmentSize;
	}

	private String nextStringFragment() throws IOException {
		if (hasNextTrace != null && !hasNextTrace)
			return null;

		StringBuilder buffer = new StringBuilder();
		if (lastTraceStart != null) {
			buffer.append(lastTraceStart);
			buffer.append(System.getProperty("line.separator"));
		}
		String nextLine = null;
		while ((nextLine = fileReader.readLine()) != null && !headerEnd(nextLine)) {
			buffer.append(nextLine);
			buffer.append(System.getProperty("line.separator"));
		}
		if (nextLine != null && !nextLine.trim().startsWith("</log")) {
			lastTraceStart = nextLine;
			hasNextTrace = true;
		} else {
			hasNextTrace = false;
		}
		return buffer.toString();
	}

	private boolean headerEnd(String line) {
		String cleanedLine = line.trim();
		return cleanedLine.startsWith("<trace") || cleanedLine.startsWith("</log");
	}

	@Override
	public boolean hasNext() {
		return hasNextTrace;
	}

	@Override
	public LogFragment next() {
		if (hasNext()) {
			try {
				LogFragment newFragment = new LogFragment();
				newFragment.addLine(header);
				int traceCount = 0;
				while (hasNext() && traceCount++ < fragmentSize) {
					newFragment.addLine(nextStringFragment());
				}
				newFragment.addLine(LOG_END);
				newFragment.close();
				return newFragment;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
