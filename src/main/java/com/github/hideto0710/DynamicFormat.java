package com.github.hideto0710;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DynamicFormat {
    public static DynamicMessage.Builder read(String key, FileInputStream file) throws DynamicFormatException, IOException {
        List<FileDescriptorProto> fps = FileDescriptorSet.parseFrom(file).getFileList();
        HashMap<String, FileDescriptorProto> fileProtoCache = createFileProtoCache(fps);
        Optional<FileDescriptorProto> fdpOpt = fps.stream().filter(f -> f.getName().contains(key)).findAny();
        if (fdpOpt.isPresent()) {
            FileDescriptor fileDescriptor = buildFileDescriptor(fdpOpt.get(), fileProtoCache);
            Optional<Descriptors.Descriptor> descOpt = fileDescriptor.getMessageTypes().stream().filter(t -> t.getName().equals(key)).findAny();
            if (descOpt.isPresent()) {
                return DynamicMessage.newBuilder(descOpt.get());
            }
        }
        throw new DynamicFormatException("");
    }

    private static HashMap<String, FileDescriptorProto> createFileProtoCache(List<FileDescriptorProto> fileProtos) {
        HashMap<String, FileDescriptorProto> result = new HashMap<>();
        fileProtos.forEach(fp -> result.put(fp.getName(), fp));
        return result;
    }

    // see https://stackoverflow.com/questions/53413674/importing-descriptor-with-included-import
    private static FileDescriptor buildFileDescriptor(FileDescriptorProto currentFileProto,
                                               HashMap<String, FileDescriptorProto> fileProtoCache) {
        List<FileDescriptor> dependencyFileDescriptorList = new ArrayList<>();
        currentFileProto.getDependencyList().forEach(dependencyStr -> {
            FileDescriptorProto dependencyFileProto = fileProtoCache.get(dependencyStr);
            FileDescriptor dependencyFileDescriptor = buildFileDescriptor(dependencyFileProto, fileProtoCache);
            dependencyFileDescriptorList.add(dependencyFileDescriptor);
        });
        try {
            return FileDescriptor.buildFrom(currentFileProto, dependencyFileDescriptorList.toArray(new FileDescriptor[0]));
        } catch (DescriptorValidationException e) {
            throw new IllegalStateException("FileDescriptor build fail!", e);
        }
    }
}
