package de.uni.freiburg.iig.telematik.sewol.parser.petrify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import de.invation.code.toval.parser.ParserException;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.sewol.log.LogEntry;
import de.uni.freiburg.iig.telematik.sewol.log.LogSummary;
import de.uni.freiburg.iig.telematik.sewol.log.LogTrace;
import de.uni.freiburg.iig.telematik.sewol.parser.AbstractLogParser;
import de.uni.freiburg.iig.telematik.sewol.parser.ParsingMode;
import java.io.FileNotFoundException;

public class PetrifyParser extends AbstractLogParser {

        @Override
        public List<List<LogTrace<LogEntry>>> parse(File file, ParsingMode parsingMode) throws IOException, ParserException, ParameterException {
                Validate.noDirectory(file);
                if (!file.canRead()) {
                        throw new ParameterException("Unable to read input file!");
                }

                try {
                        InputStream is = new FileInputStream(file);
                        return parse(is, parsingMode);
                } catch (FileNotFoundException | ParameterException | ParserException e) {
                        throw new ParserException("Exception while parsing: " + e.getMessage());
                }
        }

        @Override
        public List<List<LogTrace<LogEntry>>> parse(String filePath, ParsingMode parsingMode) throws IOException, ParserException {
                Validate.notNull(filePath);
                return parse(new File(filePath), parsingMode);
        }

        @Override
        public List<List<LogTrace<LogEntry>>> parse(InputStream inputStream, ParsingMode parsingMode) throws ParameterException, ParserException {
                try {
                        inputStream.available();
                } catch (IOException e) {
                        throw new ParameterException("Unable to read input file: " + e.getMessage());
                }

                parsedLogFiles = new ArrayList<>();

                List<LogTrace<LogEntry>> traceList = new ArrayList<>();
//		Set<LogTrace<LogEntry>> traceSet = new HashSet<LogTrace<LogEntry>>();
                parsedLogFiles.add(traceList);

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String nextLine = null;
                int traceCount = 0;

                Set<List<String>> activitySequences = new HashSet<>();
                try {
                        while ((nextLine = bufferedReader.readLine()) != null) {
                                LogTrace<LogEntry> newTrace = new LogTrace<>(++traceCount);
                                StringTokenizer tokenizer = new StringTokenizer(nextLine);
                                while (tokenizer.hasMoreTokens()) {
                                        String nextToken = tokenizer.nextToken();
                                        if (nextToken != null && !nextToken.isEmpty()) {
                                                newTrace.addEntry(new LogEntry(nextToken));
                                        }
                                }
                                switch (parsingMode) {
                                        case COMPLETE:
                                                traceList.add(newTrace);
                                                break;
                                        case DISTINCT_ACTIVITY_SEQUENCES:
//			case DISTINCT_TRACES:
                                                if (activitySequences.add(newTrace.getActivities())) {
                                                        newTrace.reduceToActivities();
                                                        traceList.add(newTrace);
                                                }
                                                break;
                                }
                        }
                } catch (IOException ex) {
                        throw new ParserException(ex);
                }
                summaries.add(new LogSummary<>(traceList));
                return parsedLogFiles;
        }
}
