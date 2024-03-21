# CryptoGUI
### Features
Key: Enter the key you wish to encrypt your file with.  
Input: Select an input file or directory.  
Encrypt: Encrypts your selected file or all files in the directory with AES encryption, outputting the result to `<filename>.enc.<fileextension>`.  
Decrypt: Decrypts your selected file or all files in the directory, outputting the result to `<filename>.<fileextension>`.  
Logging: The program logs its operations in the `log.txt` file.
### Requirements
Make sure that you have the Java Runtime Environment installed: `java -version`.
### Running the Program
Download the `CryptoGUI.jar` file and double click to run. If you are on Linux you may have to give the file permission to execute: `$ chmod +x CryptoGui.jar`.
### Building the Program
If you wish to build the jar yourself download the `*.java` files and compile: `javac CryptoGUI.java`. Download the `CryptoGUI.mf` file and build: `jar cvfm  <jarfilename>.jar CryptoGUI.mf *.class`.
