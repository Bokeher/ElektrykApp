package com.example.planlekcji.replacements;

import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplacementDataProcessor {
    private final List<Replacement> rawReplacements;
    private final List<String> replacements = new ArrayList<>();

    public List<String> getReplacements() {
        return replacements;
    }

    public ReplacementDataProcessor(List<Replacement> rawReplacements) {
        this.rawReplacements = rawReplacements;
    }

    public void process() {
        // document headers '\n \n' replacement1 '\n \n' replacement2 ...
//        ArrayList<String> temp = new ArrayList<>(Arrays.asList(rawReplacements.split("\n \n")));
//        temp.remove(0); // remove first element containing unnecessary info
//
//        for(String replacement : temp) {
//            String[] lines = replacement.split("\n");
//
//            // remove empty lines and table headers
//            lines = Arrays.stream(lines)
//                    .filter(line -> !(line.contains("LP. ") || line.length() < 8))
//                    .toArray(String[]::new);
//
//            for (int i = 0; i < lines.length; i++) {
//                String line = lines[i];
//                String[] words = line.split(" ");
//
//                if (words.length > 3) {
//                    // swap first and second name
//                    String swapTemp = words[2];
//                    words[2] = getInitial(words[1]); // also change second name to initial
//                    words[1] = swapTemp;
//
//                    // highlight first and second name
//                    words[1] = "<b><font color=\"#d4b085\">" + words[1];
//                    words[2] = words[2]+"</font></b>";
//                }
//
//                lines[i] = String.join(" ", words);
//            }
//
//            // bold the date of replacement
//            lines[0] = "<b>"+lines[0]+"</b>";
//
//            String newReplacement = String.join("<br>", lines); // add <br>s
//            newReplacement = newReplacement.replaceAll(" {2,}", " "); // replace all consecutive spaces into one space
//
//            replacements.add(newReplacement);
//        }
    }

    private String getInitial(String str) {
        if (str == null || str.length() < 2) {
            throw new IllegalArgumentException("Input string must have at least two character");
        }

        str = str.toLowerCase();
        char firstChar = str.charAt(0);
        char secondChar = str.charAt(1);

        final String[] polishDigraphs = {"sz", "cz", "dż", "dź", "rz", "ch", "dz"};

        for (String digraph : polishDigraphs) {
            if (digraph.charAt(0) == firstChar) {
                if (digraph.charAt(1) == secondChar) {
                    String ch1 = digraph.substring(0, 1).toUpperCase();
                    String ch2 = digraph.substring(1, 2);
                    return ch1.concat(ch2);
                }
            }
        }

        return String.valueOf(firstChar).toUpperCase();
    }
}
