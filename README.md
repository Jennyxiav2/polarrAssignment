# Polarr Assignment

For this assignment I decided to package all the sections into a demo Android app.  The app has 5 gradle modules, the main app module, the 4 section modules, and a benchmarking module.  I did this to hopefully make it easier to see the functionality of the exercises and also to put the exercises into an Android context.  I tried to keep most of the code together in a couple classes.  In a couple cases there is some glue code in a `ViewModel` that connects the demo UI to the implementations.  Also, I mostly used kotlin instead of java.

## Main App
The main app depends on `section1`, `section2`, and `section3`; it is just a single `Activity` with 3 buttons that each start a demo `Activity` for their respective sections.  I did not get to making a demo for the SDK in section4.

## Section1 - Trees
For section one there is some UI that lets you specify the parameters for generating the random tree structure.  It is rudimentary, but suffices for a demo.  

The random tree generation function ensures that the tree has the exact depth passed in and has at most the max number of children passed in.  It does this in two steps -- step one creates a single path of the proper depth, step two starts at the root node, visits each of the generated nodes and randomly generates subtrees.  I decided to ensure that the passed in depth was achieved because that's the way the problem read to me and it seemed like too much variation to just randomly generate entire trees.  N.B. passing in a value less than 1 is treated as 1, and passing in very large numbers for depth or number of children will lockup or crash the app.

For serialization I define a recursive function that does a depth-first pass of the tree.  As it visits each node, it serializes out the value (an Int) and then serializes all children recursively.  Before serializing each child, a marker byte is written that designates the following value as a child.  When all children are serialized another marker byte is written to the stream to indicate that this subtree is done.  

Deserialization is also a recursive function.  When deserializing a root node is created and passed in along with the `InputStream`.  A value is read from the stream and assigned to the node, then the marker byte is read.  If the marker indicates a child then a new `Node` is created, added as a child to the current node and has it's parent pointer assigned.  The function is called recursively on this new node until no more child markers are read.
Encryption and decryption is accomplished by wrapping the `InputStream` and `OutputStream`s in custom streams that "de/encrypt" each byte.  

The main issue I'm concerned about with this code is that it relies on the stack to handle recursive structures and so is prone to stack overflow issues for large trees.  A mitigation would be to convert the recursion to iteration.  Test cases should cover passing in different types of values that catch common edge cases.  For the generating functions passing in 0, negative numbers and possibly very large positive and negative numbers.  If this code could potentially be used in a multi-threaded environment (almost guaranteed), tests to run operations in parallel on multiple threads should be done, ensuring that invariants are not violated.

## Section 2 - Histograms

Performance for single vs multi-threaded was surprisingly close, I get about a 1.5x to 2x speedup, both on device and emulator.  There are some optimizations that could probably be made with my multi-threaded implementation.  Currently I wait until all threads are finished before aggregating results, that could be done as threads are finishing and that might shave off a little time.  

The OpenGL implementation, by contrast, is consistently >10x faster than single threaded.  There are also some optimizations that could be made there too.  I'm currently making 3 passes over the image, one for each channel, that could be done with one pass I think, either by processing the vertex buffer one element at a time and using the vertex id (not sure this is available, actually) to determine the channel (id % 3 == channel), or maybe using a geometry shader to output multiple vertices per input pixel.  That way I could write to a 256x3 texture instead of a 256x1.

## Section 3 - State Machines

I was unfamiliar with this state machine setup, but I got something that seems reasonable.  I created an `Intersection` class that contains three `Context`s, one for the car sensor on EW, one for EW state and one for NS state.  I added listeners to the `Context` class so that when one context changes state, I can react and trigger other actions.

## Section 4 - SDK

Of all the sections, I feel like section 4 could take the most time and effort.  Productionizing a feature or some code involves a lot incidental work.  From documentation to extra testing to examples to error detection and error handling etc.  This one feels like it was a little difficult to demonstrate in the demo environment, but if you think of it as a rough outline, it works.

For hiding the implementation, I created an `interface` and exposed a way to get an instance of that interface and implemented the interface in a private class.  I setup `proguard` rules to obfuscate all code except the interface definition and other public classes (Matrix2x2).  

For the benchmarking script I used a new feature of Instumented Tests, expressly for benchmarking (https://developer.android.com/studio/profile/benchmark).  You write an instrumentation test like normal, and use a `BenchmarkRule` to run a block of code.  The framework handles warm up and timing of repeated runs of the code block.  It runs like a standard test and outputs results to the console. There is also `Debug.startMethodTracing("sample")` that will give you fine-grained information about all function calls and CPU usage, although I did not get to implementing that.  There are also rich tools for interactively monitoring performance in Android Studio, but that is not scriptable.  Benchmarking code is in the `benchmark` module.  Right now it only benchmarks section4, but could also be used for the other modules.  Since these are standard tests they can also be run on device farms to collect real-world, device specific performance data.

For securing the SDK, I did not do anything other than proguard obfuscation.  It is possible to encrypt dalvik bytecode and decrypt it before loading, but that is rather involved.  One thing I do like to do is encrypt any configuration data you ship with a library and encrypt any data the library persists.

Thanks for taking the time to review my assignment, please let me know if you have any questions.

Nash


