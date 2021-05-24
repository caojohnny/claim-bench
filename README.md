# `claim-bench`

I've actually never done a real Bukkit benchmark before. What usually stands
in the way of doing just tests in general is the fact that you need basically
a full-fledged server in order to be able to consistently (and realistically)
use the API in any sort. Basically I figured why not just start the server 
and run the benchmark and see what happens.

Unsurprisingly, it didn't work.

As it turns out, you apparently need to add the current class path to a system
environment variable in order for JMH to figure out all the class dependencies.
I was really hoping that JMH also had some magic sauce to make forked VMs work,
but it turns out that it doesn't (what forking does is create a separate VM,
but if you do that, you basically need to start another CraftBukkit server,
which doesn't really work out since not only does the benchmark need to be
self-contained, but it also needs to start an entirely different server anyway).
It's unfortunate that you can't fork, because using the same program VM
through configuring `forks(0)` results in odd and often even inaccurate results,
but for the numbers will just have to do (this is why you should swap the order
of your benchmark trials because the ordering is sometimes impacted by the fact
that you aren't forking, if you are forking, this doesn't matter).

For this particular case, a user claimed:

> "Doing look ups in a map or table, and writing data to a storage medium is more performant the smaller and simpler the data is."

And then challenged:

> "You should try keying a map with 1000 players, keying another map with 1000 UUIDS, and comparing how long each look up took. 
> I am speaking from 25+ years of experience not a paper I read."

Well, challenge accepted!

I whipped up a quick project on PaperSpigot 1.13.2 since that was the version I
had most readily available that was somewhat recent.

# Results

The results are in the repo link above. For convenience, I've pasted the output
below:

```
[15:44:01 INFO]: # Run complete. Total time: 00:10:11
[15:44:01 INFO]: REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
[15:44:01 INFO]: why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
[15:44:01 INFO]: experiments, perform baseline and negative tests that provide experimental control, make sure
[15:44:01 INFO]: the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
[15:44:01 INFO]: Do not assume the numbers tell you what you want them to tell.
[15:44:01 INFO]: Benchmark          
[15:44:01 INFO]:   Mode
[15:44:01 INFO]:   Cnt
[15:44:01 INFO]:    Score
[15:44:01 INFO]:  
[15:44:01 INFO]:  Error
[15:44:01 INFO]:   Units
[15:44:01 INFO]: Bench.a_addSmall    
[15:44:01 INFO]:   avgt
[15:44:01 INFO]:     5
[15:44:01 INFO]:   26.489
[15:44:01 INFO]:  ?
[15:44:01 INFO]:  0.363
[15:44:01 INFO]:   ns/op
[15:44:01 INFO]: Bench.a_baselineSmall
[15:44:01 INFO]:   avgt
[15:44:01 INFO]:     5
[15:44:01 INFO]:    7.235
[15:44:01 INFO]:  ?
[15:44:01 INFO]:  0.537
[15:44:01 INFO]:   ns/op
[15:44:01 INFO]: Bench.a_containsSmall
[15:44:01 INFO]:   avgt
[15:44:01 INFO]:     5
[15:44:01 INFO]:   26.405
[15:44:01 INFO]:  ?
[15:44:01 INFO]:  0.327
[15:44:01 INFO]:   ns/op
[15:44:01 INFO]: Bench.b_addLarge    
[15:44:01 INFO]:   avgt
[15:44:01 INFO]:     5
[15:44:01 INFO]:   25.393
[15:44:01 INFO]:  ?
[15:44:01 INFO]:  0.290
[15:44:01 INFO]:   ns/op
[15:44:01 INFO]: Bench.b_baselineLarge
[15:44:01 INFO]:   avgt
[15:44:01 INFO]:     5
[15:44:01 INFO]:    7.157
[15:44:01 INFO]:  ?
[15:44:01 INFO]:  0.060
[15:44:01 INFO]:   ns/op
[15:44:01 INFO]: Bench.b_containsLarge
[15:44:01 INFO]:   avgt
[15:44:01 INFO]:     5
[15:44:01 INFO]:   23.318
[15:44:01 INFO]:  ?
[15:44:01 INFO]:  0.264
[15:44:01 INFO]:   ns/op
```

Essentially, the results show that `containsLarge` and `addLarge` are both
a few nanoseconds faster than the equivalents for the "smaller" data type,
where the larger object is an instance of `CraftPlayer` and the smaller type
an instance of `UUID`. I was actually surprised myself that the results
turned out this way, `CraftPlayer` as a matter of fact uses the same methods
to determine hashes and equality as `UUID`, so one would expect it to be the
same, if not a slight bit slower. However, upon inspecting `CraftPlayer`'s
source, the reason becomes clear: the `CraftPlayer` hashes are cached,
whereas the `UUID` hashes are calculated each call.

As I stated in an earlier response, the overhead of storing objects into a
hashtable is dependent on its hashing algorithm, and to some degree, the
equality check (although that is not as important). While the size of the
data type *might* play a miniscule role in this, it is by and large a
microoptimization. You should not prefer `CraftPlayer` or `UUID` over one
another as a key because of performance reasons. Use what *makes sense*. An
object kept in a hashtable is already present on the heap, so creating a
new entry to store a reference to it will not be impacted by the size of the
object, they are all addressed exactly the same way.

# Compiling

``` shell
git clone https://github.com/caojohnny/claim-bench.git
cd claim-bench
mvn clean install
```

Requires Spigot 1.13.2.

You may consider heading to the releases pages and using a pre-built binary
if you so wish.

# Usage

Begin a test server with this plugin. Upon startup, the benchmark will run
for about 10 minutes or so and the server will complete startup once it has
finished. You may then copy the log file output.

# Credits

Built with [IntelliJ IDEA](https://www.jetbrains.com/idea/)

Uses [JMH](https://openjdk.java.net/projects/code-tools/jmh/)
