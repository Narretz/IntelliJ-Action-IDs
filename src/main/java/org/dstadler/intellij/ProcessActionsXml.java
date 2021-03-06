package org.dstadler.intellij;

import com.google.common.collect.SortedSetMultimap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.dstadler.intellij.file.ActionDirectoryWalker;
import org.dstadler.intellij.xml.ActionIDDef;

import java.io.*;

/**
 * Small helper app to extract all the available ActionIDs of an IntelliJ installation
 * to provide a nicely readable list
 */
public class ProcessActionsXml {
    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.err.println("Usage: ProcessActionsXml <IntelliJDir1> [<IntelliJDir2] ...");
            return;
        }

        try (final Writer out = new BufferedWriter(new FileWriter("docs/_data/actions.csv"))) {
            try (final CSVPrinter printer = CSVFormat.DEFAULT.withHeader("file", "id", "text", "group").print(out)) {
                for (String dir : args) {
                    ActionDirectoryWalker walker = new ActionDirectoryWalker();
                    final SortedSetMultimap<String, ActionIDDef> ids = walker.walk(new File(dir));
                    for (String file : ids.keySet()) {
                        for (ActionIDDef def : ids.get(file)) {
                            final String fileName = StringUtils.removeStart(StringUtils.removeStart(StringUtils.removeStart(file, dir), "\\"), "/");
                            System.out.println("File: " + fileName + ": had " + def.getId() + "/" + def.getText() + "/" + def.getGroup());
                            printer.print(fileName);
                            printer.print(def.getId());
                            printer.print(def.getText());
                            printer.print(def.getGroup());
                            printer.println();
                        }
                    }

                    System.out.println("Found " + ids.size() + " actions in " + ids.keySet().size() + " files");
                }
            }
        }
    }
}
