## j2php ##
### Java to PHP transcompiler ###

This transcompiler does not try to emulate the JVM,
but produces a human friendly output close to the original Java source
(unlike to LLVM based solutions).

It supports any Java versions up to Java 9 as source, and produces PHP 5.6 code.

This is an experimental project in its early stage. Do not use in production.

**Current status:**

| Feature | Status |
|---------|--------|
| Basic package/class structure | In progress |
| Code blocks, scoping | Soon |
| Expressions, control structures | Soon |
| Method overloading | Soon |
| Docblocks | Soon |
| Unicode strings | Soon |
| Inner types | Later |
| Custom behaviors (collections etc.) | Later |
| Advanced custom (generators etc.) | Later |
| Basic threading | Later (pthreads) |
| Configurable formatting | Later |
| Configurable PHP settings | Later |
| Build tool integration | Later (gradle) |
| Standard lib | Later |
| Advanced concurrency | Not planned |
| ... | ... |
