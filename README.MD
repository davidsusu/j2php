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
| Basic concepts, project structure  | In progress | - |
| Basic package/class structure | In progress | - |
| Expressions, control structures | In progress | - |
| Code blocks, scoping, closures | In progress | Variable postfixes |
| Method overloading | Soon | Method postfixes, default variant |
| Unicode strings | Soon | mb_\* methods |
| Configurable formatting | Soon | - |
| Nested types | Later | Reflexion |
| Default methods | Later | Inherit from java\\lang\\Object |
| Advanced Java8 features | Later | - |
| Custom behaviors (collections etc.) | Later | ... |
| Advanced custom (generators etc.) | Later | ... |
| Basic threading | Later | PThreads |
| Configurable PHP settings | Later | - |
| Build tool integration | Later | Gradle plugin |
| Standard lib, deps | Later | - |
| Docblocks | Later | - |
| Advanced concurrency | Not planned | - |
| ... | ... | ... 
