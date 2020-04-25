package pers.cheng.dij;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pers.cheng.dij.core.wrapper.ReproductionResult;
import pers.cheng.dij.runner.DijSession;

public class DijApp {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static void main(String[] args) {
        Options options = new Options();

        Option classpathOpt = new Option("cp", "classpath", true, "classpath for JVM to search class files");
        classpathOpt.setRequired(false);
        options.addOption(classpathOpt);

        Option outputOpt = new Option("o", "output", true,
                "specify the output file to write, or the output will be printed to stdout");
        outputOpt.setRequired(false);
        options.addOption(outputOpt);

        Option debugOpt = new Option("X", "debug", false, "running with debug");
        debugOpt.setRequired(false);
        options.addOption(debugOpt);

        Option editDistanceOpt = new Option("ed", "edit-distance", true,
                "specify the edit distance threshold to evaluate reproduced and recorded exception strings, default is 0 (exact match)");
        editDistanceOpt.setRequired(false);
        options.addOption(editDistanceOpt);

        Option timeoutOpt = new Option("t", "timeout", true,
                "specify the timeout second(s) for waiting debug session to terminate, default is 0 (disable timeout)");
        timeoutOpt.setRequired(false);
        options.addOption(timeoutOpt);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args, true);
        } catch (ParseException e) {
            e.printStackTrace();

            printHelp(formatter, options);
            System.exit(1);
        }

        String[] remainingArgs = cmd.getArgs();
        if (remainingArgs.length < 2) {
            System.err.println("No enough command line arguemnts");

            printHelp(formatter, options);
            System.exit(1);
        }

        String crashLog = remainingArgs[0];
        String mainClass = remainingArgs[1];
        String progArgs = String.join(" ", Arrays.copyOfRange(remainingArgs, 2, remainingArgs.length));

        String classPath = cmd.hasOption("classpath") ? cmd.getOptionValue("classpath") : "";

        if (cmd.hasOption("debug")) {
            // DijSettings.getCurrent().setLogLevel("ALL");
            LOGGER.setLevel(Level.ALL);
        } else {
            // DijSettings.getCurrent().setLogLevel("WARNING");
            LOGGER.setLevel(Level.WARNING);
        }

        if (cmd.hasOption("edit-distance")) {
            int editDistance = 0;
            try {
                editDistance = Integer.parseInt(cmd.getOptionValue("edit-distance"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.err.println(String.format("Invalid value %s for option 'edit-distance'", cmd.getOptionValue("edit-distance")));
                System.exit(1);
            }

            if (editDistance < 0) {
                System.err.println(String.format("Cannot set 'edit-distance' to negative value %d", editDistance));
                System.exit(1);
            }

            DijSettings.getCurrent().setEditDistance(editDistance);
        }

        if (cmd.hasOption("timeout")) {
            int timeout = 0;
            try {
                timeout= Integer.parseInt(cmd.getOptionValue("timeout"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.err.println(String.format("Invalid value %s for option 'timeout'", cmd.getOptionValue("timeout")));
                System.exit(1);
            }
            
            if (timeout < 0) {
                System.err.println(String.format("Cannot set 'timeout' to negative value %d", timeout));
                System.exit(1);
            }

            DijSettings.getCurrent().setTimeout(timeout);
        }

        DijSession dijSession = new DijSession(mainClass, progArgs, "", "", classPath, crashLog);
        try {
            dijSession.reproduce();
        } catch (DijException e) {
            e.printStackTrace();
            System.err.println("Failed to reproduce");
            System.exit(1);
        }

        List<ReproductionResult> reproductionResults = dijSession.getReproductionResults();

        String output = cmd.getOptionValue("output");
        if (output != null) {
            try {
                writeToFile(output, reproductionResults.stream().map(ReproductionResult::toString).collect(Collectors.toList()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.err.println("Failed to write reproduction result(s) to file");
                System.exit(1);
            }
        } else {
            reproductionResults.forEach(ReproductionResult::print);
        }
    }

    private static void printHelp(HelpFormatter formatter, Options options) {
        String cmdLineSyntax = "DijApp [OPTION]... CRASH_LOG MAIN_CLASS [PROG_ARG]...";
        String header = "Reproduce the crash from the crash log";
        String footer = "Author: Chengzeyi - ichengzeyi@gmail.com";
        formatter.printHelp(cmdLineSyntax, header, options, footer);
    }

    private static void writeToFile(String filepath, Iterable<String> lines) throws FileNotFoundException {
        PrintStream ps = new PrintStream(filepath);
        try {
            lines.forEach(line -> ps.println(line));
        } finally {
            ps.close();
        }
    }
}
