# schema2descriptor

```bash
protoc \
    --include_imports \
    --descriptor_set_out=desc/ABCD.desc \
    -I examples/ \
    ABCD.proto
```
```bash
mvn test
```

## Links
- [Using Dynamic Messages in Protocol Buffers in Scala](https://codeburst.io/using-dynamic-messages-in-protocol-buffers-in-scala-9fda4f0efcb3)
- [Protocol Buffers - Techniques#Self-describing Messages](https://developers.google.com/protocol-buffers/docs/techniques#self-description)
- [Stack Overflow - Importing descriptor with included import](https://stackoverflow.com/questions/53413674/importing-descriptor-with-included-import)
