package csgi.challenge.token;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

public class Tokenizer {
   private final StreamTokenizer tokenizer;
   private int currToken;
   private static final char[] SPECIAL_CHARS = new char[]{'.', ',', ':', ';', '-', '_', '?', '!', '"', '#', '$', '%', '&', '/', '(', ')', '=', '»', '«', '@', '£', '§', '{', '[', ']', '}', '´', '`', '+', '*', '¨', 'º', 'ª', '~', '^', '\\', '/', '€', '<', '>'};

   public Tokenizer(Reader reader) throws IOException {
      this.tokenizer = new StreamTokenizer(reader);

      for(char specialChar : SPECIAL_CHARS) {
         this.tokenizer.quoteChar(specialChar);
      }

      this.currToken = this.tokenizer.nextToken();
   }

   public boolean hasNext() {
      return this.currToken != StreamTokenizer.TT_EOF;
   }

   public Token next() throws IOException {
      final String result = this.tokenizer.sval;
      final boolean isWord = this.currToken == StreamTokenizer.TT_WORD;
      this.currToken = this.tokenizer.nextToken();
      return new Token() {
         public String value() {
            return result;
         }

         public boolean isWord() {
            return isWord;
         }
      };
   }
}