# SearchSystem

A flexible and extensible text processing system that supports multiple work modes for analyzing words and text data.

## How to Get JAR

You can obtain the JAR file in two ways:

1. **Download Release**
    - Go to the releases section of this repository
    - Download the latest `SearchSystem.jar` file

2. **Build from Source**

  ```bash
   mvn clean package
   ```

The JAR will be generated in the `target` directory.

## How to Run JAR

To run the SearchSystem, use the following command:

```bash
java -jar SearchSystem.jar -w [WORK_MODE] -f [input-files]
```

Example:

```shell script
java -jar SearchSystem.jar -w START_WITH_M_COUNTER -f 5letterWords1.txt,5letterWords2.txt
java -jar SearchSystem.jar -w LONGER_THAN_5 -f 5letterWords.txt
```

For more help run

```shell script
java -jar SearchSystem.jar -h
```

## Available Work Modes

The system currently supports two work modes:

### 1. START_WITH_M_COUNTER

- **Purpose**: Counts all words that start with the letter 'M' (case-insensitive)
- **Implementation**: Uses `WordCounterWorker`
- **Output**: Returns an integer count of matching words
- **Use case**: Statistical analysis of words beginning with specific letters

### 2. LONGER_THAN_5

- **Purpose**: Filters and collects all words that are longer than 5 characters
- **Implementation**: Uses `WordLengthWorker`
- **Output**: Returns an array of strings containing all qualifying words
- **Use case**: Text analysis for longer words or filtering by word length

## Extending Functionality

The SearchSystem is designed to be easily extensible. To add new functionalities, you only need to:

1. **Extend WorkerAbstract**: Create a new class that extends `WorkerAbstract<T, R>`
    - `T` is the input type (typically `Token`)
    - `R` is the return type for your results

2. **Add to WorkMode enum**: Add your new functionality to the `WorkMode` enum

The system will automatically recognize and support your new work mode.

## Input Format

The system processes text files where words can be:

- Separated by commas
- Separated by whitespace
- On separate lines

Example input file (`5letterWords.txt`):

```
palavra, malibu
```

## Architecture

The system uses a worker-based architecture where:

- **Worker interface**: Defines the contract for processing operations
- **WorkerAbstract**: Provides base implementation for common functionality
- **WorkMode enum**: Maps work modes to their corresponding worker implementations
- **Token-based processing**: Text is tokenized and processed asynchronously

This design ensures high extensibility and maintainability while supporting concurrent processing.