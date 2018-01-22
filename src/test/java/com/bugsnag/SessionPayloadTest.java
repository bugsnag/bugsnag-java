package com.bugsnag;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SessionPayloadTest {

    @Test
    public void testJsonSerialisation() throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new SessionPayload());
        assertNotNull(json);
    }

}