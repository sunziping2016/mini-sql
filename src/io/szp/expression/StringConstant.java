package io.szp.expression;

import io.szp.exception.SQLException;
import io.szp.exception.SyntaxException;

public class StringConstant implements Expression {
    private String literal;

    public StringConstant(String raw) {
        literal = parseString(raw);
    }

    public static String parseString(String raw) {
        StringBuilder parsed = new StringBuilder();
        int begin = 1, end = raw.length() - 1;
        while (begin != end) {
            char new_char;
            if (raw.charAt(begin++) != '\\')
                new_char = raw.charAt(begin - 1);
            else if (begin == end)
                throw new SyntaxException("Unexpected ending of escape sequence");
            else {
                switch (raw.charAt(begin++)) {
                    case 'B': new_char = '\b'; break;
                    case 'F': new_char = '\f'; break;
                    case 'N': new_char = '\n'; break;
                    case 'R': new_char = '\r'; break;
                    case 'T': new_char = '\t'; break;
                    case '\'': case '\"': case '\\':
                        new_char = raw.charAt(begin - 1);
                        break;
                    case 'U':
                        if (begin + 4 > end)
                            throw new SyntaxException("Unexpected ending of escape sequence");
                        try {
                            new_char = (char) Integer.parseInt(raw.substring(begin, begin + 4), 16);
                            begin += 4;
                        } catch (NumberFormatException e) {
                            throw new SyntaxException("Invalid unicode character");
                        }
                        break;
                    default:
                        throw new SyntaxException("Unknown escape sequence");
                }
            }
            parsed.append(new_char);
        }
        return parsed.toString();
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        return ExpressionType.STRING;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return literal;
    }
}
