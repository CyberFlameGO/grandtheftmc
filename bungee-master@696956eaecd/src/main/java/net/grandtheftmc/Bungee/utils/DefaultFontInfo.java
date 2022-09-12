package net.grandtheftmc.Bungee.utils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Adam on 05/06/2017.
 */
public enum DefaultFontInfo {

    A_UPPER('A', 5),
    A_LOWER('a', 5),
    B_UPPER('B', 5),
    B_LOWER('b', 5),
    C_UPPER('C', 5),
    C_LOWER('c', 5),
    D_UPPER('D', 5),
    D_LOWER('d', 5),
    E_UPPER('E', 5),
    E_LOWER('e', 5),
    F_UPPER('F', 5),
    F_LOWER('f', 4),
    G_UPPER('G', 5),
    G_LOWER('g', 5),
    H_UPPER('H', 5),
    H_LOWER('h', 5),
    I_UPPER('I', 3),
    I_LOWER('i', 1),
    J_UPPER('J', 5),
    J_LOWER('j', 5),
    K_UPPER('K', 5),
    K_LOWER('k', 4),
    L_UPPER('L', 5),
    L_LOWER('l', 1),
    M_UPPER('M', 5),
    M_LOWER('m', 5),
    N_UPPER('N', 5),
    N_LOWER('n', 5),
    O_UPPER('O', 5),
    O_LOWER('o', 5),
    P_UPPER('P', 5),
    P_LOWER('p', 5),
    Q_UPPER('Q', 5),
    Q_LOWER('q', 5),
    R_UPPER('R', 5),
    R_LOWER('r', 5),
    S_UPPER('S', 5),
    S_LOWER('s', 5),
    T_UPPER('T', 5),
    T_LOWER('t', 4),
    U_UPPER('U', 5),
    U_LOWER('u', 5),
    V_UPPER('V', 5),
    V_LOWER('v', 5),
    W_UPPER('W', 5),
    W_LOWER('w', 5),
    X_UPPER('X', 5),
    X_LOWER('x', 5),
    Y_UPPER('Y', 5),
    Y_LOWER('y', 5),
    Z_UPPER('Z', 5),
    Z_LOWER('z', 5),
    NUM_1('1', 5),
    NUM_2('2', 5),
    NUM_3('3', 5),
    NUM_4('4', 5),
    NUM_5('5', 5),
    NUM_6('6', 5),
    NUM_7('7', 5),
    NUM_8('8', 5),
    NUM_9('9', 5),
    NUM_0('0', 5),
    EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6),
    NUM_SIGN('#', 5),
    DOLLAR_SIGN('$', 5),
    PERCENT('%', 5),
    UP_ARROW('^', 5),
    AMPERSAND('&', 5),
    ASTERISK('*', 5),
    LEFT_PARENTHESIS('(', 4),
    RIGHT_PERENTHESIS(')', 4),
    MINUS('-', 5),
    UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5),
    EQUALS_SIGN('=', 5),
    LEFT_CURL_BRACE('{', 4),
    RIGHT_CURL_BRACE('}', 4),
    LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3),
    COLON(':', 1),
    SEMI_COLON(';', 1),
    DOUBLE_QUOTE('"', 3),
    SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4),
    RIGHT_ARROW('>', 4),
    QUESTION_MARK('?', 5),
    SLASH('/', 5),
    BACK_SLASH('\\', 5),
    LINE('|', 1),
    TILDE('~', 5),
    TICK('`', 2),
    PERIOD('.', 1),
    COMMA(',', 1),
    SPACE(' ', 3),
    DEFAULT('a', 4);

    private final char character;
    private final int length;

    DefaultFontInfo(char character, int length) {
        this.character = character;
        this.length = length;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }

    public int getBoldLength() {
        if (this == SPACE) return this.length;
        return this.length + 1;
    }

    public static DefaultFontInfo getDefaultFontInfo(char c) {
        Optional<DefaultFontInfo> defaultFontInfo = Arrays.stream(values()).filter(d -> d.character == c).findFirst();
        return defaultFontInfo.orElse(DEFAULT);
    }
}
