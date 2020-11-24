package by.mark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Model {

    public Optional<List<String>> load(Path path) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            return Optional.of(lines);
        } catch (IOException e) {
            return Optional.empty();
        }
    }


    public ResultSet analyze(List<String> list) {
        List<String> tokens = TextParser.tokens(list);
        ResultSet resultSet = new ResultSet(this);
        resultSet.process(tokens);
        return resultSet;
    }
}
