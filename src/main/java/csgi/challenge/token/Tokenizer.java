package csgi.challenge.token;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.*;

import java.util.concurrent.atomic.AtomicReference;


public class Tokenizer implements Iterator<Token> {
	private final StreamTokenizer tokenizer;
	private final AtomicReference<Token> currToken;
	private static final Set<Character> SET_SPECIAL_CHARS = new HashSet<>(List.of('.', ',', ':', ';', '-', '_', '?', '!', '"', '#', '$', '%'
			, '&', '/', '(', ')', '=', '»', '«', '@', '£', '§', '{', '[', ']', '}', '´', '`', '+', '*', '¨', 'º', 'ª'
			, '~', '^', '\\', '/', '€', '<', '>'));

	public Tokenizer(Reader reader) throws IOException {
		this.tokenizer = new StreamTokenizer(reader);
		for (char specialChar : SET_SPECIAL_CHARS) {
			this.tokenizer.ordinaryChar(specialChar);
		}
		this.currToken = new AtomicReference<>(getNextToken());
	}

	private TokenImpl getNextToken() throws IOException {

		int i = tokenizer.nextToken();
		TokenType type = getType(i, tokenizer.sval);
		String value = type == TokenType.SPECIAL ? String.valueOf((char) i) : tokenizer.sval;
		return new TokenImpl(value, type);
	}

	public boolean hasNext() {

		return currToken.get().type() != TokenType.EOF;
	}

	public Token next() {
		if (!hasNext()) {
			throw new NoSuchElementException(new EOFException());
		}

		try {
			return currToken.getAndSet(getNextToken());
		} catch (IOException e) {
			throw new NoSuchElementException(e);
		}

	}

	public static TokenType getType(int type, String value) {
		switch (type) {
			case StreamTokenizer.TT_NUMBER -> {
				return TokenType.NUMBER;
			}
			case StreamTokenizer.TT_WORD -> {
				return SET_SPECIAL_CHARS.contains(value.charAt(0)) ? TokenType.SPECIAL : TokenType.WORD;
			}
			case StreamTokenizer.TT_EOL -> {
				return TokenType.EOL;
			}
			case StreamTokenizer.TT_EOF -> {
				return TokenType.EOF;
			}
			default -> {
				return TokenType.SPECIAL;
			}
		}
	}
}