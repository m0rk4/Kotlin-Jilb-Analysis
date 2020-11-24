package by.mark;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextParser {

    public static List<String> tokens(List<String> list) {

        Optional<String> concat = list.stream()
                .filter(s -> !s.trim().isEmpty())
                .map(String::trim)
                .filter(s -> !s.startsWith("import"))
                .filter(s -> !s.startsWith("package"))
                .reduce((s1, s2) -> (s1 + "\n" + s2));

        if (concat.isPresent()) {
            String fileSource = concat.get();
            // remove comms
            fileSource = fileSource.replaceAll(
                    "(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/|[ \\t]*//.*)",
                    " "
            );

            list = Arrays.stream(fileSource.split("\n")).collect(Collectors.toList());

            // removing String and char literals ints floats
            list = list.stream()
                    .map(s -> s.replaceAll("('.')", " "))
                    .map(s -> s.replaceAll("(\".*\")", " "))
                    .map(s -> s.replaceAll("(<.*>)", " "))
                    .map(s -> {
                        Matcher first = Pattern.compile("([|&<>(=][ ]*)-[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")
                                .matcher(s);
                        while (first.find()) {
                            s = (new StringBuilder(s).replace(first.start() + 1, first.end(), " ")).toString();
                            first = Pattern.compile("((\\([ ]*)|(=[ ]*))-[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")
                                    .matcher(s);
                        }
                        return s;
                    })
                    .map(s -> s.replaceAll("[0-9]+\\.?[0-9]+([eE][-+]?[0-9]+)?", " "))
                    .collect(Collectors.toList());

            // spaces split
            list = list.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .flatMap(s -> Arrays.stream(s.split("\\s+")))
                    .map(String::trim)
                    .collect(Collectors.toList());

            // 100% workable
            list = list.stream()
                    .flatMap(s -> Arrays.stream(split(s, "[\\@\\;\\[\\]\\(\\)\\{\\}\\,]")))
                    .flatMap(s -> Arrays.stream(split(s, "(\\&\\&)")))
                    .flatMap(s -> Arrays.stream(split(s, "(\\|\\|)")))
                    .flatMap(s -> Arrays.stream(split(s, "(\\?\\.)")))
                    .flatMap(s -> Arrays.stream(split(s, "(\\?:)")))
                    .flatMap(s -> {
                        if (s.matches("(\\?\\.)|(\\?:)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[\\?]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(\\.\\.)")))
                    .flatMap(s -> {
                        if (s.matches("(\\.\\.)|(\\?\\.)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[\\.]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(::)")))
                    .flatMap(s -> {
                        if (s.matches("(\\?:)|(::)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[:]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(\\+\\+)")))
                    .flatMap(s -> Arrays.stream(split(s, "(\\+=)")))
                    .flatMap(s -> {
                        if (s.matches("(\\+\\+)|(\\+=)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[\\+]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(\\-\\-)")))
                    .flatMap(s -> Arrays.stream(split(s, "(\\-=)")))
                    .flatMap(s -> {
                        if (s.matches("(--)|(-=)|(->)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[-]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(\\*=)|(/=)|(%=)")))
                    .flatMap(s -> {
                        if (s.matches("(\\*=)|(/=)|(%=)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[*/%]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(>=)|(<=)|(->)")))
                    .flatMap(s -> {
                        if (s.matches("(>=)|(<=)|(->)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[<>]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(\\!\\!)")))
                    .flatMap(s -> Arrays.stream(split(s, "(\\!=)")))
                    .flatMap(s -> {
                        if (s.matches("(!!)|(!=)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[!]"));
                    })
                    .flatMap(s -> Arrays.stream(split(s, "(==)")))
                    .flatMap(s -> {
                        if (s.matches("(\\+=)|(-=)|(\\*=)|(/=)|(%=)|(==)|(!=)|(<=)|(>=)"))
                            return Arrays.stream(singleArray(s));
                        return Arrays.stream(split(s, "[=]"));
                    })
                    .map(String::trim)
                    .filter(s -> s.trim().length() != 0)
                    .collect(Collectors.toList());
        }

        return list;
    }

    private static String[] split(String s, String reg) {
        String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
        return s.split(String.format(WITH_DELIMITER, reg));
    }

    private static String[] singleArray(String s) {
        return new String[]{s};
    }

}
