package by.mark;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ResultSet {

    private final Map<String, Integer> operators = new HashMap<>();
    private final Map<String, Pair<Integer, Integer>> condOperators = new HashMap<>();
    private final Set<String> ignoredKeyWords = new HashSet<>();
    private final Set<String> operatorKeyWords = new HashSet<>();

    public ResultSet(Model model) {
        List<String> list = Arrays.asList("+",
                "-",
                "*",
                "/",
                "%",
                "=",
                "+=",
                "-=",
                "*=",
                "/=",
                "%=",
                "++",
                "--",
                "&&",
                "||",
                "!",
                "==",
                "!=",
                "===",
                "!==",
                "<",
                "<=",
                ">",
                ">=",
                "[",
                "(",
                "{",
                ".",
                "!!",
                "?.",
                "?:",
                "::",
                "..",
                ":",
                "?",
                "->",
                "@",
                ";",
                ",",
                "shl",
                "shr",
                "ushr",
                "and",
                "or",
                "xor",
                "inv",
                "as",
                "as?",
                "break",
                "continue",
                "if",
                "do",
                "while",
                "for",
                "when",
                "in",
                "!in",
                "is",
                "!is",
                "return",
                "try",
                "throw",
                "until",
                "downTo");
        operatorKeyWords.addAll(list);
    }

    public Map<String, Integer> getOperators() {
        return operators;
    }

    public Map<String, Pair<Integer, Integer>> getCondOperators() {
        return condOperators;
    }

    public int getNumberOfConditionalOperators() {
        int total = 0;
        for (Map.Entry<String, Pair<Integer, Integer>> entry : condOperators.entrySet()) {
            total += entry.getValue().getValue();
        }
        return total;
    }

    public int getNumberOfOperators() {
        int total = 0;
        for (Map.Entry<String, Integer> entry : operators.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    public int getMaxNestedLevel() {
        return maxDepth;
    }

    public void process(List<String> tokens) {
        ListIterator<String> iterator = tokens.listIterator();
        while (iterator.hasNext()) {
            String currToken = iterator.next();
            if (operatorKeyWords.contains(currToken))
                addOperator(currToken, operators);
            else {
                if (iterator.hasNext()) {
                    String nextToken = iterator.next();
                    if (nextToken.equals("(")) {
                        currToken += "(..)";
                        addOperator(currToken, operators);
                    }
                    iterator.previous();
                }
            }
        }
        correct();
        countCondOperators(tokens);
        maxLevel(tokens);
    }


    private boolean isCond(String token) {
        return (token.equals("if") || token.equals("while") || token.equals("do") || token.equals("when")
                || token.equals("for") || token.equals("else"));
    }


    private int maxDepth = -1;

    private void maxLevel(List<String> tokens) {
        tokens = removeFuns(tokens);
        List<String> onlyNecessary = clean(tokens);
        System.out.println("\n");
        for (String s : onlyNecessary) {
            System.out.print(s + ' ');
        }
        List<String> temp = removeDoWhile(onlyNecessary);
        System.out.println("\n");
        for (String s : temp) {
            System.out.print(s + ' ');
        }
        List<String> onlyIfElseStatements = changeLeftToIf(temp);
        System.out.println("\n");
        for (String s : onlyIfElseStatements) {
            System.out.print(s + ' ');
        }
        System.out.println();
        List<String> finalExp = bracketsIns(onlyIfElseStatements);
        System.out.println("\nBrackets Ins: ");
        for (String s : finalExp) {
            System.out.print(s + ' ');
        }
        List<String> almost = elseIfHandle(finalExp);
        System.out.println("\nElse if handle:");
        for (String s : almost) {
            System.out.print(s + ' ');
        }
        List<String> almostReally = fight(almost);
        System.out.println("\nDELETED");
        for (String s : almostReally) {
            System.out.print(s + ' ');
        }
        List<String> finalRes = almostReally.stream()
                .filter(s -> !s.equals("if"))
                .collect(Collectors.toList());

        System.out.println();
        System.out.println("FINAL");
        for (String s : finalRes) {
            System.out.print(s + " ");
        }
        System.out.println();
        int res = maxLev(finalRes);
        maxDepth = finalCheck(res);
    }

    private List<String> removeFuns(List<String> tokens) {
        String[] toks = tokens.toArray(new String[0]);
        for (int i = 0; i < toks.length; ) {
            if (toks[i] != null && toks[i].equals("fun")) {
                // look for )
                int j = i;
                while (toks[j] == null || !toks[j].equals(")"))
                    j++;
                j++;
                if (toks[j] != null && toks[j].equals("{")) {
                    toks[j++] = null;
                    int count = 1;
                    while (count != 0) {
                        if (toks[j] != null)
                            if (toks[j].equals("{"))
                                count++;
                            else if (toks[j].equals("}"))
                                count--;
                        j++;
                    }
                    System.out.println("lol");
                    toks[j - 1] = null;
                }
                i = j;
            } else i++;
        }
        List<String> ha = new LinkedList<>();
        for (String tok : toks) {
            if (tok != null)
                ha.add(tok);
        }
        return ha;
    }

    private int finalCheck(int res) {
        return res < 2 ? 0 : res - 1;
    }

    private List<String> fight(List<String> list) {
        for (int i = list.size() - 1; i >= 0; ) {
            if (list.get(i).equals("else")) {
                list.remove(i);
                int startBlock = i;
                int endBlock = findEnd(list, startBlock);
                int len = endBlock - startBlock + 1;
                int j = i - 1;
                while (notIfBracket(list, j))
                    j--;

                j++;
                for (int c = 0; c < len; c++) {
                    String val = list.remove(startBlock + c);
                    list.add(j + c, val);
                }

                i = list.size() - 1;
            } else i--;
        }


        return list;
    }

    private boolean notIfBracket(List<String> list, int start) {
        int count = 1;
        int j = start - 1;
        while (count != 0) {
            if (list.get(j).equals("}"))
                count++;
            else if (list.get(j).equals("{"))
                count--;
            j--;
        }
        return list.get(j).equals("else");
    }

    private int findEnd(List<String> list, int startBlock) {
        int count = 1;
        int j = startBlock + 1;
        while (count != 0) {
            if (list.get(j).equals("{"))
                count++;
            else if (list.get(j).equals("}"))
                count--;
            j++;
        }
        return j - 1;
    }

    private List<String> elseIfHandle(List<String> list) {
        for (int i = 0; i < list.size(); ) {
            if (list.get(i).equals("else") && list.get(i + 1).equals("if")) {
                int j = i;
                list.add(j + 1, "{");
                j += 3;
                int count_sk = 0;
                boolean first = true;
                while (first || count_sk != 0) {
                    if (list.get(j).equals("{")) {
                        first = false;
                        count_sk++;
                    } else if (list.get(j).equals("}")) {
                        first = false;
                        count_sk--;
                    }
                    j++;
                }
                list.add(j, "}");
                i += 3;
            } else i++;
        }
        return list;
    }

    private int maxLev(List<String> list) {
        int max = Integer.MIN_VALUE, curr = 0;
        String[] toks = list.toArray(new String[0]);
        for (String tok : toks) {
            if (tok.equals("{")) {
                curr++;
            } else {
                if (curr > max)
                    max = curr;
                curr--;
            }
        }
        return max;
    }

    private int getLastBracket(List<String> list, int ind) {
        int count = 0;
        boolean first = true;
        int j = ind;
        while (first || count != 0) {
            if (list.get(j).equals("{")) {
                first = false;
                count++;
            } else if (list.get(j).equals("}")) {
                first = false;
                count--;
            }
            j++;
        }
        return j;
    }

    private List<String> bracketsIns(List<String> list) {
        for (int i = 0; i < list.size(); ) {
            if (list.get(i).equals("if") && !list.get(i + 1).equals("{")) {
                list.add(i + 1, "{");
                int j = i + 2;
                while (true) {
                    int insPoint = getLastBracket(list, j);
                    if (insPoint < list.size() && list.get(insPoint).equals("else")) {
                        j = insPoint;
                    } else {
                        j = insPoint;
                        break;
                    }
                }
                list.add(j, "}");
                i += 2;
            } else i++;
        }
        return list;
    }

    private List<String> changeLeftToIf(List<String> temp) {
        ListIterator<String> iterator = temp.listIterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            if (token.equals("for") || token.equals("while")) {
                iterator.set("if");
            }
        }
        return temp;
    }

    private List<String> removeDoWhile(List<String> onlyNecessary) {
        String[] toks = onlyNecessary.toArray(new String[0]);
        for (int i = 0; i < toks.length; ) {
            if (toks[i] != null && toks[i].equals("do")) {
                toks[i] = "if";
                // next {
                Stack<String> stack = new Stack<>();
                stack.push("{");
                // i - do i + 1 - {
                int j = i + 2;
                while (!stack.isEmpty()) {
                    while (toks[j] == null || (!toks[j].equals("{") && !toks[j].equals("}") && !toks[j].equals("do")))
                        j++;

                    if (!toks[j].equals("do")) {
                        String get = toks[j];
                        if (get.equals("}")) {
                            if (stack.peek().equals("{"))
                                stack.pop();
                            else
                                stack.push(get);
                        } else {
                            stack.push(get);
                        }
                    } else {
                        j = lastRemovedDo(toks, j);
                    }
                    j++;
                }

                // j - 1 } j - while
                toks[j] = null;

                i = j;
            } else i++;
        }

        List<String> withoutDo = new LinkedList<>();
        for (String tok : toks) {
            if (tok != null)
                withoutDo.add(tok);
        }

        return withoutDo;
    }

    private int lastRemovedDo(String[] toks, int i) {
        System.out.println("lol");
        toks[i] = "if";
        // next {
        Stack<String> stack = new Stack<>();
        stack.push("{");
        // i - do i + 1 - {
        int j = i + 2;
        System.out.println(toks[j]);
        while (!stack.isEmpty()) {
            while (toks[j] == null || (!toks[j].equals("{")
                    &&
                    !toks[j].equals("}")
                    && !toks[j].equals("do")))
                j++;

            if (!toks[j].equals("do")) {
                String get = toks[j];
                if (get.equals("}")) {
                    if (stack.peek().equals("{")) {
                        stack.pop();
                    } else
                        stack.push(get);
                } else {
                    stack.push(get);
                }
            } else {
                j = lastRemovedDo(toks, j);
            }
            j++;
        }

        // j - 1 } j - while
        toks[j] = null;

        return j - 1;
    }


    private List<String> handleNoBracket(List<String> tokens) {
        String[] toks = tokens.toArray(new String[0]);

        for (int i = 0; i < toks.length; ) {
            if (toks[i].equals("if") || toks[i].equals("while")
                    || toks[i].equals("else") || toks[i].equals("for") || toks[i].equals("->")) {
                int j = i;

                if (toks[i].equals("else") && toks[i + 1].equals("->")) {
                    i++;
                    continue;
                }

                // check if not do while
                if (toks[i].equals("while")) {
                    if (toks[i - 1] != null && toks[i - 1].equals("}")) {
                        System.out.println(i);
                        Stack<String> stack = new Stack<>();
                        stack.push("}");
                        j = i - 2;
                        while (!stack.isEmpty()) {
                            while (toks[j] == null || (!toks[j].equals("{") && !toks[j].equals("}"))) {
                                j--;
                            }
                            if (toks[j].equals("{")) {
                                if (stack.peek().equals("}"))
                                    stack.pop();
                                else
                                    stack.push("{");
                            } else {
                                stack.push("}");
                            }
                            j--;
                        }
                        if (toks[j] != null && toks[j].equals("do")) {
                            i++;
                            continue;
                        }
                        j = i;
                    }
                }

                if (toks[i].equals("else") || toks[i].equals("->")) {
                    j = i + 1;
                } else {
                    j++;
                    while (toks[j] == null || !toks[j].equals(")")) {
                        toks[j++] = null;
                    }
                    toks[j++] = null;
                }

                if (!isCond(toks[j]) && !toks[j].equals("{")) {
                    toks[j++] = "{}";
                    while (j < toks.length &&
                            (toks[j] == null || (!isCond(toks[j]) && !toks[j].equals("}") && !toks[j].equals("{") && !toks[j].equals("->")))) {
                        toks[j++] = null;
                    }
                }

                i = j;
            } else i++;
        }


        List<String> list = new LinkedList<>();
        for (String tok : toks) {
            if (tok != null)
                if (tok.equals("{}")) {
                    list.add("{");
                    list.add("}");
                } else
                    list.add(tok);
        }
        System.out.println(list);
        return list;
    }


    private List<String> clean(List<String> tokens) {
        List<String> temp = handleNoBracket(tokens);

        temp = temp.stream()
                .filter(s -> isCond(s) || s.equals("{") || s.equals("}") || s.equals("->"))
                .collect(Collectors.toList());

        for (String s : temp) {
            System.out.print(s + " ");
        }
        System.out.println();

        String[] toks = temp.toArray(new String[0]);
        for (int i = 0; i < toks.length; ) {
            if (toks[i] != null && toks[i].equals("when")) {
                toks[i] = null;

                Stack<String> stack = new Stack<>();
                stack.push("{");
                // if when is empty delete
                if (toks[i + 1].equals("{") && toks[i + 2].equals("}")) {
                    toks[i + 1] = null;
                    toks[i + 2] = null;
                    i += 3;
                } else {
                    toks[i + 1] = null;
                    toks[i + 2] = "if";
                    i += 3;

                    int j = i;
                    while (!stack.isEmpty()) {
                        while (toks[j] == null || (!toks[j].equals("{") && !toks[j].equals("}") && !toks[j].equals("when")))
                            j++;
                        if (!toks[j].equals("when")) {
                            String get = toks[j];
                            if (get.equals("}")) {
                                if (stack.peek().equals("{"))
                                    stack.pop();
                                else
                                    stack.push(get);
                            } else {
                                stack.push(get);
                            }
                        } else {
                            j = lastRemovedWhen(toks, j);
                        }
                        j++;
                    }


                    // j - 1 - closing of when
                    toks[j - 1] = null;

                    for (int k = i; k < j; k++) {
                        if (toks[k] != null && toks[k - 1] != null && toks[k].equals("->") &&
                                toks[k - 1].equals("else"))
                            toks[k] = null;
                        else if (toks[k] != null && toks[k].equals("->"))
                            toks[k] = "elseIf";
                    }

                    i = j;
                }
            } else i++;
        }

        List<String> finalList = new LinkedList<>();
        for (String tok : toks) {
            if (tok != null) {
                if (tok.equals("elseIf")) {
                    System.out.println("What");
                    finalList.add("else");
                    finalList.add("if");
                } else {
                    finalList.add(tok);
                }
            }
        }

        return finalList;
    }


    private int lastRemovedWhen(String[] toks, int i) {
        toks[i] = null;

        Stack<String> stack = new Stack<>();
        stack.push("{");
        // if when is empty delete
        if (toks[i + 1].equals("{") && toks[i + 2].equals("}")) {
            toks[i + 1] = null;
            toks[i + 2] = null;
            return i + 2;
        } else {
            toks[i + 1] = null;
            toks[i + 2] = "if";
            i += 3;

            int j = i;
            while (!stack.isEmpty()) {
                while (toks[j] == null || (!toks[j].equals("{") && !toks[j].equals("}") && !toks[j].equals("when")))
                    j++;
                if (!toks[j].equals("when")) {
                    String get = toks[j];
                    if (get.equals("}")) {
                        if (stack.peek().equals("{"))
                            stack.pop();
                        else
                            stack.push(get);
                    } else {
                        stack.push(get);
                    }
                } else {
                    j = lastRemovedWhen(toks, j);
                }
                j++;
            }

            // j - 1 - closing of when
            toks[j - 1] = null;

            for (int k = i; k < j - 1; k++) {
                if (toks[k] != null && toks[k - 1] != null && toks[k].equals("->") &&
                        toks[k - 1].equals("else"))
                    toks[k] = null;
                else if (toks[k] != null && toks[k].equals("->"))
                    toks[k] = "elseIf";
            }
            i = j - 1;
        }

        return i;
    }


    private void countCondOperators(List<String> tokens) {
        putCondOpers("while(..)", "if(..) else(..)", "do {..} while(..)", "for(..)", "when(..)");

        // for when
        int ifThenElseInWhen = 0;
        ListIterator<String> iterator = tokens.listIterator();
        while (iterator.hasNext()) {
            String currToken = iterator.next();
            if (currToken.equals("->")) {
                iterator.previous();
                String prevToken = iterator.previous();
                if (!prevToken.equals("else")) {
                    ifThenElseInWhen++;
                }
                iterator.next();
                iterator.next();
            }
        }

        if (condOperators.containsKey("when(..)")) {
            Pair<Integer, Integer> whenNumber = condOperators.get("when(..)");
            condOperators.remove("when(..)");
            condOperators.put("when(..)", new Pair<>(whenNumber.getKey(), ifThenElseInWhen));
        }
    }

    private void putCondOpers(String... s) {
        for (String o : s)
            if (operators.containsKey(o))
                condOperators.put(o, new Pair<>(operators.get(o), operators.get(o)));
    }

    private void correct() {

        if (operators.containsKey("do")) {
            int countDo = operators.get("do");
            int countWhile = operators.get("while");
            operators.remove("do");
            operators.put("do {..} while(..)", countDo);
            operators.remove("while");
            operators.put("while(..)", countWhile - countDo);
        }

        correct("if", "if(..) else(..)");
        correct("try", "try-catch-finally");
        correct("while", "while(..)");
        correct("for", "for(..)");
        correct("when", "when(..)");
        correct("[", "[..]");
        correct("{", "{..}");
        correct("(", "(..)");

        int funCalls = 0;
        for (Map.Entry<String, Integer> entry : operators.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("(..)") && key.endsWith("(..)"))
                funCalls += entry.getValue();
        }

        if (funCalls != 0) {
            int prev = 0;
            if (operators.containsKey("(..)"))
                prev = operators.get("(..)");
            if (funCalls == prev)
                operators.remove("(..)");
            else
                operators.put("(..)", prev - funCalls);
        }

    }

    private void correct(String s1, String s2) {
        if (operators.containsKey(s1)) {
            int count = operators.get(s1);
            operators.remove(s1);
            operators.put(s2, count);
        }
    }

    private void addOperator(String operator, Map<String, Integer> map) {
        if (map.containsKey(operator)) {
            int count = map.get(operator);
            map.put(operator, ++count);
        } else
            map.put(operator, 1);
    }

}
