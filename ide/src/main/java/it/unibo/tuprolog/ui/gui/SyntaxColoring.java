package it.unibo.tuprolog.ui.gui;

import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SyntaxColoring {


    private static final String[] KEYWORDS = new String[]{
            "rem", "mod", "is",
            /*":-",  "-->", ","  "?-", "->",";","\\+","=", "\\=",
            "==", "\\==", "@<", "@=<", "@>", "@>=","=..",
            "=:=", "=\\=", "<", "=<", ">", ">=","+", "-", "\\",
            "^","**","*", "/", "//", "<<", ">>",
            "+", "-", "\\/", "/\\",*/
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String FULLSTOP_PATTERN = "\\.\\s*([\\n\\r]+|$)"; //. E A CAPO OPPURE SPAZIO BIANCO
    private static final String STRING_PATTERN = "(\"([^\"\\\\]|\\\\.)*\")|('([^'\\\\]|\\\\.)*')";
    private static final String COMMENT_PATTERN = "%[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String VARIABLE_PATTERN = "\\b([_A-Z][_A-Za-z0-9]*)\\b";
    private static final String ATOM_PATTERN = "\\b([a-z][_A-Za-z0-9]*)\\b";
    private static final String FUNCTOR_PATTERN = ATOM_PATTERN + "\\s*(?=\\()";
    private static final String INTEGER_PATTERN = "[0-9]+";
    private static final String HEX_PATTERN = "0[xX][0-9A-Fa-f]+";
    private static final String BIN_PATTERN = "0[bB][0-1]+";
    private static final String OCT_PATTERN = "0[oO][0-7]+";
    private static final String FLOAT_PATTERN = "[0-9]+\\.[0-9]+([eE][-+]?[0-9]+)?";
    private static final String CHAR_PATTERN = "0'(\\\\[abfnrtv'`\"]|.)";
    private static final String NUMBER_PATTERN = "\\b(" + String.join("|", new String[]{
            FLOAT_PATTERN,
            INTEGER_PATTERN,
            HEX_PATTERN,
            BIN_PATTERN,
            OCT_PATTERN,
            CHAR_PATTERN
    }) + ")\\b";


    private static final Pattern PATTERN = Pattern.compile(
            new StringBuilder().append("(?<KEYWORD>").append(KEYWORD_PATTERN).append(")")
                    .append("|(?<PAREN>").append(PAREN_PATTERN).append(")")
                    .append("|(?<NUMBER>").append(NUMBER_PATTERN).append(")")
                    .append("|(?<BRACE>").append(BRACE_PATTERN).append(")")
                    .append("|(?<BRACKET>").append(BRACKET_PATTERN).append(")")
                    .append("|(?<STRING>").append(STRING_PATTERN).append(")")
                    .append("|(?<COMMENT>").append(COMMENT_PATTERN).append(")")
                    .append("|(?<VARIABLE>").append(VARIABLE_PATTERN).append(")")
                    .append("|(?<FUNCTOR>").append(FUNCTOR_PATTERN).append(")")
                    .append("|(?<ATOM>").append(ATOM_PATTERN).append(")")
                    .append("|(?<FULLSTOP>").append(FULLSTOP_PATTERN).append(")")
                    .toString()
    );

    private CodeArea codeArea;
    private ExecutorService executor;

    public SyntaxColoring(CodeArea codeArea) {
        this.executor = Executors.newSingleThreadExecutor();
        this.codeArea = codeArea;
        String defText = codeArea.getText();
        codeArea.replaceText("");
        codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(200))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        codeArea.replaceText(defText);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = "";
            if (matcher.group("KEYWORD") != null) styleClass = "keyword";
            else if (matcher.group("PAREN") != null) styleClass = "paren";
            else if (matcher.group("BRACE") != null) styleClass = "brace";
            else if (matcher.group("BRACKET") != null) styleClass = "bracket";
            else if (matcher.group("FULLSTOP") != null) styleClass = "fullstop"; //CHANGE TO FULLSTOP
            else if (matcher.group("STRING") != null) styleClass = "string";
            else if (matcher.group("COMMENT") != null) styleClass = "comment";
            else if (matcher.group("VARIABLE") != null) styleClass = "variable";
            else if (matcher.group("ATOM") != null) styleClass = "atom";
            else if (matcher.group("FUNCTOR") != null) styleClass = "functor";
            else if (matcher.group("NUMBER") != null) styleClass = "number";
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = this.codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        this.codeArea.setStyleSpans(0, highlighting);
    }
}