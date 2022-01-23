# cs-logparser
A log parser that outputs results to a file based HSQL database

### Disclaimer : I spent more time than allotted : around 3 hours spread on two evenings. I don't consider that what I'm delivering is complete, I had to stop because I was way over time. What took me the most time was testing and dealing with the database part. Below are details.

## What has been done :
The log parser matches most of the acceptance criteria described in the evaluation instructions :
* It should be able to handle a log file that respects the given format, including a large one (the file is consumed as a stream, so memory usage should stay reasonable)
* It will parse the log files into events that will be sent to a file-based HSQL database
* It is a multi-threaded solution - the database client will write the events in separate threads from the main thread which will host the parsing process
* There are some tests

## What needs improvement
Some acceptance criterias are not matched :
* Error management is not very good at the time. For example, I don't really know what happens if I feed my app a corrupted file
* Logging is rudimentary
* The testing is lackluster, I didn't dare to check the coverage. I spent most of my testing efforts checking that the parser could handle very large files

## How to use : 

These steps apply to Windows machines. All commands are made from the root folder of the project.

To build the jar and run the tests : 

`gradlew build`

To run the jar against a sample log file

`java -jar build\libs\logparser-1.0-SNAPSHOT.jar src\test\resources\shortFile.txt` 
