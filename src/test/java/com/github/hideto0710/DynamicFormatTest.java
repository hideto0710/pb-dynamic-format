package com.github.hideto0710;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.HashMap;

public class DynamicFormatTest {
    @Test
    public void testRead() throws Exception {
        String key = "ABCD";
        String json = "{\"query\":\"id=21\",\"page_number\":1,\"result_per_page\":10,\"dep\":{\"method\":\"GET\"}}";
        ObjectMapper mapper = new ObjectMapper();
        HashMap map = mapper.readValue(json, HashMap.class);

        FileInputStream file = new FileInputStream("desc/" + key + ".desc");
        DynamicMessage.Builder builder = DynamicFormat.read(key, file);
        JsonFormat.parser().merge(json, builder);

        // debug print
        System.out.println(builder.build());

        DynamicMessage msg = builder.build();
        msg.getAllFields().forEach((fd, v) -> {
            // for key=dep
            if (v.getClass().getCanonicalName().equals("com.google.protobuf.DynamicMessage")) {
                // for google.protobuf.StringValue
                assertEquals(
                        ((DynamicMessage) v).getAllFields().values().toArray()[0].toString().trim(),
                        "value: \"GET\""
                );
            } else {
                assertEquals(map.get(fd.getName()), v);
            }
        });
    }
}
