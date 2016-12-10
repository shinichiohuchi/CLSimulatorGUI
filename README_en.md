#CLSimulatorGUI Manual

Version: 2.0  
Author: Shinichi Oouchi (大内 真一)  
Since: 2016/11/10  
Last Chagned: 2016/11/10  
Execution File: CLSG.jar  
Operation  
* OS:  
  Windows10 Pro 64bit  
  LinuxBean12.04
* Processor:  
  2.00GHz Intel Core i7-3667U  
* Memory:  
  8GB RAM  
* Java Version:  
  1.8.0-111  

##Overview
CLSimulatorGUI is tool to calculate Combinator Logic Code(CLCode);

This tool has S, K, I, B, C Combinators by default.  
This tool could output result of calculation as text file  
and add new combinators to after default Combinators.  
But couldn't override default combinators.

##Usage
###Calculation Combinators
1. Double click the CLSG.jar or type "java -jar CLSG.jar" on terminal.
2. Type CLCode to text field on top.  
and push "Add" button or press ENTER Key.  
Result will be displayed to Result Tab on center.
3. Select "Open..." of "File" Menu if you want to input from text files.  
CLCodeは1行に1コードまでで記述するようにしてください。  
Line is ignored that starts with '#';
4. Select "Save As..." of "File" Menu if you want to output result of calculation
to text file. Result of selected tab is saved to text file.

###Add Combinators
Select "Open Definition..." of "File" Menu.

###Edit Combinators
1. Select "Edit Definition..." of "File" Menu.
2. Type format to left text area.
3. Push "Update" button or press Ctrl + ENTER key.
4. "OK" button will be clickable if format is correct.  
Check your format if you couldn't click "OK" button.
5. Check "Defined Combinators" of right of main window.
