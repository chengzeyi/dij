package pers.cheng.dij;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
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

        String classPath = cmd.getOptionValue("classpath");
        if (classPath == null) {
            classPath = "";
        }

        String output = cmd.getOptionValue("output");

        boolean debug = cmd.hasOption("debug");
        if (debug) {
            DijSettings.getCurrent().setLogLevel("ALL");
        } else {
            DijSettings.getCurrent().setLogLevel("WARNING");
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
