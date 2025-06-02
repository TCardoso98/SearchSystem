package csgi.challenge.token;

public class TokenImpl implements Token {
	private final String value;
	private final TokenType type;

	public TokenImpl(String value, TokenType type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public TokenType type() {
		return type;
	}
}