## Micro-batching in Apache Storm (example)

Micro-batching is a technique of collecting data from stream, packing them into chunks and 
delivery them to some external applications that supports batching input. Using micro-batching in 
Apache Storm makes sense for integration with external systems from performance perspective.

Here's example code that introduces micro-batching to bolt, using Storm's tick-tuple feature.

Broader description is on [Micro-batching in Apache Storm](http://notes.vault7
.net/apache/storm/microbatching-in-apache-storm/) page.