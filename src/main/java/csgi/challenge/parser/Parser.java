package csgi.challenge.parser;

import csgi.challenge.token.Token;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public interface Parser extends AutoCloseable {


	Token getToken();

	boolean hasNext();
}