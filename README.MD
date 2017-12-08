## j2php ##
### Java to PHP transcompiler ###

This transcompiler does not try to emulate the JVM,
but produces a human friendly output close to the original Java source
(unlike to LLVM based solutions).

It supports any Java versions up to Java 9 as source, and produces PHP 7 code.

This is an experimental project in its early stage. Do not use in production.

**Current status:**

| Feature | Status | Idea |
|---------|--------|------|
| Basic package/class structure | In progress | - |
| Code blocks, scoping | Soon | Variable postfixes |
| Expressions, control structures | Soon | - |
| Method overloading | Soon | Method postfixes, default variant |
| Unicode strings | Soon | mb_\* methods |
| Configurable formatting | Soon | - |
| Inner types | Later | Reflexion |
| Default methods | Later | Inherit from a generic JavaObject |
| Custom behaviors (collections etc.) | Later | ... |
| Advanced custom (generators etc.) | Later | ... |
| Basic threading | Later | PThreads |
| Configurable PHP settings | Later | - |
| Build tool integration | Later | Gradle plugin |
| Standard lib | Later | - |
| Advanced concurrency | Not planned | - |
| Docblocks | Later | - |
| ... | ... | ... 
