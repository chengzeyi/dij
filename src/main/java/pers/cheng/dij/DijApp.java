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
import org.apache.commons.cli.PatternOptionBuilder;

import pers.cheng.dij.core.wrapper.ReproductionResult;
import pers.cheng.dij.runner.DijSession;

public class DijApp {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static void main(String[] args) {
        Options options = new Options();

        Option classpathOpt = new Option("cp", "classpath", true, "class search path of directories and zip/jar files");
        classpathOpt.setRequired(false);
        options.addOption(classpathOpt);

        Option modulePathOpt = new Option("m", "module-path", true, "module search path of directories");
        modulePathOpt.setRequired(false);
        options.addOption(modulePathOpt);

        Option cwdOpt = new Option("c", "cwd", true, "current working directory for the launched program");
        cwdOpt.setRequired(false);
        options.addOption(cwdOpt);
        
        Option outputOpt = new Option("o", "output", true,
                "specify the output file to write, or the output will be printed to stdout");
        outputOpt.setRequired(false);
        options.addOption(outputOpt);

        Option debugOpt = new Option("X", "debug", false, "running in debug mode");
        debugOpt.setRequired(false);
        options.addOption(debugOpt);
        
        Option targetFrameOpt = new Option("tf", "target-frame", true, "1-indexed target frame element in the crash log for setting breakpoint, or the first valid one will be used, default is 0 (not specified)");
        targetFrameOpt.setRequired(false);
        targetFrameOpt.setType(PatternOptionBuilder.NUMBER_VALUE);
        options.addOption(targetFrameOpt);

        Option editDistanceOpt = new Option("ed", "edit-distance", true,
                "edit distance threshold to evaluate reproduced and recorded exception strings, default is 0 (exact match)");
        editDistanceOpt.setRequired(false);
        editDistanceOpt.setType(PatternOptionBuilder.NUMBER_VALUE);
        options.addOption(editDistanceOpt);

        Option timeoutOpt = new Option("t", "timeout", true,
                "timeout second(s) for waiting debug session to terminate, default is 0 (timeout disabled)");
        timeoutOpt.setRequired(false);
        timeoutOpt.setType(PatternOptionBuilder.NUMBER_VALUE);
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
        if (remainingArgs.length < 1) {
            System.err.println("No enough command line arguemnts");

            printHelp(formatter, options);
            System.exit(1);
        }

        String crashLog = remainingArgs[0];
        String mainClass = "";
        if (remainingArgs.length >= 2) {
            mainClass = remainingArgs[1];
        }
        String progArgs = "";
        if (remainingArgs.length >= 3) {
            progArgs = String.join(" ", Arrays.copyOfRange(remainingArgs, 2, remainingArgs.length));
        }

        String classPaths = cmd.hasOption("classpath") ? cmd.getOptionValue("classpath") : "";
        String modulePaths = cmd.hasOption("module-path") ? cmd.getOptionValue("module-path") : "";
        String cwd = cmd.hasOption("cwd") ? cmd.getOptionValue("cwd") : ".";

        if (cmd.hasOption("debug")) {
            // DijSettings.getCurrent().setLogLevel("ALL");
            LOGGER.setLevel(Level.ALL);
        } else {
            // DijSettings.getCurrent().setLogLevel("WARNING");
            LOGGER.setLevel(Level.WARNING);
        }

        if (cmd.hasOption("target-frame")) {
            int targetFrame = 0;
            try {
                targetFrame = (Integer) cmd.getParsedOptionValue("target-frame");
            } catch (ParseException e) {
                e.printStackTrace();
                System.err.println("Failed to parse option 'target-frame'");
                System.exit(1);
            }
            DijSettings.getCurrent().setTargetFrame(targetFrame);
        }

        if (cmd.hasOption("edit-distance")) {
            int editDistance = 0;
            try {
                editDistance = (Integer) cmd.getParsedOptionValue("edit-distance");
            } catch (ParseException e) {
                e.printStackTrace();
                System.err.println("Failed to parse option 'edit-distance'");
                System.exit(1);
            }
            DijSettings.getCurrent().setEditDistance(editDistance);
        }

        if (cmd.hasOption("timeout")) {
            int timeout = 0;
            try {
                timeout = (Integer) cmd.getParsedOptionValue("timeout");
            } catch (ParseException e) {
                e.printStackTrace();
                System.err.println("Failed to parse option 'timeout'");
                System.exit(1);
            }
            DijSettings.getCurrent().setTimeout(timeout);
        }

        DijSession dijSession = new DijSession(mainClass, progArgs, "", modulePaths, classPaths, cwd, crashLog);
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
                writeToFile(output,
                        reproductionResults.stream().map(ReproductionResult::toString).collect(Collectors.toList()));
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
        String cmdLineSyntax = "DijApp [OPTION]... CRASH_LOG [MAIN_CLASS [PROG_ARG]...]";
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
